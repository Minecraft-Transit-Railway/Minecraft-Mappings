package org.mtr.mapping.mapper;

import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.BlockEntityType;
import org.mtr.mapping.holder.BlockPos;
import org.mtr.mapping.holder.BlockState;
import org.mtr.mapping.holder.BlockView;

import javax.annotation.Nullable;

public abstract class BlockWithEntity extends BlockExtension {

	@MappedMethod
	public BlockWithEntity(BlockExtension.Properties properties) {
		super(properties);
	}

	@MappedMethod
	public abstract <T extends BlockEntityExtension> BlockEntityType<T> getBlockEntityTypeForTicking();

	@Override
	public final boolean hasTileEntity2(BlockState state) {
		return true;
	}

	@Override
	public final org.mtr.mapping.holder.BlockEntity createTileEntity2(BlockState state, BlockView world) {
		return new org.mtr.mapping.holder.BlockEntity(createBlockEntity(null, null));
	}

	@MappedMethod
	public abstract BlockEntityExtension createBlockEntity(@Nullable BlockPos blockPos, @Nullable BlockState state);
}
