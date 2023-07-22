package org.mtr.mapping.registry;

import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.client.util.InputUtil;
import net.minecraft.network.PacketByteBuf;
import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.*;
import org.mtr.mapping.mapper.BlockEntityExtension;
import org.mtr.mapping.mapper.BlockEntityRenderer;
import org.mtr.mapping.tool.DummyClass;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public final class RegistryClient extends DummyClass {

	private static final List<Runnable> OBJECTS_TO_REGISTER = new ArrayList<>();

	@MappedMethod
	public static void init() {
		OBJECTS_TO_REGISTER.forEach(Runnable::run);
	}

	@MappedMethod
	public static <T extends BlockEntityTypeRegistryObject<U>, U extends BlockEntityExtension> void registerBlockEntityRenderer(T blockEntityType, Function<BlockEntityRendererArgument, BlockEntityRenderer<U>> rendererInstance) {
		OBJECTS_TO_REGISTER.add(() -> BlockEntityRendererRegistry.register(blockEntityType.get().data, context -> rendererInstance.apply(new BlockEntityRendererArgument(context))));
	}

	@MappedMethod
	public static void registerBlockRenderType(RenderLayer renderLayer, BlockRegistryObject block) {
		OBJECTS_TO_REGISTER.add(() -> BlockRenderLayerMap.INSTANCE.putBlock(block.get().data, renderLayer.data));
	}

	@MappedMethod
	public static KeyBinding registerKeyBinding(String translationKey, int key, String categoryKey) {
		return new KeyBinding(KeyBindingHelper.registerKeyBinding(new net.minecraft.client.option.KeyBinding(translationKey, InputUtil.Type.KEYSYM, key, categoryKey)));
	}

	@MappedMethod
	public static void registerBlockColors(BlockColorProvider blockColorProvider, BlockRegistryObject... blocks) {
		final net.minecraft.block.Block[] newBlocks = new net.minecraft.block.Block[blocks.length];
		for (int i = 0; i < blocks.length; i++) {
			newBlocks[i] = blocks[i].get().data;
		}
		ColorProviderRegistry.BLOCK.register((blockState, blockRenderView, blockPos, tintIndex) -> blockColorProvider.getColor2(new BlockState(blockState), blockRenderView == null ? null : new BlockRenderView(blockRenderView), blockPos == null ? null : new BlockPos(blockPos), tintIndex), newBlocks);
	}

	@MappedMethod
	public static void registerItemColors(ItemColorProvider itemColorProvider, ItemRegistryObject... items) {
		final net.minecraft.item.Item[] newItems = new net.minecraft.item.Item[items.length];
		for (int i = 0; i < items.length; i++) {
			newItems[i] = items[i].get().data;
		}
		ColorProviderRegistry.ITEM.register((itemStack, tintIndex) -> itemColorProvider.getColor2(new ItemStack(itemStack), tintIndex), newItems);
	}

	@MappedMethod
	public static void registerItemModelPredicate(ItemRegistryObject item, Identifier identifier, ModelPredicateProvider modelPredicateProvider) {
		ModelPredicateProviderRegistry.register(item.get().data, identifier.data, (itemStack, clientWorld, livingEntity, seed) -> modelPredicateProvider.call(new ItemStack(itemStack), clientWorld == null ? null : new ClientWorld(clientWorld), livingEntity == null ? null : new LivingEntity(livingEntity)));
	}

	@MappedMethod
	public static void setupPackets(Identifier identifier) {
		ClientPlayNetworking.registerGlobalReceiver(identifier.data, (client, handler, buf, responseSender) -> {
			final Function<PacketBuffer, ? extends PacketHandler> getInstance = Registry.PACKETS.get(buf.readString());
			if (getInstance != null) {
				final PacketHandler packetHandler = getInstance.apply(new PacketBuffer(buf));
				client.execute(packetHandler::run);
			}
		});
	}

	@MappedMethod
	public static <T extends PacketHandler> void sendPacketToServer(T data) {
		if (Registry.packetsIdentifier != null) {
			final PacketByteBuf packetByteBuf = PacketByteBufs.create();
			packetByteBuf.writeString(data.getClass().getName());
			data.write(new PacketBuffer(packetByteBuf));
			ClientPlayNetworking.send(Registry.packetsIdentifier.data, packetByteBuf);
		}
	}

	@FunctionalInterface
	public interface ModelPredicateProvider {
		@MappedMethod
		float call(ItemStack itemStack, @Nullable ClientWorld clientWorld, @Nullable LivingEntity livingEntity);
	}
}
