package org.mtr.mapping;

import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Tickable;
import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.BlockEntityType;
import org.mtr.mapping.holder.BlockPos;
import org.mtr.mapping.holder.BlockState;
import org.mtr.mapping.holder.CompoundTag;

public abstract class BlockEntityMapper extends BlockEntity implements BlockEntityClientSerializable, Tickable {

	@MappedMethod
	public BlockEntityMapper(BlockEntityType<?> type, BlockPos blockPos, BlockState state) {
		super(type.data);
	}

	@Override
	public final NbtCompound writeNbt(NbtCompound nbt) {
		super.writeNbt(nbt);
		writeCompoundTag(new CompoundTag(nbt));
		return nbt;
	}

	@Override
	public final void fromTag(net.minecraft.block.BlockState state, NbtCompound nbt) {
		super.fromTag(state, nbt);
		readCompoundTag(new CompoundTag(nbt));
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

	@MappedMethod
	@Override
	public void markDirty() {
		super.markDirty();
	}

	@Override
	public final void tick() {
		blockEntityTick();
	}

	@MappedMethod
	public void blockEntityTick() {
	}
}
