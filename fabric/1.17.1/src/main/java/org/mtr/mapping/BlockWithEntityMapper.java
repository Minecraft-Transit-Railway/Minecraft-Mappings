package org.mtr.mapping;

import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.world.World;
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
	public final BlockEntity createBlockEntity(net.minecraft.util.math.BlockPos pos, net.minecraft.block.BlockState state) {
		return createBlockEntity(new BlockPos(pos), new BlockState(state));
	}

	@MappedMethod
	public abstract BlockEntityMapper createBlockEntity(BlockPos blockPos, BlockState blockState);

	@Override
	public final <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, net.minecraft.block.BlockState state, net.minecraft.block.entity.BlockEntityType<T> type) {
		final net.minecraft.block.entity.BlockEntityType<BlockEntityMapper> blockEntityType = getBlockEntityTypeForTicking().data;
		if (blockEntityType == null) {
			return super.getTicker(world, state, type);
		} else {
			return checkType(type, blockEntityType, (world1, pos, state1, blockEntity) -> blockEntity.blockEntityTick());
		}
	}
}
