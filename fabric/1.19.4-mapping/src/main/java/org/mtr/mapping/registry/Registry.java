package org.mtr.mapping.registry;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.Registries;
import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.*;
import org.mtr.mapping.mapper.BlockEntityExtension;
import org.mtr.mapping.mapper.BlockItemExtension;
import org.mtr.mapping.mapper.EntityExtension;
import org.mtr.mapping.tool.DummyClass;
import org.mtr.mapping.tool.HolderBase;
import org.mtr.mapping.tool.PacketBufferReceiver;
import org.mtr.mapping.tool.PacketBufferSender;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public final class Registry extends DummyClass {

	Identifier packetsIdentifier;
	final Map<String, Function<PacketBufferReceiver, ? extends PacketHandler>> packets = new HashMap<>();
	private final List<Runnable> objectsToRegister = new ArrayList<>();

	@MappedMethod
	public void init() {
		objectsToRegister.forEach(Runnable::run);
	}

	@MappedMethod
	public BlockRegistryObject registerBlock(Identifier identifier, Supplier<Block> supplier) {
		final Block block = supplier.get();
		objectsToRegister.add(() -> net.minecraft.registry.Registry.register(Registries.BLOCK, identifier.data, block.data));
		return new BlockRegistryObject(block);
	}

	@MappedMethod
	public BlockRegistryObject registerBlockWithBlockItem(Identifier identifier, Supplier<Block> supplier, CreativeModeTabHolder creativeModeTabHolder) {
		final Block block = supplier.get();
		objectsToRegister.add(() -> net.minecraft.registry.Registry.register(Registries.BLOCK, identifier.data, block.data));
		final BlockItemExtension blockItemExtension = new BlockItemExtension(block, new ItemSettings());
		objectsToRegister.add(() -> net.minecraft.registry.Registry.register(Registries.ITEM, identifier.data, blockItemExtension));
		ItemGroupEvents.modifyEntriesEvent(creativeModeTabHolder.creativeModeTab).register(content -> content.add(blockItemExtension));
		return new BlockRegistryObject(block);
	}

	@MappedMethod
	public ItemRegistryObject registerItem(Identifier identifier, Function<ItemSettings, Item> function, CreativeModeTabHolder creativeModeTabHolder) {
		final Item item = function.apply(new ItemSettings());
		objectsToRegister.add(() -> net.minecraft.registry.Registry.register(Registries.ITEM, identifier.data, item.data));
		ItemGroupEvents.modifyEntriesEvent(creativeModeTabHolder.creativeModeTab).register(content -> content.add(item.data));
		return new ItemRegistryObject(item);
	}

	@MappedMethod
	public <T extends BlockEntityExtension> BlockEntityTypeRegistryObject<T> registerBlockEntityType(Identifier identifier, BiFunction<BlockPos, BlockState, T> function, Supplier<Block>... blockSuppliers) {
		final net.minecraft.block.entity.BlockEntityType<T> blockEntityType = FabricBlockEntityTypeBuilder.create((pos, state) -> function.apply(new BlockPos(pos), new BlockState(state)), HolderBase.convertArray(blockSuppliers, net.minecraft.block.Block[]::new)).build(null);
		objectsToRegister.add(() -> net.minecraft.registry.Registry.register(Registries.BLOCK_ENTITY_TYPE, identifier.data, blockEntityType));
		return new BlockEntityTypeRegistryObject<>(new BlockEntityType<>(blockEntityType));
	}

	@MappedMethod
	public <T extends EntityExtension> EntityTypeRegistryObject<T> registerEntityType(Identifier identifier, BiFunction<EntityType<?>, World, T> function, float width, float height) {
		final net.minecraft.entity.EntityType<T> entityType = FabricEntityTypeBuilder.create(SpawnGroup.MISC, getEntityFactory(function)).dimensions(EntityDimensions.fixed(width, height)).build();
		objectsToRegister.add(() -> net.minecraft.registry.Registry.register(Registries.ENTITY_TYPE, identifier.data, entityType));
		return new EntityTypeRegistryObject<>(new EntityType<>(entityType));
	}

	private <T extends EntityExtension> net.minecraft.entity.EntityType.EntityFactory<T> getEntityFactory(BiFunction<EntityType<?>, World, T> function) {
		return (entityType, world) -> function.apply(new EntityType<>(entityType), new World(world));
	}

	@MappedMethod
	public CreativeModeTabHolder createCreativeModeTabHolder(Identifier identifier, Supplier<ItemStack> iconSupplier) {
		return new CreativeModeTabHolder(FabricItemGroup.builder(identifier.data).icon(() -> iconSupplier.get().data).build());
	}

	@MappedMethod
	public SoundEventRegistryObject registerSoundEvent(Identifier identifier) {
		final SoundEvent soundEvent = SoundEvent.of(identifier);
		objectsToRegister.add(() -> net.minecraft.registry.Registry.register(Registries.SOUND_EVENT, identifier.data, soundEvent.data));
		return new SoundEventRegistryObject(soundEvent);
	}

	@MappedMethod
	public void setupPackets(Identifier identifier) {
		packetsIdentifier = identifier;
		ServerPlayNetworking.registerGlobalReceiver(identifier.data, (server, player, handler, buf, responseSender) -> PacketBufferReceiver.receive(buf, packetBufferReceiver -> {
			final Function<PacketBufferReceiver, ? extends PacketHandler> getInstance = packets.get(packetBufferReceiver.readString());
			if (getInstance != null) {
				final PacketHandler packetHandler = getInstance.apply(packetBufferReceiver);
				packetHandler.runServer();
				server.execute(() -> packetHandler.runServerQueued(new MinecraftServer(server), new ServerPlayerEntity(player)));
			}
		}));
	}

	@MappedMethod
	public <T extends PacketHandler> void registerPacket(Class<T> classObject, Function<PacketBufferReceiver, T> getInstance) {
		packets.put(classObject.getName(), getInstance);
	}

	@MappedMethod
	public <T extends PacketHandler> void sendPacketToClient(ServerPlayerEntity serverPlayerEntity, T data) {
		if (packetsIdentifier != null) {
			final PacketBufferSender packetBufferSender = new PacketBufferSender(PacketByteBufs::create);
			packetBufferSender.writeString(data.getClass().getName());
			data.write(packetBufferSender);
			packetBufferSender.send(byteBuf -> ServerPlayNetworking.send(serverPlayerEntity.data, packetsIdentifier.data, byteBuf instanceof PacketByteBuf ? (PacketByteBuf) byteBuf : new PacketByteBuf(byteBuf)));
		}
	}
}
