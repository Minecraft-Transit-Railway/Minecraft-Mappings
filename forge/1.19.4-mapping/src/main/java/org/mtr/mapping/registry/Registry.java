package org.mtr.mapping.registry;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.*;
import org.mtr.mapping.mapper.BlockEntityExtension;
import org.mtr.mapping.mapper.BlockItemExtension;
import org.mtr.mapping.mapper.EntityExtension;
import org.mtr.mapping.tool.DummyClass;
import org.mtr.mapping.tool.HolderBase;
import org.mtr.mapping.tool.PacketBufferReceiver;
import org.mtr.mapping.tool.PacketBufferSender;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public final class Registry extends DummyClass {

	SimpleChannel simpleChannel;
	private final MainEventBus mainEventBus = new MainEventBus();
	private final ModEventBus modEventBus = new ModEventBus();
	final Map<String, Function<PacketBufferReceiver, ? extends PacketHandler>> packets = new HashMap<>();
	public final EventRegistry eventRegistry = new EventRegistry(mainEventBus);
	private static final String PROTOCOL_VERSION = "1";

	@MappedMethod
	public void init() {
		MinecraftForge.EVENT_BUS.register(mainEventBus);
		FMLJavaModLoadingContext.get().getModEventBus().register(modEventBus);
	}

	@MappedMethod
	public BlockRegistryObject registerBlock(Identifier identifier, Supplier<Block> supplier) {
		modEventBus.BLOCKS.put(identifier, supplier);
		return new BlockRegistryObject(identifier);
	}

	@MappedMethod
	public BlockRegistryObject registerBlockWithBlockItem(Identifier identifier, Supplier<Block> supplier, CreativeModeTabHolder... creativeModeTabHolders) {
		return registerBlockWithBlockItem(identifier, supplier, BlockItemExtension::new, creativeModeTabHolders);
	}

	@MappedMethod
	public BlockRegistryObject registerBlockWithBlockItem(Identifier identifier, Supplier<Block> supplier, BiFunction<Block, ItemSettings, BlockItemExtension> function, CreativeModeTabHolder... creativeModeTabHolders) {
		modEventBus.BLOCKS.put(identifier, supplier);
		final BlockRegistryObject blockRegistryObject = new BlockRegistryObject(identifier);
		modEventBus.BLOCK_ITEMS.put(identifier, () -> function.apply(blockRegistryObject.get(), new ItemSettings()));
		for (final CreativeModeTabHolder creativeModeTabHolder : creativeModeTabHolders) {
			creativeModeTabHolder.itemSuppliers.add(new ItemRegistryObject(identifier)::get);
		}
		return blockRegistryObject;
	}

	@MappedMethod
	public ItemRegistryObject registerItem(Identifier identifier, Function<ItemSettings, Item> function, CreativeModeTabHolder... creativeModeTabHolders) {
		modEventBus.ITEMS.put(identifier, () -> function.apply(new ItemSettings()));
		final ItemRegistryObject itemRegistryObject = new ItemRegistryObject(identifier);
		for (final CreativeModeTabHolder creativeModeTabHolder : creativeModeTabHolders) {
			creativeModeTabHolder.itemSuppliers.add(itemRegistryObject::get);
		}
		return itemRegistryObject;
	}

	@MappedMethod
	public <T extends BlockEntityExtension> BlockEntityTypeRegistryObject<T> registerBlockEntityType(Identifier identifier, BiFunction<BlockPos, BlockState, T> function, Supplier<Block>... blockSuppliers) {
		modEventBus.blockEntityTypes.put(identifier, () -> BlockEntityType.Builder.of((pos, state) -> function.apply(new BlockPos(pos), new BlockState(state)), HolderBase.convertArray(blockSuppliers, net.minecraft.world.level.block.Block[]::new)).build(null));
		return new BlockEntityTypeRegistryObject<>(identifier);
	}

	@MappedMethod
	public <T extends EntityExtension> EntityTypeRegistryObject<T> registerEntityType(Identifier identifier, BiFunction<EntityType<?>, World, T> function, float width, float height) {
		modEventBus.entityTypes.put(identifier, () -> net.minecraft.world.entity.EntityType.Builder.of(getEntityFactory(function), MobCategory.MISC).sized(width, height).build(identifier.toString()));
		return new EntityTypeRegistryObject<>(identifier);
	}

	private <T extends EntityExtension> net.minecraft.world.entity.EntityType.EntityFactory<T> getEntityFactory(BiFunction<EntityType<?>, World, T> function) {
		return (entityType, world) -> function.apply(new EntityType<>(entityType), new World(world));
	}

	@MappedMethod
	public ParticleTypeRegistryObject registerParticleType(Identifier identifier) {
		return registerParticleType(identifier, false);
	}

	@MappedMethod
	public ParticleTypeRegistryObject registerParticleType(Identifier identifier, boolean alwaysSpawn) {
		modEventBus.particleTypes.put(identifier, () -> new SimpleParticleType(alwaysSpawn));
		return new ParticleTypeRegistryObject(identifier);
	}

	@MappedMethod
	public CreativeModeTabHolder createCreativeModeTabHolder(Identifier identifier, Supplier<ItemStack> iconSupplier) {
		final CreativeModeTabHolder creativeModeTabHolder = new CreativeModeTabHolder(identifier.data, iconSupplier);
		modEventBus.creativeModeTabs.add(creativeModeTabHolder);
		return creativeModeTabHolder;
	}

	@MappedMethod
	public SoundEventRegistryObject registerSoundEvent(Identifier identifier) {
		modEventBus.soundEvents.put(identifier, () -> new SoundEvent(net.minecraft.sounds.SoundEvent.createVariableRangeEvent(identifier.data)));
		return new SoundEventRegistryObject(identifier);
	}

	@MappedMethod
	public void registerCommand(String command, Consumer<CommandBuilder<?>> buildCommand, String... redirects) {
		mainEventBus.commands.add(dispatcher -> {
			final CommandBuilder<LiteralArgumentBuilder<CommandSourceStack>> commandBuilder = new CommandBuilder<>(Commands.literal(command));
			buildCommand.accept(commandBuilder);
			final LiteralCommandNode<CommandSourceStack> literalCommandNode = dispatcher.register(commandBuilder.argumentBuilder);
			for (final String redirect : redirects) {
				dispatcher.register(Commands.literal(redirect).redirect(literalCommandNode));
			}
		});
	}

	@MappedMethod
	public void setupPackets(Identifier identifier) {
		simpleChannel = NetworkRegistry.newSimpleChannel(identifier.data, () -> PROTOCOL_VERSION, Registry::validProtocol, Registry::validProtocol);
		simpleChannel.registerMessage(0, PacketObject.class, (packetObject, packetBuffer) -> packetBuffer.writeBytes(packetObject.byteBuf), packetBuffer -> new PacketObject(packetBuffer.readBytes(packetBuffer.readableBytes())), (packetObject, contextSupplier) -> {
			final NetworkEvent.Context context = contextSupplier.get();
			context.setPacketHandled(true);
			PacketBufferReceiver.receive(packetObject.byteBuf, packetBufferReceiver -> {
				final Function<PacketBufferReceiver, ? extends PacketHandler> getPacketInstance = packets.get(packetBufferReceiver.readString());
				if (getPacketInstance != null) {
					final PacketHandler packetHandler = getPacketInstance.apply(packetBufferReceiver);
					if (context.getDirection().getReceptionSide().isClient()) {
						packetHandler.runClient();
					} else {
						final ServerPlayer serverPlayerEntity = context.getSender();
						if (serverPlayerEntity != null) {
							packetHandler.runServer(new MinecraftServer(serverPlayerEntity.server), new ServerPlayerEntity(serverPlayerEntity));
						}
					}
				}
			}, context::enqueueWork);
		});
	}

	@MappedMethod
	public <T extends PacketHandler> void registerPacket(Class<T> classObject, Function<PacketBufferReceiver, T> getInstance) {
		packets.put(classObject.getName(), getInstance);
	}

	@MappedMethod
	public <T extends PacketHandler> void sendPacketToClient(ServerPlayerEntity serverPlayerEntity, T data) {
		if (simpleChannel != null) {
			final PacketBufferSender packetBufferSender = new PacketBufferSender(Unpooled::buffer);
			packetBufferSender.writeString(data.getClass().getName());
			data.write(packetBufferSender);
			packetBufferSender.send(byteBuf -> simpleChannel.send(PacketDistributor.PLAYER.with(() -> serverPlayerEntity.data), new PacketObject(byteBuf)), serverPlayerEntity.getServerMapped()::execute);
		}
	}

	private static boolean validProtocol(String text) {
		return text.equals(PROTOCOL_VERSION) || text.equals(NetworkRegistry.ACCEPTVANILLA) || text.equals(NetworkRegistry.ABSENT.version());
	}

	static class PacketObject {

		final ByteBuf byteBuf;

		PacketObject(ByteBuf byteBuf) {
			this.byteBuf = byteBuf;
		}
	}
}
