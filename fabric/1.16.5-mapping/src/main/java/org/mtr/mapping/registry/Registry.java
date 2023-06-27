package org.mtr.mapping.registry;

import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.*;
import org.mtr.mapping.mapper.Block;
import org.mtr.mapping.mapper.BlockEntity;
import org.mtr.mapping.mapper.BlockItem;
import org.mtr.mapping.mapper.Item;
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
	public static BlockRegistryObject registerBlock(ResourceLocation resourceLocation, Supplier<Block> supplier) {
		final Block block = supplier.get();
		OBJECTS_TO_REGISTER.add(() -> net.minecraft.util.registry.Registry.register(net.minecraft.util.registry.Registry.BLOCK, resourceLocation.data, block));
		return new BlockRegistryObject(block);
	}

	@MappedMethod
	public static BlockRegistryObject registerBlockWithBlockItem(ResourceLocation resourceLocation, Supplier<Block> supplier) {
		final Block block = supplier.get();
		OBJECTS_TO_REGISTER.add(() -> net.minecraft.util.registry.Registry.register(net.minecraft.util.registry.Registry.BLOCK, resourceLocation.data, block));
		OBJECTS_TO_REGISTER.add(() -> net.minecraft.util.registry.Registry.register(net.minecraft.util.registry.Registry.ITEM, resourceLocation.data, new BlockItem(block, new Item.Properties())));
		return new BlockRegistryObject(block);
	}

	@MappedMethod
	public static ItemRegistryObject registerItem(ResourceLocation resourceLocation, Supplier<Item> supplier) {
		final Item item = supplier.get();
		OBJECTS_TO_REGISTER.add(() -> net.minecraft.util.registry.Registry.register(net.minecraft.util.registry.Registry.ITEM, resourceLocation.data, item));
		return new ItemRegistryObject(item);
	}

	@MappedMethod
	public static <T extends BlockEntity> BlockEntityTypeRegistryObject<T> registerBlockEntityType(ResourceLocation resourceLocation, BiFunction<BlockPos, BlockState, T> function, Block... blocks) {
		final net.minecraft.block.entity.BlockEntityType<T> blockEntityType = net.minecraft.block.entity.BlockEntityType.Builder.create(() -> function.apply(null, null), blocks).build(null);
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
