package org.mtr.mapping.mapper;

import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.Block;
import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.*;

public abstract class BlockEntityExtension extends BlockEntityAbstractMapping {

	@MappedMethod
	public BlockEntityExtension(BlockEntityType<?> type, BlockPos blockPos, BlockState blockState) {
		super(type, blockPos, blockState);
	}

	@Deprecated
	@Override
	public final CompoundTag save2(CompoundTag compoundTag) {
		super.save2(compoundTag);
		writeCompoundTag(compoundTag);
		return compoundTag;
	}

	@Deprecated
	@Override
	public final void load2(CompoundTag compoundTag) {
		super.load2(compoundTag);
		readCompoundTag(compoundTag);
	}

	@MappedMethod
	public void writeCompoundTag(CompoundTag compoundTag) {
	}

	@MappedMethod
	public void readCompoundTag(CompoundTag compoundTag) {
	}

	@Deprecated
	@Override
	public final CompoundTag getUpdateTag2() {
		final CompoundTag compoundTag = super.getUpdateTag2();
		writeCompoundTag(compoundTag);
		return compoundTag;
	}

	@Deprecated
	@Override
	public final void handleUpdateTag2(CompoundTag tag) {
		readCompoundTag(tag);
	}

	@Deprecated
	@Override
	public final ClientboundBlockEntityDataPacket getUpdatePacket2() {
		final net.minecraft.nbt.CompoundTag compoundTag = new net.minecraft.nbt.CompoundTag();
		writeCompoundTag(new CompoundTag(compoundTag));
		return new ClientboundBlockEntityDataPacket(worldPosition, -1, compoundTag);
	}

	@Deprecated
	@Override
	public final void onDataPacket2(Connection connection, ClientboundBlockEntityDataPacket packet) {
		readCompoundTag(new CompoundTag(packet.getTag()));
	}

	@MappedMethod
	public void blockEntityTick() {
	}

	@MappedMethod
	@Override
	public void markDirty2() {
		super.markDirty2();
		if (level != null && !level.isClientSide) {
			level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
		}
	}

	@MappedMethod
	public double getRenderDistance3() {
		return 0;
	}
}
