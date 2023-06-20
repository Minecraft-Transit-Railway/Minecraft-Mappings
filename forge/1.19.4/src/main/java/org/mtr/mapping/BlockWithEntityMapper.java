package org.mtr.mapping;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.BlockEntityType;
import org.mtr.mapping.holder.BlockPos;
import org.mtr.mapping.holder.BlockState;

public abstract class BlockWithEntityMapper extends Block implements EntityBlock {

	@MappedMethod
	public BlockWithEntityMapper(PropertiesMapper propertiesMapper) {
		super(propertiesMapper.blockSettings);
	}

	@MappedMethod
	public abstract <T extends BlockEntityMapper> BlockEntityType<T> getBlockEntityTypeForTicking();

	@Override
	public final BlockEntity newBlockEntity(net.minecraft.core.BlockPos pos, net.minecraft.world.level.block.state.BlockState state) {
		return createBlockEntity(new BlockPos(pos), new BlockState(state));
	}

	@MappedMethod
	public abstract BlockEntityMapper createBlockEntity(BlockPos blockPos, BlockState blockState);

	@Override
	public final <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, net.minecraft.world.level.block.state.BlockState state, net.minecraft.world.level.block.entity.BlockEntityType<T> type) {
		if (type == getBlockEntityTypeForTicking().data) {
			return (world1, pos, state1, blockEntity) -> ((BlockEntityMapper) blockEntity).blockEntityTick();
		} else {
			return null;
		}
	}
}
