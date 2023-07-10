package org.mtr.mapping.registry;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.*;
import org.mtr.mapping.mapper.BlockEntityExtension;
import org.mtr.mapping.mapper.BlockItem;
import org.mtr.mapping.mapper.ItemHelper;
import org.mtr.mapping.tool.DummyClass;
import org.mtr.mapping.tool.HolderBase;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public final class Registry extends DummyClass {

	static SimpleChannel simpleChannel;
	private static int packetIdCounter;
	private static final String PROTOCOL_VERSION = "1";

	@MappedMethod
	public static void init() {
		MinecraftForge.EVENT_BUS.register(MainEventBus.class);
		FMLJavaModLoadingContext.get().getModEventBus().register(ModEventBus.class);
	}

	@MappedMethod
	public static BlockRegistryObject registerBlock(ResourceLocation resourceLocation, Supplier<Block> supplier) {
		ModEventBus.BLOCKS.add(supplier);
		return new BlockRegistryObject(resourceLocation);
	}

	@MappedMethod
	public static BlockRegistryObject registerBlockWithBlockItem(ResourceLocation resourceLocation, Supplier<Block> supplier) {
		ModEventBus.BLOCKS.add(supplier);
		final BlockRegistryObject blockRegistryObject = new BlockRegistryObject(resourceLocation);
		ModEventBus.BLOCK_ITEMS.add(() -> new BlockItem(blockRegistryObject.get(), new ItemHelper.Properties()));
		return blockRegistryObject;
	}

	@MappedMethod
	public static ItemRegistryObject registerItem(ResourceLocation resourceLocation, Supplier<Item> supplier) {
		ModEventBus.ITEMS.add(supplier);
		return new ItemRegistryObject(resourceLocation);
	}

	@MappedMethod
	public static <T extends BlockEntityExtension> BlockEntityTypeRegistryObject<T> registerBlockEntityType(ResourceLocation resourceLocation, BiFunction<BlockPos, BlockState, T> function, Block... blocks) {
		ModEventBus.BLOCK_ENTITY_TYPES.add(() -> BlockEntityType.Builder.of((pos, state) -> function.apply(new BlockPos(pos), new BlockState(state)), HolderBase.convertArray(blocks, net.minecraft.world.level.block.Block[]::new)).build(null));
		return new BlockEntityTypeRegistryObject<>(resourceLocation);
	}

	@MappedMethod
	public static CreativeModeTabHolder createCreativeModeTabHolder(ResourceLocation resourceLocation, Supplier<ItemStack> iconSupplier) {
		return new CreativeModeTabHolder(resourceLocation, iconSupplier);
	}

	@MappedMethod
	public static void addItemsToCreativeModeTab(CreativeModeTabHolder creativeModeTabHolder, ItemRegistryObject... itemRegistryObjects) {
	}

	@MappedMethod
	public static void setupPackets(ResourceLocation resourceLocation) {
		simpleChannel = NetworkRegistry.newSimpleChannel(resourceLocation.data, () -> PROTOCOL_VERSION, Registry::validProtocol, Registry::validProtocol);
	}

	@MappedMethod
	public static <T extends PacketHandler> void registerPacket(Class<T> classObject, Function<PacketBuffer, T> getInstance) {
		if (simpleChannel != null) {
			simpleChannel.registerMessage(packetIdCounter++, classObject, (packetHandler, packetBuffer) -> {
				packetBuffer.writeUtf(classObject.getName());
				packetHandler.write(new PacketBuffer(packetBuffer));
			}, packetBuffer -> {
				packetBuffer.readUtf();
				return getInstance.apply(new PacketBuffer(packetBuffer));
			}, (packetHandler, contextSupplier) -> contextSupplier.get().enqueueWork(packetHandler::run));
		}
	}

	@MappedMethod
	public static <T extends PacketHandler> void sendPacketToClient(ServerPlayerEntity serverPlayerEntity, T data) {
		if (simpleChannel != null) {
			simpleChannel.send(PacketDistributor.PLAYER.with(() -> serverPlayerEntity.data), data);
		}
	}

	private static boolean validProtocol(String text) {
		return text.equals(PROTOCOL_VERSION) || text.equals(NetworkRegistry.ACCEPTVANILLA) || text.equals(NetworkRegistry.ABSENT);
	}
}