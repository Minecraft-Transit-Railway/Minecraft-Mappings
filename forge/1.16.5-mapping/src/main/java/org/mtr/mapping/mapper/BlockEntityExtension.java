package org.mtr.mapping.mapper;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.*;

public abstract class BlockEntityExtension extends BlockEntityAbstractMapping implements ITickableTileEntity {

	@MappedMethod
	public BlockEntityExtension(BlockEntityType<?> type, BlockPos blockPos, BlockState state) {
		super(type);
	}

	@Override
	public final CompoundTag save2(CompoundTag compoundTag) {
		super.save2(compoundTag);
		writeCompoundTag(compoundTag);
		return compoundTag;
	}

	@Override
	public final void load2(BlockState blockState, CompoundTag compoundTag) {
		super.load2(blockState, compoundTag);
		readCompoundTag(compoundTag);
	}

	@MappedMethod
	public void writeCompoundTag(CompoundTag compoundTag) {
	}

	@MappedMethod
	public void readCompoundTag(CompoundTag compoundTag) {
	}

	@Override
	public final CompoundTag getUpdateTag2() {
		final CompoundTag compoundTag = new CompoundTag();
		writeCompoundTag(compoundTag);
		return compoundTag;
	}

	@Override
	public final void handleUpdateTag2(BlockState blockState, CompoundTag compoundTag) {
		readCompoundTag(compoundTag);
	}

	@Override
	public final SUpdateTileEntityPacket getUpdatePacket2() {
		final CompoundNBT compoundNBT = new CompoundNBT();
		writeCompoundTag(new CompoundTag(compoundNBT));
		return new SUpdateTileEntityPacket(getBlockPos(), -1, compoundNBT);
	}

	@Override
	public final void onDataPacket2(NetworkManager net, SUpdateTileEntityPacket pkt) {
		readCompoundTag(new CompoundTag(pkt.getTag()));
	}

	@Override
	public final void tick() {
		blockEntityTick();
	}

	@MappedMethod
	public void blockEntityTick() {
	}
}
