package minecraftmappings;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Function;

public interface Utilities {

	static float getYaw(Entity entity) {
		return entity.getYRot();
	}

	static void setYaw(Entity entity, float yaw) {
		entity.setYRot(yaw);
	}

	static void incrementYaw(Entity entity, float yaw) {
		setYaw(entity, entity.getYRot() + yaw);
	}

	static boolean isHolding(Player player, Function<Item, Boolean> predicate) {
		return player.isHolding(itemStack -> predicate.apply(itemStack.getItem()));
	}

	static Inventory getInventory(Player player) {
		return player.getInventory();
	}

	static void scheduleBlockTick(Level world, BlockPos pos, Block block, int ticks) {
		world.scheduleTick(pos, block, ticks);
	}

	@FunctionalInterface
	interface TileEntitySupplier<T extends BlockEntityMapper> {
		T supplier(BlockPos pos, BlockState state);
	}
}
