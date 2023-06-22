package org.mtr.mapping.mapper;

import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.minecraft.nbt.NbtCompound;
import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.BlockEntityType;
import org.mtr.mapping.holder.BlockPos;
import org.mtr.mapping.holder.BlockState;
import org.mtr.mapping.holder.CompoundTag;

public abstract class BlockEntity extends net.minecraft.block.entity.BlockEntity implements BlockEntityClientSerializable {

	@MappedMethod
	public BlockEntity(BlockEntityType<?> type, BlockPos blockPos, BlockState blockState) {
		super(type.data, blockPos.data, blockState.data);
	}

	@Override
	public final NbtCompound writeNbt(NbtCompound nbt) {
		super.writeNbt(nbt);
		writeCompoundTag(new CompoundTag(nbt));
		return nbt;
	}

	@Override
	public final void readNbt(NbtCompound nbt) {
		super.readNbt(nbt);
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
		readNbt(tag);
	}

	@MappedMethod
	@Override
	public void markDirty() {
		super.markDirty();
	}

	@MappedMethod
	public void blockEntityTick() {
	}
}
