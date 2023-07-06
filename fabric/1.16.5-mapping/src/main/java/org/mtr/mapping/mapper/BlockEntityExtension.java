package org.mtr.mapping.mapper;

import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Tickable;
import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.*;

public abstract class BlockEntityExtension extends BlockEntityAbstractMapping implements BlockEntityClientSerializable, Tickable {

	@MappedMethod
	public BlockEntityExtension(BlockEntityType<?> type, BlockPos blockPos, BlockState state) {
		super(type);
	}

	@Override
	public final CompoundTag writeNbt2(CompoundTag nbt) {
		super.writeNbt2(nbt);
		writeCompoundTag(nbt);
		return nbt;
	}

	@Override
	public final void fromTag2(BlockState state, CompoundTag tag) {
		super.fromTag2(state, tag);
		readCompoundTag(tag);
	}

	@MappedMethod
	public void writeCompoundTag(CompoundTag compoundTag) {
	}

	@MappedMethod
	public void readCompoundTag(CompoundTag compoundTag) {
	}

	@Override
	public final NbtCompound toClientTag(NbtCompound tag) {
		return writeNbt(tag);
	}

	@Override
	public final void fromClientTag(NbtCompound tag) {
		fromTag(getCachedState(), tag);
	}

	@Override
	public final void tick() {
		blockEntityTick();
	}

	@MappedMethod
	public void blockEntityTick() {
	}

	@MappedMethod
	@Override
	public void markDirty2() {
		super.markDirty2();
		sync();
	}
}
