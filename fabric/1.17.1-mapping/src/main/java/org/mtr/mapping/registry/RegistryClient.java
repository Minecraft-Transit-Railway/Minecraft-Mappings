package org.mtr.mapping.registry;

import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.object.builder.v1.client.model.FabricModelPredicateProviderRegistry;
import net.minecraft.client.util.InputUtil;
import net.minecraft.network.PacketByteBuf;
import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.*;
import org.mtr.mapping.mapper.BlockEntityExtension;
import org.mtr.mapping.mapper.BlockEntityRenderer;
import org.mtr.mapping.mapper.EntityExtension;
import org.mtr.mapping.mapper.EntityRenderer;
import org.mtr.mapping.tool.DummyClass;
import org.mtr.mapping.tool.PacketBufferReceiver;
import org.mtr.mapping.tool.PacketBufferSender;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public final class RegistryClient extends DummyClass {

	public static Function<World, ? extends EntityExtension> worldRenderingEntity;
	private final Registry registry;
	private final List<Runnable> objectsToRegister = new ArrayList<>();

	public RegistryClient(Registry registry) {
		this.registry = registry;
	}

	@MappedMethod
	public void init() {
		objectsToRegister.forEach(Runnable::run);
	}

	@MappedMethod
	public <T extends BlockEntityTypeRegistryObject<U>, U extends BlockEntityExtension> void registerBlockEntityRenderer(T blockEntityType, Function<BlockEntityRenderer.Argument, BlockEntityRenderer<U>> rendererInstance) {
		objectsToRegister.add(() -> BlockEntityRendererRegistry.register(blockEntityType.get().data, context -> rendererInstance.apply(new BlockEntityRenderer.Argument(context))));
	}

	@MappedMethod
	public <T extends EntityTypeRegistryObject<U>, U extends EntityExtension> void registerEntityRenderer(T entityType, Function<EntityRenderer.Argument, EntityRenderer<U>> rendererInstance) {
		objectsToRegister.add(() -> EntityRendererRegistry.register(entityType.get().data, dispatcher -> rendererInstance.apply(new EntityRenderer.Argument(dispatcher))));
	}

	@MappedMethod
	public void registerBlockRenderType(RenderLayer renderLayer, BlockRegistryObject block) {
		objectsToRegister.add(() -> BlockRenderLayerMap.INSTANCE.putBlock(block.get().data, renderLayer.data));
	}

	@MappedMethod
	public KeyBinding registerKeyBinding(String translationKey, int key, String categoryKey) {
		return new KeyBinding(KeyBindingHelper.registerKeyBinding(new net.minecraft.client.option.KeyBinding(translationKey, InputUtil.Type.KEYSYM, key, categoryKey)));
	}

	@MappedMethod
	public void registerBlockColors(BlockColorProvider blockColorProvider, BlockRegistryObject... blocks) {
		final net.minecraft.block.Block[] newBlocks = new net.minecraft.block.Block[blocks.length];
		for (int i = 0; i < blocks.length; i++) {
			newBlocks[i] = blocks[i].get().data;
		}
		ColorProviderRegistry.BLOCK.register((blockState, blockRenderView, blockPos, tintIndex) -> blockColorProvider.getColor2(new BlockState(blockState), blockRenderView == null ? null : new BlockRenderView(blockRenderView), blockPos == null ? null : new BlockPos(blockPos), tintIndex), newBlocks);
	}

	@MappedMethod
	public void registerItemColors(ItemColorProvider itemColorProvider, ItemRegistryObject... items) {
		final net.minecraft.item.Item[] newItems = new net.minecraft.item.Item[items.length];
		for (int i = 0; i < items.length; i++) {
			newItems[i] = items[i].get().data;
		}
		ColorProviderRegistry.ITEM.register((itemStack, tintIndex) -> itemColorProvider.getColor2(new ItemStack(itemStack), tintIndex), newItems);
	}

	@MappedMethod
	public void registerItemModelPredicate(ItemRegistryObject item, Identifier identifier, ModelPredicateProvider modelPredicateProvider) {
		FabricModelPredicateProviderRegistry.register(item.get().data, identifier.data, (itemStack, clientWorld, livingEntity, seed) -> modelPredicateProvider.call(new ItemStack(itemStack), clientWorld == null ? null : new ClientWorld(clientWorld), livingEntity == null ? null : new LivingEntity(livingEntity)));
	}

	@MappedMethod
	public void setupPackets(Identifier identifier) {
		ClientPlayNetworking.registerGlobalReceiver(identifier.data, (client, handler, buf, responseSender) -> PacketBufferReceiver.receive(buf, packetBufferReceiver -> {
			final Function<PacketBufferReceiver, ? extends PacketHandler> getInstance = registry.packets.get(packetBufferReceiver.readString());
			if (getInstance != null) {
				getInstance.apply(packetBufferReceiver).runClient();
			}
		}, client::execute));
	}

	@MappedMethod
	public <T extends PacketHandler> void sendPacketToServer(T data) {
		if (registry.packetsIdentifier != null) {
			final PacketBufferSender packetBufferSender = new PacketBufferSender(PacketByteBufs::create);
			packetBufferSender.writeString(data.getClass().getName());
			data.write(packetBufferSender);
			packetBufferSender.send(byteBuf -> ClientPlayNetworking.send(registry.packetsIdentifier.data, byteBuf instanceof PacketByteBuf ? (PacketByteBuf) byteBuf : new PacketByteBuf(byteBuf)), MinecraftClient.getInstance()::execute);
		}
	}

	@FunctionalInterface
	public interface ModelPredicateProvider {
		@MappedMethod
		float call(ItemStack itemStack, @Nullable ClientWorld clientWorld, @Nullable LivingEntity livingEntity);
	}
}
