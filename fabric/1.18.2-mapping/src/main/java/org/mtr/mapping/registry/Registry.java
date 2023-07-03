package org.mtr.mapping.registry;

import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.network.PacketByteBuf;
import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.*;
import org.mtr.mapping.mapper.BlockEntityExtension;
import org.mtr.mapping.mapper.BlockExtension;
import org.mtr.mapping.mapper.BlockItem;
import org.mtr.mapping.mapper.ItemExtension;
import org.mtr.mapping.tool.Dummy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public final class Registry extends Dummy {

	static ResourceLocation packetsResourceLocation;
	static final Map<String, Function<PacketBuffer, ? extends PacketHandler>> PACKETS = new HashMap<>();
	private static final List<Runnable> OBJECTS_TO_REGISTER = new ArrayList<>();

	@MappedMethod
	public static void init() {
		OBJECTS_TO_REGISTER.forEach(Runnable::run);
	}

	@MappedMethod
	public static BlockRegistryObject registerBlock(ResourceLocation resourceLocation, Supplier<BlockExtension> supplier) {
		final BlockExtension blockExtension = supplier.get();
		OBJECTS_TO_REGISTER.add(() -> net.minecraft.util.registry.Registry.register(net.minecraft.util.registry.Registry.BLOCK, resourceLocation.data, blockExtension));
		return new BlockRegistryObject(blockExtension);
	}

	@MappedMethod
	public static BlockRegistryObject registerBlockWithBlockItem(ResourceLocation resourceLocation, Supplier<BlockExtension> supplier) {
		final BlockExtension blockExtension = supplier.get();
		OBJECTS_TO_REGISTER.add(() -> net.minecraft.util.registry.Registry.register(net.minecraft.util.registry.Registry.BLOCK, resourceLocation.data, blockExtension));
		OBJECTS_TO_REGISTER.add(() -> net.minecraft.util.registry.Registry.register(net.minecraft.util.registry.Registry.ITEM, resourceLocation.data, new BlockItem(blockExtension, new ItemExtension.Properties())));
		return new BlockRegistryObject(blockExtension);
	}

	@MappedMethod
	public static ItemRegistryObject registerItem(ResourceLocation resourceLocation, Supplier<ItemExtension> supplier) {
		final ItemExtension itemExtension = supplier.get();
		OBJECTS_TO_REGISTER.add(() -> net.minecraft.util.registry.Registry.register(net.minecraft.util.registry.Registry.ITEM, resourceLocation.data, itemExtension));
		return new ItemRegistryObject(itemExtension);
	}

	@MappedMethod
	public static <T extends BlockEntityExtension> BlockEntityTypeRegistryObject<T> registerBlockEntityType(ResourceLocation resourceLocation, BiFunction<BlockPos, BlockState, T> function, BlockExtension... blockExtensions) {
		final net.minecraft.block.entity.BlockEntityType<T> blockEntityType = FabricBlockEntityTypeBuilder.create((pos, state) -> function.apply(new BlockPos(pos), new BlockState(state)), blockExtensions).build(null);
		OBJECTS_TO_REGISTER.add(() -> net.minecraft.util.registry.Registry.register(net.minecraft.util.registry.Registry.BLOCK_ENTITY_TYPE, resourceLocation.data, blockEntityType));
		return new BlockEntityTypeRegistryObject<>(new BlockEntityType<>(blockEntityType));
	}

	@MappedMethod
	public static CreativeModeTabHolder createCreativeModeTabHolder(ResourceLocation resourceLocation, Supplier<ItemStack> iconSupplier) {
		return new CreativeModeTabHolder(FabricItemGroupBuilder.create(resourceLocation.data).icon(() -> iconSupplier.get().data).build());
	}

	@MappedMethod
	public static void setupPackets(ResourceLocation resourceLocation) {
		packetsResourceLocation = resourceLocation;
		ServerPlayNetworking.registerGlobalReceiver(resourceLocation.data, (server, player, handler, buf, responseSender) -> {
			final Function<PacketBuffer, ? extends PacketHandler> getInstance = PACKETS.get(buf.readString());
			if (getInstance != null) {
				final PacketHandler packetHandler = getInstance.apply(new PacketBuffer(buf));
				server.execute(packetHandler::run);
			}
		});
	}

	@MappedMethod
	public static <T extends PacketHandler> void registerPacket(Class<T> classObject, Function<PacketBuffer, T> getInstance) {
		PACKETS.put(classObject.getName(), getInstance);
	}

	@MappedMethod
	public static <T extends PacketHandler> void sendPacketToClient(ServerPlayerEntity serverPlayerEntity, T data) {
		if (packetsResourceLocation != null) {
			final PacketByteBuf packetByteBuf = PacketByteBufs.create();
			packetByteBuf.writeString(data.getClass().getName());
			data.write(new PacketBuffer(packetByteBuf));
			ServerPlayNetworking.send(serverPlayerEntity.data, packetsResourceLocation.data, packetByteBuf);
		}
	}
}
