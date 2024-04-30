package org.mtr.mapping.mapper;

import net.minecraft.nbt.NbtCompound;
import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.CompoundTag;
import org.mtr.mapping.holder.PersistentStateAbstractMapping;
import org.mtr.mapping.holder.ServerWorld;

import java.util.function.Supplier;

public abstract class PersistenceStateExtension extends PersistentStateAbstractMapping {

	@MappedMethod
	public PersistenceStateExtension(String key) {
		super(key);
	}

	@Deprecated
	@Override
	public final void fromTag(NbtCompound tag) {
		readNbt(new CompoundTag(tag));
	}

	@MappedMethod
	public abstract void readNbt(CompoundTag tag);

	@MappedMethod
	public static PersistenceStateExtension register(ServerWorld serverWorld, Supplier<PersistenceStateExtension> supplier, String modId) {
		return serverWorld.data.getPersistentStateManager().getOrCreate(supplier, modId);
	}
}
