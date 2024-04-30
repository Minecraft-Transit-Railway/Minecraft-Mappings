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
	public final net.minecraft.nbt.CompoundTag save(net.minecraft.nbt.CompoundTag compoundTag) {
		super.save(compoundTag);
		writeCompoundTag(new CompoundTag(compoundTag));
		return compoundTag;
	}

	@Deprecated
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

	@Deprecated
	@Override
	public final net.minecraft.nbt.CompoundTag getUpdateTag() {
		final net.minecraft.nbt.CompoundTag compoundTag = super.getUpdateTag();
		writeCompoundTag(new CompoundTag(compoundTag));
		return compoundTag;
	}

	@Deprecated
	@Override
	public final void handleUpdateTag(net.minecraft.nbt.CompoundTag tag) {
		readCompoundTag(new CompoundTag(tag));
	}

	@Deprecated
	@Override
	public final ClientboundBlockEntityDataPacket getUpdatePacket() {
		final net.minecraft.nbt.CompoundTag compoundTag = new net.minecraft.nbt.CompoundTag();
		writeCompoundTag(new CompoundTag(compoundTag));
		return new ClientboundBlockEntityDataPacket(worldPosition, -1, compoundTag);
	}

	@Deprecated
	@Override
	public final void onDataPacket(Connection connection, ClientboundBlockEntityDataPacket packet) {
		readCompoundTag(new CompoundTag(packet.getTag()));
	}

	@MappedMethod
	public void blockEntityTick() {
	}

	@MappedMethod
	@Override
	public void markDirty2() {
		super.markDirty2();
		final net.minecraft.world.level.block.state.BlockState blockState = getBlockState();
		if (level != null && !level.isClientSide && blockState != null) {
			level.sendBlockUpdated(worldPosition, blockState, blockState, Block.UPDATE_CLIENTS);
		}
	}

	@MappedMethod
	public double getRenderDistance2() {
		return 0;
	}
}
