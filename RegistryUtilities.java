package minecraftmappings;

import me.shedaniel.architectury.event.events.LifecycleEvent;
import me.shedaniel.architectury.event.events.PlayerEvent;
import me.shedaniel.architectury.event.events.TickEvent;
import me.shedaniel.architectury.registry.CreativeTabs;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.Collections;
import java.util.function.Consumer;
import java.util.function.Supplier;

public interface RegistryUtilities {

	static <T extends BlockEntityMapper> BlockEntityType<T> getBlockEntityType(Utilities.TileEntitySupplier<T> supplier, Block block) {
		return new BlockEntityType<>(() -> supplier.supplier(null, null), Collections.singleton(block), null);
	}

	static void registerPlayerJoinEvent(Consumer<ServerPlayer> consumer) {
		PlayerEvent.PLAYER_JOIN.register(consumer::accept);
	}

	static void registerPlayerQuitEvent(Consumer<ServerPlayer> consumer) {
		PlayerEvent.PLAYER_QUIT.register(consumer::accept);
	}

	static void registerPlayerChangeDimensionEvent(Consumer<ServerPlayer> consumer) {
		PlayerEvent.CHANGE_DIMENSION.register(((player, oldWorld, newWorld) -> consumer.accept(player)));
	}

	static void registerServerStartingEvent(Consumer<MinecraftServer> consumer) {
		LifecycleEvent.SERVER_STARTING.register(consumer::accept);
	}

	static void registerServerStoppingEvent(Consumer<MinecraftServer> consumer) {
		LifecycleEvent.SERVER_STOPPING.register(consumer::accept);
	}

	static void registerTickEvent(Consumer<MinecraftServer> consumer) {
		TickEvent.SERVER_PRE.register(consumer::accept);
	}

	static CreativeModeTab createCreativeTab(ResourceLocation name, Supplier<ItemStack> icon) {
		return CreativeTabs.create(name, icon);
	}
}
