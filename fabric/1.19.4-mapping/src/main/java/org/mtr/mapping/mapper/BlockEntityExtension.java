package org.mtr.mapping.mapper;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.*;

public abstract class BlockEntityExtension extends BlockEntityAbstractMapping {

	@MappedMethod
	public BlockEntityExtension(BlockEntityType<?> type, BlockPos blockPos, BlockState blockState) {
		super(type, blockPos, blockState);
	}

	@Override
	public final void writeNbt(NbtCompound nbt) {
		super.writeNbt(nbt);
		writeCompoundTag(new CompoundTag(nbt));
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
	public final Packet<ClientPlayPacketListener> toUpdatePacket2() {
		return BlockEntityUpdateS2CPacket.create(this);
	}

	@Override
	public final CompoundTag toInitialChunkDataNbt2() {
		return new CompoundTag(createNbt());
	}

	@MappedMethod
	public void blockEntityTick() {
	}

	@MappedMethod
	@Override
	public void markDirty2() {
		super.markDirty2();
		if (world != null && !world.isClient) {
			world.updateListeners(pos, getCachedState(), getCachedState(), net.minecraft.block.Block.NOTIFY_LISTENERS);
		}
	}
}
