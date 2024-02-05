package org.mtr.mapping.registry;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fmllegacy.network.NetworkEvent;
import net.minecraftforge.fmllegacy.network.NetworkRegistry;
import net.minecraftforge.fmllegacy.network.PacketDistributor;
import net.minecraftforge.fmllegacy.network.simple.SimpleChannel;
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
	final Map<String, Function<PacketBufferReceiver, ? extends PacketHandler>> packets = new HashMap<>();
	private static final String PROTOCOL_VERSION = "1";

	@MappedMethod
	public void init() {
		MinecraftForge.EVENT_BUS.register(MainEventBus.class);
		FMLJavaModLoadingContext.get().getModEventBus().register(ModEventBus.class);
	}

	@MappedMethod
	public BlockRegistryObject registerBlock(Identifier identifier, Supplier<Block> supplier) {
		ModEventBus.BLOCKS.add(() -> {
			final net.minecraft.world.level.block.Block block = supplier.get().data;
			block.setRegistryName(identifier.data);
			return block;
		});
		return new BlockRegistryObject(identifier);
	}

	@MappedMethod
	public BlockRegistryObject registerBlockWithBlockItem(Identifier identifier, Supplier<Block> supplier, CreativeModeTabHolder creativeModeTabHolder) {
		ModEventBus.BLOCKS.add(() -> {
			final net.minecraft.world.level.block.Block block = supplier.get().data;
			block.setRegistryName(identifier.data);
			return block;
		});
		final BlockRegistryObject blockRegistryObject = new BlockRegistryObject(identifier);
		ModEventBus.BLOCK_ITEMS.add(() -> {
			final BlockItemExtension blockItemExtension = new BlockItemExtension(blockRegistryObject.get(), new ItemSettings().tab(creativeModeTabHolder.creativeModeTab));
			blockItemExtension.setRegistryName(identifier.data);
			return blockItemExtension;
		});
		return blockRegistryObject;
	}

	@MappedMethod
	public ItemRegistryObject registerItem(Identifier identifier, Function<ItemSettings, Item> function, CreativeModeTabHolder creativeModeTabHolder) {
		ModEventBus.ITEMS.add(() -> {
			final net.minecraft.world.item.Item item = function.apply(new ItemSettings().tab(creativeModeTabHolder.creativeModeTab)).data;
			item.setRegistryName(identifier.data);
			return item;
		});
		return new ItemRegistryObject(identifier);
	}

	@MappedMethod
	public <T extends BlockEntityExtension> BlockEntityTypeRegistryObject<T> registerBlockEntityType(Identifier identifier, BiFunction<BlockPos, BlockState, T> function, Supplier<Block>... blockSuppliers) {
		ModEventBus.BLOCK_ENTITY_TYPES.add(() -> {
			final BlockEntityType<T> blockEntityType = BlockEntityType.Builder.of((pos, state) -> function.apply(new BlockPos(pos), new BlockState(state)), HolderBase.convertArray(blockSuppliers, net.minecraft.world.level.block.Block[]::new)).build(null);
			blockEntityType.setRegistryName(identifier.data);
			return blockEntityType;
		});
		return new BlockEntityTypeRegistryObject<>(identifier);
	}

	@MappedMethod
	public <T extends EntityExtension> EntityTypeRegistryObject<T> registerEntityType(Identifier identifier, BiFunction<EntityType<?>, World, T> function, float width, float height) {
		ModEventBus.ENTITY_TYPES.add(() -> {
			final net.minecraft.world.entity.EntityType<T> entityType = net.minecraft.world.entity.EntityType.Builder.of(getEntityFactory(function), MobCategory.MISC).sized(width, height).build(identifier.toString());
			entityType.setRegistryName(identifier.data);
			return entityType;
		});
		return new EntityTypeRegistryObject<>(identifier);
	}

	private <T extends EntityExtension> net.minecraft.world.entity.EntityType.EntityFactory<T> getEntityFactory(BiFunction<EntityType<?>, World, T> function) {
		return (entityType, world) -> function.apply(new EntityType<>(entityType), new World(world));
	}

	@MappedMethod
	public CreativeModeTabHolder createCreativeModeTabHolder(Identifier identifier, Supplier<ItemStack> iconSupplier) {
		return new CreativeModeTabHolder(identifier, iconSupplier);
	}

	@MappedMethod
	public SoundEventRegistryObject registerSoundEvent(Identifier identifier) {
		ModEventBus.SOUND_EVENTS.add(() -> {
			final SoundEvent soundEvent = new SoundEvent(identifier.data);
			soundEvent.setRegistryName(identifier.data);
			return soundEvent;
		});
		return new SoundEventRegistryObject(identifier);
	}

	@MappedMethod
	public void registerCommand(String command, Consumer<CommandBuilder<?>> buildCommand) {
		MainEventBus.COMMANDS.add(() -> {
			final CommandBuilder<LiteralArgumentBuilder<CommandSourceStack>> commandBuilder = new CommandBuilder<>(Commands.literal(command));
			buildCommand.accept(commandBuilder);
			return commandBuilder;
		});
	}

	@MappedMethod
	public void setupPackets(Identifier identifier) {
		simpleChannel = NetworkRegistry.newSimpleChannel(identifier.data, () -> PROTOCOL_VERSION, Registry::validProtocol, Registry::validProtocol);
		simpleChannel.registerMessage(0, ByteBuf.class, (byteBuf, packetBuffer) -> packetBuffer.writeBytes(byteBuf), packetBuffer -> packetBuffer.readBytes(packetBuffer.readableBytes()), (byteBuf, contextSupplier) -> {
			final NetworkEvent.Context context = contextSupplier.get();
			PacketBufferReceiver.receive(byteBuf, packetBufferReceiver -> {
				final Function<PacketBufferReceiver, ? extends PacketHandler> getPacketInstance = packets.get(packetBufferReceiver.readString());
				if (getPacketInstance != null) {
					final PacketHandler packetHandler = getPacketInstance.apply(packetBufferReceiver);
					if (context.getDirection().getReceptionSide().isClient()) {
						packetHandler.runClient();
						context.enqueueWork(packetHandler::runClientQueued);
					} else {
						packetHandler.runServer();
						final ServerPlayer serverPlayerEntity = context.getSender();
						if (serverPlayerEntity != null) {
							context.enqueueWork(() -> packetHandler.runServerQueued(new MinecraftServer(serverPlayerEntity.server), new ServerPlayerEntity(serverPlayerEntity)));
						}
					}
				}
			});
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
			packetBufferSender.send(byteBuf -> simpleChannel.send(PacketDistributor.PLAYER.with(() -> serverPlayerEntity.data), byteBuf));
		}
	}

	private static boolean validProtocol(String text) {
		return text.equals(PROTOCOL_VERSION) || text.equals(NetworkRegistry.ACCEPTVANILLA) || text.equals(NetworkRegistry.ABSENT);
	}
}
