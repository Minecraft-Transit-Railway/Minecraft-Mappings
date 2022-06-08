package @package@;

import net.minecraft.core.BlockPos;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Optional;
import java.util.function.Function;

public interface Utilities {

	static float getYaw(Entity entity) {
		return entity.yRot;
	}

	static void setYaw(Entity entity, float yaw) {
		entity.yRot = yaw;
	}

	static void incrementYaw(Entity entity, float yaw) {
		entity.yRot += yaw;
	}

	static boolean isHolding(Player player, Function<Item, Boolean> predicate) {
		return player.isHolding(predicate::apply);
	}

	static Inventory getInventory(Player player) {
		return player.inventory;
	}

	static Abilities getAbilities(Player player) {
		return player.abilities;
	}

	static void scheduleBlockTick(Level world, BlockPos pos, Block block, int ticks) {
		world.getBlockTicks().scheduleTick(pos, block, ticks);
	}

	static boolean entityRemoved(Entity entity) {
		return entity == null || entity.removed;
	}

	static InputStream getInputStream(Resource resource) throws IOException {
		return resource.getInputStream();
	}

	static InputStream getInputStream(Optional<Resource> optionalResource) throws IOException {
		if (optionalResource.isPresent()) {
			return optionalResource.get().getInputStream();
		} else {
			return IOUtils.toInputStream("", Charset.defaultCharset());
		}
	}

	static void closeResource(Resource resource) throws IOException {
		resource.close();
	}

	@FunctionalInterface
	interface TileEntitySupplier<T extends BlockEntityMapper> {
		T supplier(BlockPos pos, BlockState state);
	}
}
