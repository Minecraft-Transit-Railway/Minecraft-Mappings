package org.mtr.mapping;

import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.world.BlockView;
import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.BlockEntityType;
import org.mtr.mapping.holder.BlockPos;
import org.mtr.mapping.holder.BlockState;

public abstract class BlockWithEntityMapper extends BlockWithEntity {

	@MappedMethod
	public BlockWithEntityMapper(PropertiesMapper propertiesMapper) {
		super(propertiesMapper.blockSettings);
	}

	@MappedMethod
	public abstract <T extends BlockEntityMapper> BlockEntityType<T> getBlockEntityTypeForTicking();

	@Override
	public final BlockEntity createBlockEntity(BlockView world) {
		return createBlockEntity(null, null);
	}

	@MappedMethod
	public abstract BlockEntityMapper createBlockEntity(BlockPos blockPos, BlockState blockState);
}
