package org.mtr.mapping.mapper;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.BlockEntityType;
import org.mtr.mapping.holder.BlockPos;
import org.mtr.mapping.holder.BlockState;
import org.mtr.mapping.holder.CompoundTag;

public abstract class BlockEntity extends TileEntity implements ITickableTileEntity {

	@MappedMethod
	public BlockEntity(BlockEntityType<?> type, BlockPos blockPos, BlockState state) {
		super(type.data);
	}

	@Override
	public final CompoundNBT save(CompoundNBT nbtCompound) {
		super.save(nbtCompound);
		writeCompoundTag(new CompoundTag(nbtCompound));
		return nbtCompound;
	}

	@Override
	public final void load(net.minecraft.block.BlockState blockState, CompoundNBT nbtCompound) {
		super.load(blockState, nbtCompound);
		readCompoundTag(new CompoundTag(nbtCompound));
	}

	@MappedMethod
	public void writeCompoundTag(CompoundTag compoundTag) {
	}

	@MappedMethod
	public void readCompoundTag(CompoundTag compoundTag) {
	}

	@Override
	public final CompoundNBT getUpdateTag() {
		final CompoundNBT compoundNBT = new CompoundNBT();
		writeCompoundTag(new CompoundTag(compoundNBT));
		return compoundNBT;
	}

	@Override
	public final void handleUpdateTag(net.minecraft.block.BlockState state, CompoundNBT tag) {
		readCompoundTag(new CompoundTag(tag));
	}

	@Override
	public final SUpdateTileEntityPacket getUpdatePacket() {
		final CompoundNBT compoundNBT = new CompoundNBT();
		writeCompoundTag(new CompoundTag(compoundNBT));
		return new SUpdateTileEntityPacket(getBlockPos(), -1, compoundNBT);
	}

	@Override
	public final void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
		readCompoundTag(new CompoundTag(pkt.getTag()));
	}

	@MappedMethod
	public void markDirty() {
		setChanged();
	}

	@Override
	public final void tick() {
		blockEntityTick();
	}

	@MappedMethod
	public void blockEntityTick() {
	}
}
