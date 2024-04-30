package org.mtr.mapping.registry;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
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
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public final class Registry extends DummyClass {

	Identifier packetsIdentifier;
	final Map<String, Function<PacketBufferReceiver, ? extends PacketHandler>> packets = new HashMap<>();
	public final EventRegistry eventRegistry = new EventRegistry();
	private final List<Runnable> objectsToRegister = new ArrayList<>();
	private final List<Consumer<CommandDispatcher<ServerCommandSource>>> commandsToRegister = new ArrayList<>();

	@MappedMethod
	public void init() {
		objectsToRegister.forEach(Runnable::run);
		CommandRegistrationCallback.EVENT.register((dispatcher, commandRegistryAccess, environment) -> commandsToRegister.forEach(consumer -> consumer.accept(dispatcher)));
	}

	@MappedMethod
	public BlockRegistryObject registerBlock(Identifier identifier, Supplier<Block> supplier) {
		final Block block = supplier.get();
		objectsToRegister.add(() -> net.minecraft.util.registry.Registry.register(net.minecraft.util.registry.Registry.BLOCK, identifier.data, block.data));
		return new BlockRegistryObject(block);
	}

	@MappedMethod
	public BlockRegistryObject registerBlockWithBlockItem(Identifier identifier, Supplier<Block> supplier, CreativeModeTabHolder... creativeModeTabHolders) {
		return registerBlockWithBlockItem(identifier, supplier, BlockItemExtension::new, creativeModeTabHolders);
	}

	@MappedMethod
	public BlockRegistryObject registerBlockWithBlockItem(Identifier identifier, Supplier<Block> supplier, BiFunction<Block, ItemSettings, BlockItemExtension> function, CreativeModeTabHolder... creativeModeTabHolders) {
		final Block block = supplier.get();
		objectsToRegister.add(() -> net.minecraft.util.registry.Registry.register(net.minecraft.util.registry.Registry.BLOCK, identifier.data, block.data));
		objectsToRegister.add(() -> net.minecraft.util.registry.Registry.register(net.minecraft.util.registry.Registry.ITEM, identifier.data, function.apply(block, getItemSettings(creativeModeTabHolders))));
		return new BlockRegistryObject(block);
	}

	@MappedMethod
	public ItemRegistryObject registerItem(Identifier identifier, Function<ItemSettings, Item> function, CreativeModeTabHolder... creativeModeTabHolders) {
		final Item item = function.apply(getItemSettings(creativeModeTabHolders));
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
	public ParticleTypeRegistryObject registerParticleType(Identifier identifier) {
		return registerParticleType(identifier, false);
	}

	@MappedMethod
	public ParticleTypeRegistryObject registerParticleType(Identifier identifier, boolean alwaysSpawn) {
		final DefaultParticleType defaultParticleType = new DefaultParticleType(FabricParticleTypes.simple(alwaysSpawn));
		objectsToRegister.add(() -> net.minecraft.util.registry.Registry.register(net.minecraft.util.registry.Registry.PARTICLE_TYPE, identifier.data, defaultParticleType.data));
		return new ParticleTypeRegistryObject(defaultParticleType, identifier);
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
	public void registerCommand(String command, Consumer<CommandBuilder<?>> buildCommand, String... redirects) {
		commandsToRegister.add(dispatcher -> {
			final CommandBuilder<LiteralArgumentBuilder<ServerCommandSource>> commandBuilder = new CommandBuilder<>(CommandManager.literal(command));
			buildCommand.accept(commandBuilder);
			final LiteralCommandNode<ServerCommandSource> literalCommandNode = dispatcher.register(commandBuilder.argumentBuilder);
			for (final String redirect : redirects) {
				dispatcher.register(CommandManager.literal(redirect).redirect(literalCommandNode));
			}
		});
	}

	@MappedMethod
	public void setupPackets(Identifier identifier) {
		packetsIdentifier = identifier;
		ServerPlayNetworking.registerGlobalReceiver(identifier.data, (server, player, handler, buf, responseSender) -> PacketBufferReceiver.receive(buf, packetBufferReceiver -> {
			final Function<PacketBufferReceiver, ? extends PacketHandler> getInstance = packets.get(packetBufferReceiver.readString());
			if (getInstance != null) {
				getInstance.apply(packetBufferReceiver).runServer(new MinecraftServer(server), new ServerPlayerEntity(player));
			}
		}, server::execute));
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
			packetBufferSender.send(byteBuf -> ServerPlayNetworking.send(serverPlayerEntity.data, packetsIdentifier.data, byteBuf instanceof PacketByteBuf ? (PacketByteBuf) byteBuf : new PacketByteBuf(byteBuf)), serverPlayerEntity.getServerMapped()::execute);
		}
	}

	private static ItemSettings getItemSettings(CreativeModeTabHolder... creativeModeTabHolders) {
		if (creativeModeTabHolders.length == 0) {
			return new ItemSettings();
		} else {
			return new ItemSettings(new net.minecraft.item.Item.Settings().group(creativeModeTabHolders[0].creativeModeTab));
		}
	}
}
