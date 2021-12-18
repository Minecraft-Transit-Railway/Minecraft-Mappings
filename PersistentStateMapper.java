package minecraftmappings;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.function.Supplier;

public abstract class PersistentStateMapper extends SavedData {

	public PersistentStateMapper(String name) {
		super(name);
	}

	@Override
	public abstract void load(CompoundTag compoundTag);

	protected static <T extends PersistentStateMapper> T getInstance(Level world, Supplier<T> supplier, String name) {
		return world instanceof ServerLevel ? ((ServerLevel) world).getDataStorage().computeIfAbsent(supplier, name) : null;
	}
}
