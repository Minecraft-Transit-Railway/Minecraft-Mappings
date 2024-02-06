package org.mtr.mapping.registry;

import io.netty.buffer.Unpooled;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.util.InputMappings;
import net.minecraft.item.ItemModelsProperties;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.*;
import org.mtr.mapping.mapper.BlockEntityExtension;
import org.mtr.mapping.mapper.BlockEntityRenderer;
import org.mtr.mapping.mapper.EntityExtension;
import org.mtr.mapping.mapper.EntityRenderer;
import org.mtr.mapping.tool.DummyClass;
import org.mtr.mapping.tool.PacketBufferSender;

import javax.annotation.Nullable;
import java.util.function.Function;

public final class RegistryClient extends DummyClass {

	public static Function<World, ? extends EntityExtension> worldRenderingEntity;
	private final Registry registry;

	public RegistryClient(Registry registry) {
		this.registry = registry;
	}

	@MappedMethod
	public void init() {
		MinecraftForge.EVENT_BUS.register(MainEventBusClient.class);
		FMLJavaModLoadingContext.get().getModEventBus().register(ModEventBusClient.class);
	}

	@MappedMethod
	public <T extends BlockEntityTypeRegistryObject<U>, U extends BlockEntityExtension> void registerBlockEntityRenderer(T blockEntityType, Function<BlockEntityRenderer.Argument, BlockEntityRenderer<U>> rendererInstance) {
		ModEventBusClient.CLIENT_OBJECTS_TO_REGISTER.add(() -> ClientRegistry.bindTileEntityRenderer(blockEntityType.get().data, dispatcher -> rendererInstance.apply(new BlockEntityRenderer.Argument(dispatcher))));
	}

	@MappedMethod
	public <T extends EntityTypeRegistryObject<U>, U extends EntityExtension> void registerEntityRenderer(T entityType, Function<EntityRenderer.Argument, EntityRenderer<U>> rendererInstance) {
		ModEventBusClient.CLIENT_OBJECTS_TO_REGISTER.add(() -> RenderingRegistry.registerEntityRenderingHandler(entityType.get().data, dispatcher -> rendererInstance.apply(new EntityRenderer.Argument(dispatcher))));
	}

	@MappedMethod
	public void registerBlockRenderType(RenderLayer renderLayer, BlockRegistryObject block) {
		ModEventBusClient.CLIENT_OBJECTS_TO_REGISTER.add(() -> RenderTypeLookup.setRenderLayer(block.get().data, renderLayer.data));
	}

	@MappedMethod
	public KeyBinding registerKeyBinding(String translationKey, int key, String categoryKey) {
		final net.minecraft.client.settings.KeyBinding keyBinding = new net.minecraft.client.settings.KeyBinding(translationKey, InputMappings.Type.KEYSYM, key, categoryKey);
		ModEventBusClient.CLIENT_OBJECTS_TO_REGISTER.add(() -> ClientRegistry.registerKeyBinding(keyBinding));
		return new KeyBinding(keyBinding);
	}

	@MappedMethod
	public void registerBlockColors(BlockColorProvider blockColorProvider, BlockRegistryObject... blocks) {
		ModEventBusClient.BLOCK_COLORS.add(event -> {
			final net.minecraft.block.Block[] newBlocks = new net.minecraft.block.Block[blocks.length];
			for (int i = 0; i < blocks.length; i++) {
				newBlocks[i] = blocks[i].get().data;
			}
			event.getBlockColors().register((blockState, blockRenderView, blockPos, tintIndex) -> blockColorProvider.getColor2(new BlockState(blockState), blockRenderView == null ? null : new BlockRenderView(blockRenderView), blockPos == null ? null : new BlockPos(blockPos), tintIndex), newBlocks);
		});
	}

	@MappedMethod
	public void registerItemColors(ItemColorProvider itemColorProvider, ItemRegistryObject... items) {
		ModEventBusClient.ITEM_COLORS.add(event -> {
			final net.minecraft.item.Item[] newItems = new net.minecraft.item.Item[items.length];
			for (int i = 0; i < items.length; i++) {
				newItems[i] = items[i].get().data;
			}
			event.getItemColors().register(((itemStack, tintIndex) -> itemColorProvider.getColor2(new ItemStack(itemStack), tintIndex)), newItems);
		});
	}

	@MappedMethod
	public void registerItemModelPredicate(ItemRegistryObject item, Identifier identifier, ModelPredicateProvider modelPredicateProvider) {
		ModEventBusClient.CLIENT_OBJECTS_TO_REGISTER_QUEUED.add(() -> ItemModelsProperties.register(item.get().data, identifier.data, (itemStack, clientWorld, livingEntity) -> modelPredicateProvider.call(new ItemStack(itemStack), clientWorld == null ? null : new ClientWorld(clientWorld), livingEntity == null ? null : new LivingEntity(livingEntity))));
	}

	@MappedMethod
	public void setupPackets(Identifier identifier) {
	}

	@MappedMethod
	public <T extends PacketHandler> void sendPacketToServer(T data) {
		if (registry.simpleChannel != null) {
			final PacketBufferSender packetBufferSender = new PacketBufferSender(Unpooled::buffer);
			packetBufferSender.writeString(data.getClass().getName());
			data.write(packetBufferSender);
			packetBufferSender.send(byteBuf -> registry.simpleChannel.sendToServer(new Registry.PacketObject(byteBuf)));
		}
	}

	@FunctionalInterface
	public interface ModelPredicateProvider {
		@MappedMethod
		float call(ItemStack itemStack, @Nullable ClientWorld clientWorld, @Nullable LivingEntity livingEntity);
	}
}
