package org.mtr.mapping.mapper;

import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.extensions.IForgeBlockEntity;
import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.*;

public abstract class BlockEntityExtension extends BlockEntityAbstractMapping implements IForgeBlockEntity {

	@MappedMethod
	public BlockEntityExtension(BlockEntityType<?> type, BlockPos blockPos, BlockState blockState) {
		super(type, blockPos, blockState);
	}

	@Deprecated
	@Override
	protected final void saveAdditional2(CompoundTag compoundTag) {
		super.saveAdditional2(compoundTag);
		writeCompoundTag(compoundTag);
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
		final CompoundTag compoundTag = new CompoundTag();
		writeCompoundTag(compoundTag);
		return compoundTag;
	}

	@Deprecated
	@Override
	public final void handleUpdateTag2(CompoundTag compoundTag) {
		readCompoundTag(compoundTag);
	}

	@Deprecated
	@Override
	public final Packet<ClientGamePacketListener> getUpdatePacket2() {
		return ClientboundBlockEntityDataPacket.create(this);
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
}
