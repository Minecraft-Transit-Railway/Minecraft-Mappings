package org.mtr.mapping.mapper;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraftforge.common.util.Constants;
import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.*;

public abstract class BlockEntityExtension extends BlockEntityAbstractMapping implements ITickableTileEntity {

	@MappedMethod
	public BlockEntityExtension(BlockEntityType<?> type, BlockPos blockPos, BlockState state) {
		super(type);
	}

	@Deprecated
	@Override
	public final CompoundNBT save(CompoundNBT compoundTag) {
		super.save(compoundTag);
		writeCompoundTag(new CompoundTag(compoundTag));
		return compoundTag;
	}

	@Deprecated
	@Override
	public final void load(net.minecraft.block.BlockState blockState, CompoundNBT compoundTag) {
		super.load(blockState, compoundTag);
		readCompoundTag(new CompoundTag(compoundTag));
	}

	@MappedMethod
	public void writeCompoundTag(CompoundTag compoundTag) {
	}

	@MappedMethod
	public void readCompoundTag(CompoundTag compoundTag) {
	}

	@Deprecated
	@Override
	public final CompoundNBT getUpdateTag() {
		final CompoundNBT compoundTag = new CompoundNBT();
		writeCompoundTag(new CompoundTag(compoundTag));
		return compoundTag;
	}

	@Deprecated
	@Override
	public final void handleUpdateTag(net.minecraft.block.BlockState blockState, CompoundNBT compoundTag) {
		readCompoundTag(new CompoundTag(compoundTag));
	}

	@Deprecated
	@Override
	public final SUpdateTileEntityPacket getUpdatePacket() {
		final CompoundNBT compoundNBT = new CompoundNBT();
		writeCompoundTag(new CompoundTag(compoundNBT));
		return new SUpdateTileEntityPacket(worldPosition, -1, compoundNBT);
	}

	@Deprecated
	@Override
	public final void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
		readCompoundTag(new CompoundTag(pkt.getTag()));
	}

	@Deprecated
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
		if (level != null && !level.isClientSide) {
			level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Constants.BlockFlags.BLOCK_UPDATE);
		}
	}

	@MappedMethod
	public double getRenderDistance2() {
		return super.getViewDistance();
	}

	@Deprecated
	@Override
	public final double getViewDistance() {
		return getRenderDistance2();
	}
}
