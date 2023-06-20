package org.mtr.mapping;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.BlockEntityType;
import org.mtr.mapping.holder.BlockPos;
import org.mtr.mapping.holder.BlockState;
import org.mtr.mapping.holder.CompoundTag;

public abstract class BlockEntityMapper extends BlockEntity {

	@MappedMethod
	public BlockEntityMapper(BlockEntityType<?> type, BlockPos blockPos, BlockState blockState) {
		super(type.data, blockPos.data, blockState.data);
	}

	@Override
	public final void writeNbt(NbtCompound nbt) {
		super.writeNbt(nbt);
		writeCompoundTag(new CompoundTag(nbt));
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
	public final Packet<ClientPlayPacketListener> toUpdatePacket() {
		return BlockEntityUpdateS2CPacket.create(this);
	}

	@Override
	public final NbtCompound toInitialChunkDataNbt() {
		return createNbt();
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
