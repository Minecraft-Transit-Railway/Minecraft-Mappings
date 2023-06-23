package org.mtr.mapping.mapper;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;
import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.BlockEntityType;
import org.mtr.mapping.holder.BlockPos;
import org.mtr.mapping.holder.BlockState;

import javax.annotation.Nullable;

public abstract class BlockWithEntity extends Block {

	@MappedMethod
	public BlockWithEntity(Block.Properties properties) {
		super(properties);
	}

	@MappedMethod
	public abstract <T extends BlockEntity> BlockEntityType<T> getBlockEntityTypeForTicking();

	@Override
	public final boolean hasTileEntity(net.minecraft.block.BlockState state) {
		return true;
	}

	@Override
	public final TileEntity createTileEntity(net.minecraft.block.BlockState state, IBlockReader world) {
		return createBlockEntity(null, null);
	}

	@MappedMethod
	public abstract BlockEntity createBlockEntity(@Nullable BlockPos blockPos, @Nullable BlockState state);
}
