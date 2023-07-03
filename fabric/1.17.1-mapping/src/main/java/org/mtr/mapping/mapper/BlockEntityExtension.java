package org.mtr.mapping.mapper;

import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.minecraft.nbt.NbtCompound;
import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.*;

public abstract class BlockEntityExtension extends BlockEntityAbstractMapping implements BlockEntityClientSerializable {

	@MappedMethod
	public BlockEntityExtension(BlockEntityType<?> type, BlockPos blockPos, BlockState blockState) {
		super(type, blockPos, blockState);
	}

	@Override
	public final CompoundTag writeNbt2(CompoundTag nbt) {
		super.writeNbt2(nbt);
		writeCompoundTag(nbt);
		return nbt;
	}

	@Override
	public final void readNbt2(CompoundTag nbt) {
		super.readNbt2(nbt);
		readCompoundTag(nbt);
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
	public void blockEntityTick() {
	}
}
