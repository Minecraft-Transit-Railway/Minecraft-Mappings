package org.mtr.mapping.mapper;

import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraftforge.common.extensions.IForgeBlockEntity;
import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.*;

public abstract class BlockEntityExtension extends BlockEntityAbstractMapping implements IForgeBlockEntity {

	@MappedMethod
	public BlockEntityExtension(BlockEntityType<?> type, BlockPos blockPos, BlockState blockState) {
		super(type, blockPos, blockState);
	}

	@Override
	public final CompoundTag save2(CompoundTag compoundTag) {
		super.save2(compoundTag);
		writeCompoundTag(compoundTag);
		return compoundTag;
	}

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

	@Override
	public final CompoundTag getUpdateTag2() {
		final CompoundTag compoundTag = new CompoundTag();
		writeCompoundTag(compoundTag);
		return compoundTag;
	}

	@Override
	public final void handleUpdateTag2(CompoundTag tag) {
		readCompoundTag(new CompoundTag());
	}

	@Override
	public final ClientboundBlockEntityDataPacket getUpdatePacket2() {
		final net.minecraft.nbt.CompoundTag compoundTag = new net.minecraft.nbt.CompoundTag();
		writeCompoundTag(new CompoundTag(compoundTag));
		return new ClientboundBlockEntityDataPacket(getBlockPos(), -1, compoundTag);
	}

	@MappedMethod
	public void blockEntityTick() {
	}
}
