package @package@;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
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

	static Abilities getAbilities(Player player) {
		return player.getAbilities();
	}

	static void scheduleBlockTick(Level world, BlockPos pos, Block block, int ticks) {
		world.scheduleTick(pos, block, ticks);
	}

	static boolean entityRemoved(Entity entity) {
		return entity == null || entity.isRemoved();
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

	static void sendCommand(MinecraftServer server, CommandSourceStack commandSourceStack, String command) {
		server.getCommands().performCommand(commandSourceStack, command);
	}

	static CreativeModeTab getDefaultTab() {
		return CreativeModeTab.TAB_MISC;
	}

	static SoundEvent unwrapSoundEvent(SoundEvent soundEvent) {
		return soundEvent;
	}

	@FunctionalInterface
	interface TileEntitySupplier<T extends BlockEntityMapper> {
		T supplier(BlockPos pos, BlockState state);
	}
}
