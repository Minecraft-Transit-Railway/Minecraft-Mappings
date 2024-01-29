package org.mtr.mapping.registry;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.ClientRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.*;
import org.mtr.mapping.mapper.BlockEntityExtension;
import org.mtr.mapping.mapper.BlockEntityRenderer;
import org.mtr.mapping.mapper.EntityExtension;
import org.mtr.mapping.mapper.EntityRenderer;
import org.mtr.mapping.tool.DummyClass;

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
		ModEventBusClient.BLOCK_ENTITY_RENDERERS.add(event -> event.registerBlockEntityRenderer(blockEntityType.get().data, context -> rendererInstance.apply(new BlockEntityRenderer.Argument(context))));
	}

	@MappedMethod
	public <T extends EntityTypeRegistryObject<U>, U extends EntityExtension> void registerEntityRenderer(T entityType, Function<EntityRenderer.Argument, EntityRenderer<U>> rendererInstance) {
		ModEventBusClient.BLOCK_ENTITY_RENDERERS.add(event -> event.registerEntityRenderer(entityType.get().data, dispatcher -> rendererInstance.apply(new EntityRenderer.Argument(dispatcher))));
	}

	@MappedMethod
	public void registerBlockRenderType(RenderLayer renderLayer, BlockRegistryObject block) {
		ModEventBusClient.CLIENT_OBJECTS_TO_REGISTER.add(() -> ItemBlockRenderTypes.setRenderLayer(block.get().data, renderLayer.data));
	}

	@MappedMethod
	public KeyBinding registerKeyBinding(String translationKey, int key, String categoryKey) {
		final KeyMapping keyBinding = new KeyMapping(translationKey, InputConstants.Type.KEYSYM, key, categoryKey);
		ModEventBusClient.CLIENT_OBJECTS_TO_REGISTER.add(() -> ClientRegistry.registerKeyBinding(keyBinding));
		return new KeyBinding(keyBinding);
	}

	@MappedMethod
	public void registerBlockColors(BlockColorProvider blockColorProvider, BlockRegistryObject... blocks) {
		ModEventBusClient.BLOCK_COLORS.add(event -> {
			final net.minecraft.world.level.block.Block[] newBlocks = new Block[blocks.length];
			for (int i = 0; i < blocks.length; i++) {
				newBlocks[i] = blocks[i].get().data;
			}
			event.getBlockColors().register((blockState, blockRenderView, blockPos, tintIndex) -> blockColorProvider.getColor2(new BlockState(blockState), blockRenderView == null ? null : new BlockRenderView(blockRenderView), blockPos == null ? null : new BlockPos(blockPos), tintIndex), newBlocks);
		});
	}

	@MappedMethod
	public void registerItemColors(ItemColorProvider itemColorProvider, ItemRegistryObject... items) {
		ModEventBusClient.ITEM_COLORS.add(event -> {
			final net.minecraft.world.item.Item[] newItems = new net.minecraft.world.item.Item[items.length];
			for (int i = 0; i < items.length; i++) {
				newItems[i] = items[i].get().data;
			}
			event.getItemColors().register(((itemStack, tintIndex) -> itemColorProvider.getColor2(new ItemStack(itemStack), tintIndex)), newItems);
		});
	}

	@MappedMethod
	public void registerItemModelPredicate(ItemRegistryObject item, Identifier identifier, ModelPredicateProvider modelPredicateProvider) {
		ModEventBusClient.CLIENT_OBJECTS_TO_REGISTER_QUEUED.add(() -> ItemProperties.register(item.get().data, identifier.data, (itemStack, clientWorld, livingEntity, seed) -> modelPredicateProvider.call(new ItemStack(itemStack), clientWorld == null ? null : new ClientWorld(clientWorld), livingEntity == null ? null : new LivingEntity(livingEntity))));
	}

	@MappedMethod
	public void setupPackets(Identifier identifier) {
	}

	@MappedMethod
	public <T extends PacketHandler> void sendPacketToServer(T data) {
		if (registry.simpleChannel != null) {
			registry.simpleChannel.sendToServer(data);
		}
	}

	@FunctionalInterface
	public interface ModelPredicateProvider {
		@MappedMethod
		float call(ItemStack itemStack, @Nullable ClientWorld clientWorld, @Nullable LivingEntity livingEntity);
	}
}
