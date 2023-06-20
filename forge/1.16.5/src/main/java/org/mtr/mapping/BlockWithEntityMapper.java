package org.mtr.mapping;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;
import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.BlockEntityType;
import org.mtr.mapping.holder.BlockPos;
import org.mtr.mapping.holder.BlockState;

import javax.annotation.Nullable;

public abstract class BlockWithEntityMapper extends Block {

	@MappedMethod
	public BlockWithEntityMapper(PropertiesMapper propertiesMapper) {
		super(propertiesMapper.blockSettings);
	}

	@MappedMethod
	public abstract <T extends BlockEntityMapper> BlockEntityType<T> getBlockEntityTypeForTicking();

	@Override
	public final boolean hasTileEntity(net.minecraft.block.BlockState state) {
		return true;
	}

	@Override
	public final TileEntity createTileEntity(net.minecraft.block.BlockState state, IBlockReader world) {
		return createBlockEntity(null, null);
	}

	@MappedMethod
	public abstract BlockEntityMapper createBlockEntity(@Nullable BlockPos blockPos, @Nullable BlockState state);
}
