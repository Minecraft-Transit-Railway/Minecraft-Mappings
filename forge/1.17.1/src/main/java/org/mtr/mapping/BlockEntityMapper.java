package org.mtr.mapping;

import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.extensions.IForgeBlockEntity;
import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.BlockEntityType;
import org.mtr.mapping.holder.BlockPos;
import org.mtr.mapping.holder.BlockState;
import org.mtr.mapping.holder.CompoundTag;

public abstract class BlockEntityMapper extends BlockEntity implements IForgeBlockEntity {

	@MappedMethod
	public BlockEntityMapper(BlockEntityType<?> type, BlockPos blockPos, BlockState blockState) {
		super(type.data, blockPos.data, blockState.data);
	}

	@Override
	public final net.minecraft.nbt.CompoundTag save(net.minecraft.nbt.CompoundTag compoundTag) {
		super.save(compoundTag);
		writeCompoundTag(new CompoundTag(compoundTag));
		return compoundTag;
	}

	@Override
	public final void load(net.minecraft.nbt.CompoundTag compoundTag) {
		super.load(compoundTag);
		readCompoundTag(new CompoundTag(compoundTag));
	}

	@MappedMethod
	public void writeCompoundTag(CompoundTag compoundTag) {
	}

	@MappedMethod
	public void readCompoundTag(CompoundTag compoundTag) {
	}

	@Override
	public final net.minecraft.nbt.CompoundTag getUpdateTag() {
		net.minecraft.nbt.CompoundTag compoundTag = new net.minecraft.nbt.CompoundTag();
		writeCompoundTag(new CompoundTag(compoundTag));
		return compoundTag;
	}

	@Override
	public final void handleUpdateTag(net.minecraft.nbt.CompoundTag tag) {
		readCompoundTag(new CompoundTag(new net.minecraft.nbt.CompoundTag()));
	}

	@Override
	public final ClientboundBlockEntityDataPacket getUpdatePacket() {
		final net.minecraft.nbt.CompoundTag compoundTag = new net.minecraft.nbt.CompoundTag();
		writeCompoundTag(new CompoundTag(compoundTag));
		return new ClientboundBlockEntityDataPacket(getBlockPos(), -1, compoundTag);
	}

	@MappedMethod
	public void markDirty() {
		setChanged();
	}

	@MappedMethod
	public void blockEntityTick() {
	}
}
