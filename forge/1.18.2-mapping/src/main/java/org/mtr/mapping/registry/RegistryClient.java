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
import org.mtr.mapping.tool.DummyClass;

import javax.annotation.Nullable;
import java.util.function.Function;

public final class RegistryClient extends DummyClass {

	@MappedMethod
	public static void init() {
		MinecraftForge.EVENT_BUS.register(MainEventBusClient.class);
		FMLJavaModLoadingContext.get().getModEventBus().register(ModEventBusClient.class);
	}

	@MappedMethod
	public static <T extends BlockEntityTypeRegistryObject<U>, U extends BlockEntityExtension> void registerBlockEntityRenderer(T blockEntityType, Function<BlockEntityRendererArgument, BlockEntityRenderer<U>> rendererInstance) {
		ModEventBusClient.BLOCK_ENTITY_RENDERERS.add(event -> event.registerBlockEntityRenderer(blockEntityType.get().data, context -> rendererInstance.apply(new BlockEntityRendererArgument(context))));
	}

	@MappedMethod
	public static void registerBlockRenderType(RenderLayer renderLayer, BlockRegistryObject block) {
		ModEventBusClient.CLIENT_OBJECTS_TO_REGISTER.add(() -> ItemBlockRenderTypes.setRenderLayer(block.get().data, renderLayer.data));
	}

	@MappedMethod
	public static KeyBinding registerKeyBinding(String translationKey, int key, String categoryKey) {
		final KeyMapping keyBinding = new KeyMapping(translationKey, InputConstants.Type.KEYSYM, key, categoryKey);
		ModEventBusClient.CLIENT_OBJECTS_TO_REGISTER.add(() -> ClientRegistry.registerKeyBinding(keyBinding));
		return new KeyBinding(keyBinding);
	}

	@MappedMethod
	public static void registerBlockColors(BlockColorProvider blockColorProvider, BlockRegistryObject... blocks) {
		final net.minecraft.world.level.block.Block[] newBlocks = new Block[blocks.length];
		for (int i = 0; i < blocks.length; i++) {
			newBlocks[i] = blocks[i].get().data;
		}
		ModEventBusClient.BLOCK_COLORS.add(event -> event.getBlockColors().register((blockState, blockRenderView, blockPos, tintIndex) -> blockColorProvider.getColor2(new BlockState(blockState), blockRenderView == null ? null : new BlockRenderView(blockRenderView), blockPos == null ? null : new BlockPos(blockPos), tintIndex), newBlocks));
	}

	@MappedMethod
	public static void registerItemColors(ItemColorProvider itemColorProvider, ItemRegistryObject... items) {
		final net.minecraft.world.item.Item[] newItems = new net.minecraft.world.item.Item[items.length];
		for (int i = 0; i < items.length; i++) {
			newItems[i] = items[i].get().data;
		}
		ModEventBusClient.ITEM_COLORS.add(event -> event.getItemColors().register(((itemStack, tintIndex) -> itemColorProvider.getColor2(new ItemStack(itemStack), tintIndex)), newItems));
	}

	@MappedMethod
	public static void registerItemModelPredicate(ItemRegistryObject item, Identifier identifier, ModelPredicateProvider modelPredicateProvider) {
		ItemProperties.register(item.get().data, identifier.data, (itemStack, clientWorld, livingEntity, seed) -> modelPredicateProvider.call(new ItemStack(itemStack), clientWorld == null ? null : new ClientWorld(clientWorld), livingEntity == null ? null : new LivingEntity(livingEntity)));
	}

	@MappedMethod
	public static void setupPackets(Identifier identifier) {
	}

	@MappedMethod
	public static <T extends PacketHandler> void sendPacketToServer(T data) {
		if (Registry.simpleChannel != null) {
			Registry.simpleChannel.sendToServer(data);
		}
	}

	@FunctionalInterface
	public interface ModelPredicateProvider {
		@MappedMethod
		float call(ItemStack itemStack, @Nullable ClientWorld clientWorld, @Nullable LivingEntity livingEntity);
	}
}
