package org.mtr.mapping.registry;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.Registries;
import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.*;
import org.mtr.mapping.mapper.BlockEntityExtension;
import org.mtr.mapping.mapper.BlockItemExtension;
import org.mtr.mapping.mapper.ItemHelper;
import org.mtr.mapping.tool.DummyClass;
import org.mtr.mapping.tool.HolderBase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public final class Registry extends DummyClass {

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
		OBJECTS_TO_REGISTER.add(() -> net.minecraft.registry.Registry.register(Registries.BLOCK, resourceLocation.data, block.data));
		return new BlockRegistryObject(block);
	}

	@MappedMethod
	public static BlockRegistryObject registerBlockWithBlockItem(ResourceLocation resourceLocation, Supplier<Block> supplier) {
		final Block block = supplier.get();
		OBJECTS_TO_REGISTER.add(() -> net.minecraft.registry.Registry.register(Registries.BLOCK, resourceLocation.data, block.data));
		OBJECTS_TO_REGISTER.add(() -> net.minecraft.registry.Registry.register(Registries.ITEM, resourceLocation.data, new BlockItemExtension(block, new ItemHelper.Properties())));
		return new BlockRegistryObject(block);
	}

	@MappedMethod
	public static ItemRegistryObject registerItem(ResourceLocation resourceLocation, Supplier<Item> supplier) {
		final Item item = supplier.get();
		OBJECTS_TO_REGISTER.add(() -> net.minecraft.registry.Registry.register(Registries.ITEM, resourceLocation.data, item.data));
		return new ItemRegistryObject(item);
	}

	@MappedMethod
	public static <T extends BlockEntityExtension> BlockEntityTypeRegistryObject<T> registerBlockEntityType(ResourceLocation resourceLocation, BiFunction<BlockPos, BlockState, T> function, Block... blocks) {
		final net.minecraft.block.entity.BlockEntityType<T> blockEntityType = FabricBlockEntityTypeBuilder.create((pos, state) -> function.apply(new BlockPos(pos), new BlockState(state)), HolderBase.convertArray(blocks, net.minecraft.block.Block[]::new)).build(null);
		OBJECTS_TO_REGISTER.add(() -> net.minecraft.registry.Registry.register(Registries.BLOCK_ENTITY_TYPE, resourceLocation.data, blockEntityType));
		return new BlockEntityTypeRegistryObject<>(new BlockEntityType<>(blockEntityType));
	}

	@MappedMethod
	public static CreativeModeTabHolder createCreativeModeTabHolder(ResourceLocation resourceLocation, Supplier<ItemStack> iconSupplier) {
		return new CreativeModeTabHolder(FabricItemGroup.builder(resourceLocation.data).icon(() -> iconSupplier.get().data).build());
	}

	@MappedMethod
	public static void addItemsToCreativeModeTab(CreativeModeTabHolder creativeModeTabHolder, ItemRegistryObject... itemRegistryObjects) {
		ItemGroupEvents.modifyEntriesEvent(creativeModeTabHolder.creativeModeTab).register(content -> {
			for (final ItemRegistryObject itemRegistryObject : itemRegistryObjects) {
				content.add(itemRegistryObject.get().data);
			}
		});
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
