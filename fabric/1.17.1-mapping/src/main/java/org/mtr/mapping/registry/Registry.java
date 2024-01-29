package org.mtr.mapping.registry;

import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.network.PacketByteBuf;
import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.*;
import org.mtr.mapping.mapper.BlockEntityExtension;
import org.mtr.mapping.mapper.BlockItemExtension;
import org.mtr.mapping.mapper.EntityExtension;
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

	Identifier packetsIdentifier;
	final Map<String, Function<PacketBuffer, ? extends PacketHandler>> packets = new HashMap<>();
	private final List<Runnable> objectsToRegister = new ArrayList<>();

	@MappedMethod
	public void init() {
		objectsToRegister.forEach(Runnable::run);
	}

	@MappedMethod
	public BlockRegistryObject registerBlock(Identifier identifier, Supplier<Block> supplier) {
		final Block block = supplier.get();
		objectsToRegister.add(() -> net.minecraft.util.registry.Registry.register(net.minecraft.util.registry.Registry.BLOCK, identifier.data, block.data));
		return new BlockRegistryObject(block);
	}

	@MappedMethod
	public BlockRegistryObject registerBlockWithBlockItem(Identifier identifier, Supplier<Block> supplier, CreativeModeTabHolder creativeModeTabHolder) {
		final Block block = supplier.get();
		objectsToRegister.add(() -> net.minecraft.util.registry.Registry.register(net.minecraft.util.registry.Registry.BLOCK, identifier.data, block.data));
		objectsToRegister.add(() -> net.minecraft.util.registry.Registry.register(net.minecraft.util.registry.Registry.ITEM, identifier.data, new BlockItemExtension(block, new ItemSettings().group(creativeModeTabHolder.creativeModeTab))));
		return new BlockRegistryObject(block);
	}

	@MappedMethod
	public ItemRegistryObject registerItem(Identifier identifier, Function<ItemSettings, Item> function, CreativeModeTabHolder creativeModeTabHolder) {
		final Item item = function.apply(new ItemSettings().group(creativeModeTabHolder.creativeModeTab));
		objectsToRegister.add(() -> net.minecraft.util.registry.Registry.register(net.minecraft.util.registry.Registry.ITEM, identifier.data, item.data));
		return new ItemRegistryObject(item);
	}

	@MappedMethod
	public <T extends BlockEntityExtension> BlockEntityTypeRegistryObject<T> registerBlockEntityType(Identifier identifier, BiFunction<BlockPos, BlockState, T> function, Supplier<Block>... blockSuppliers) {
		final net.minecraft.block.entity.BlockEntityType<T> blockEntityType = FabricBlockEntityTypeBuilder.create((pos, state) -> function.apply(new BlockPos(pos), new BlockState(state)), HolderBase.convertArray(blockSuppliers, net.minecraft.block.Block[]::new)).build(null);
		objectsToRegister.add(() -> net.minecraft.util.registry.Registry.register(net.minecraft.util.registry.Registry.BLOCK_ENTITY_TYPE, identifier.data, blockEntityType));
		return new BlockEntityTypeRegistryObject<>(new BlockEntityType<>(blockEntityType));
	}

	@MappedMethod
	public <T extends EntityExtension> EntityTypeRegistryObject<T> registerEntityType(Identifier identifier, BiFunction<EntityType<?>, World, T> function, float width, float height) {
		final net.minecraft.entity.EntityType<T> entityType = FabricEntityTypeBuilder.create(SpawnGroup.MISC, getEntityFactory(function)).dimensions(EntityDimensions.fixed(width, height)).build();
		objectsToRegister.add(() -> net.minecraft.util.registry.Registry.register(net.minecraft.util.registry.Registry.ENTITY_TYPE, identifier.data, entityType));
		return new EntityTypeRegistryObject<>(new EntityType<>(entityType));
	}

	private <T extends EntityExtension> net.minecraft.entity.EntityType.EntityFactory<T> getEntityFactory(BiFunction<EntityType<?>, World, T> function) {
		return (entityType, world) -> function.apply(new EntityType<>(entityType), new World(world));
	}

	@MappedMethod
	public CreativeModeTabHolder createCreativeModeTabHolder(Identifier identifier, Supplier<ItemStack> iconSupplier) {
		return new CreativeModeTabHolder(FabricItemGroupBuilder.create(identifier.data).icon(() -> iconSupplier.get().data).build());
	}

	@MappedMethod
	public SoundEventRegistryObject registerSoundEvent(Identifier identifier) {
		final SoundEvent soundEvent = new SoundEvent(identifier);
		objectsToRegister.add(() -> net.minecraft.util.registry.Registry.register(net.minecraft.util.registry.Registry.SOUND_EVENT, identifier.data, soundEvent.data));
		return new SoundEventRegistryObject(soundEvent);
	}

	@MappedMethod
	public void setupPackets(Identifier identifier) {
		packetsIdentifier = identifier;
		ServerPlayNetworking.registerGlobalReceiver(identifier.data, (server, player, handler, buf, responseSender) -> {
			final Function<PacketBuffer, ? extends PacketHandler> getInstance = packets.get(buf.readString());
			if (getInstance != null) {
				final PacketHandler packetHandler = getInstance.apply(new PacketBuffer(buf));
				packetHandler.runServer();
				server.execute(() -> packetHandler.runServerQueued(new MinecraftServer(server), new ServerPlayerEntity(player)));
			}
		});
	}

	@MappedMethod
	public <T extends PacketHandler> void registerPacket(Class<T> classObject, Function<PacketBuffer, T> getInstance) {
		packets.put(classObject.getName(), getInstance);
	}

	@MappedMethod
	public <T extends PacketHandler> void sendPacketToClient(ServerPlayerEntity serverPlayerEntity, T data) {
		if (packetsIdentifier != null) {
			final PacketByteBuf packetByteBuf = PacketByteBufs.create();
			packetByteBuf.writeString(data.getClass().getName());
			data.write(new PacketBuffer(packetByteBuf));
			ServerPlayNetworking.send(serverPlayerEntity.data, packetsIdentifier.data, packetByteBuf);
		}
	}
}
