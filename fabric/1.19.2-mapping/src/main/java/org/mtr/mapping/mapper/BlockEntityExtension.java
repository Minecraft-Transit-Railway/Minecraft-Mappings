package org.mtr.mapping.mapper;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.*;

public abstract class BlockEntityExtension extends BlockEntityAbstractMapping {

	@MappedMethod
	public BlockEntityExtension(BlockEntityType<?> type, BlockPos blockPos, BlockState blockState) {
		super(type, blockPos, blockState);
	}

	@Deprecated
	@Override
	protected final void writeNbt(NbtCompound nbt) {
		super.writeNbt(nbt);
		writeCompoundTag(new CompoundTag(nbt));
	}

	@Deprecated
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

	@Deprecated
	@Override
	public final Packet<ClientPlayPacketListener> toUpdatePacket() {
		return BlockEntityUpdateS2CPacket.create(this);
	}

	@Deprecated
	@Override
	public final NbtCompound toInitialChunkDataNbt() {
		return createNbt();
	}

	@MappedMethod
	public void blockEntityTick() {
	}

	@MappedMethod
	@Override
	public void markDirty2() {
		super.markDirty2();
		final net.minecraft.block.BlockState blockState = getCachedState();
		if (world != null && !world.isClient && blockState != null) {
			world.updateListeners(pos, blockState, blockState, net.minecraft.block.Block.NOTIFY_LISTENERS);
		}
	}

	@MappedMethod
	public double getRenderDistance2() {
		return 0;
	}
}
