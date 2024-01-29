package org.mtr.mapping.registry;

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

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public final class Registry extends DummyClass {

	SimpleChannel simpleChannel;
	private int packetIdCounter;
	private static final String PROTOCOL_VERSION = "1";

	@MappedMethod
	public void init() {
		MinecraftForge.EVENT_BUS.register(MainEventBus.class);
		FMLJavaModLoadingContext.get().getModEventBus().register(ModEventBus.class);
	}

	@MappedMethod
	public BlockRegistryObject registerBlock(Identifier identifier, Supplier<Block> supplier) {
		ModEventBus.BLOCKS.put(identifier, supplier);
		return new BlockRegistryObject(identifier);
	}

	@MappedMethod
	public BlockRegistryObject registerBlockWithBlockItem(Identifier identifier, Supplier<Block> supplier, CreativeModeTabHolder creativeModeTabHolder) {
		ModEventBus.BLOCKS.put(identifier, supplier);
		final BlockRegistryObject blockRegistryObject = new BlockRegistryObject(identifier);
		ModEventBus.BLOCK_ITEMS.put(identifier, () -> new BlockItemExtension(blockRegistryObject.get(), new ItemSettings()));
		creativeModeTabHolder.itemSuppliers.add(new ItemRegistryObject(identifier)::get);
		return blockRegistryObject;
	}

	@MappedMethod
	public ItemRegistryObject registerItem(Identifier identifier, Function<ItemSettings, Item> function, CreativeModeTabHolder creativeModeTabHolder) {
		ModEventBus.ITEMS.put(identifier, () -> function.apply(new ItemSettings()));
		final ItemRegistryObject itemRegistryObject = new ItemRegistryObject(identifier);
		creativeModeTabHolder.itemSuppliers.add(itemRegistryObject::get);
		return itemRegistryObject;
	}

	@MappedMethod
	public <T extends BlockEntityExtension> BlockEntityTypeRegistryObject<T> registerBlockEntityType(Identifier identifier, BiFunction<BlockPos, BlockState, T> function, Supplier<Block>... blockSuppliers) {
		ModEventBus.BLOCK_ENTITY_TYPES.put(identifier, () -> BlockEntityType.Builder.of((pos, state) -> function.apply(new BlockPos(pos), new BlockState(state)), HolderBase.convertArray(blockSuppliers, net.minecraft.world.level.block.Block[]::new)).build(null));
		return new BlockEntityTypeRegistryObject<>(identifier);
	}

	@MappedMethod
	public <T extends EntityExtension> EntityTypeRegistryObject<T> registerEntityType(Identifier identifier, BiFunction<EntityType<?>, World, T> function, float width, float height) {
		ModEventBus.ENTITY_TYPES.put(identifier, () -> net.minecraft.world.entity.EntityType.Builder.of(getEntityFactory(function), MobCategory.MISC).sized(width, height).build(identifier.toString()));
		return new EntityTypeRegistryObject<>(identifier);
	}

	private <T extends EntityExtension> net.minecraft.world.entity.EntityType.EntityFactory<T> getEntityFactory(BiFunction<EntityType<?>, World, T> function) {
		return (entityType, world) -> function.apply(new EntityType<>(entityType), new World(world));
	}

	@MappedMethod
	public CreativeModeTabHolder createCreativeModeTabHolder(Identifier identifier, Supplier<ItemStack> iconSupplier) {
		final CreativeModeTabHolder creativeModeTabHolder = new CreativeModeTabHolder(identifier.data, iconSupplier);
		ModEventBus.CREATIVE_MODE_TABS.add(creativeModeTabHolder);
		return creativeModeTabHolder;
	}

	@MappedMethod
	public SoundEventRegistryObject registerSoundEvent(Identifier identifier) {
		ModEventBus.SOUND_EVENTS.put(identifier, () -> SoundEvent.createVariableRangeEvent(identifier));
		return new SoundEventRegistryObject(identifier);
	}

	@MappedMethod
	public void setupPackets(Identifier identifier) {
		simpleChannel = NetworkRegistry.newSimpleChannel(identifier.data, () -> PROTOCOL_VERSION, Registry::validProtocol, Registry::validProtocol);
	}

	@MappedMethod
	public <T extends PacketHandler> void registerPacket(Class<T> classObject, Function<PacketBuffer, T> getInstance) {
		if (simpleChannel != null) {
			simpleChannel.registerMessage(packetIdCounter++, classObject, (packetHandler, packetBuffer) -> {
				packetBuffer.writeUtf(classObject.getName());
				packetHandler.write(new PacketBuffer(packetBuffer));
			}, packetBuffer -> {
				packetBuffer.readUtf();
				return getInstance.apply(new PacketBuffer(packetBuffer));
			}, (packetHandler, contextSupplier) -> {
				final NetworkEvent.Context context = contextSupplier.get();
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
			});
		}
	}

	@MappedMethod
	public <T extends PacketHandler> void sendPacketToClient(ServerPlayerEntity serverPlayerEntity, T data) {
		if (simpleChannel != null) {
			simpleChannel.send(PacketDistributor.PLAYER.with(() -> serverPlayerEntity.data), data);
		}
	}

	private static boolean validProtocol(String text) {
		return text.equals(PROTOCOL_VERSION) || text.equals(NetworkRegistry.ACCEPTVANILLA) || text.equals(NetworkRegistry.ABSENT.version());
	}
}
