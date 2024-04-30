package org.mtr.mapping.mapper;

import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
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
	protected final void saveAdditional(net.minecraft.nbt.CompoundTag compoundTag) {
		super.saveAdditional(compoundTag);
		writeCompoundTag(new CompoundTag(compoundTag));
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
	public final void handleUpdateTag(net.minecraft.nbt.CompoundTag compoundTag) {
		readCompoundTag(new CompoundTag(compoundTag));
	}

	@Deprecated
	@Override
	public final Packet<ClientGamePacketListener> getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
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
