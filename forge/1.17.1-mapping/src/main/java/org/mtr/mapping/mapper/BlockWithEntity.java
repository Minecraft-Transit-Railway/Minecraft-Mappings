package org.mtr.mapping.mapper;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.BlockEntityType;
import org.mtr.mapping.holder.BlockPos;
import org.mtr.mapping.holder.BlockState;

public interface BlockWithEntity extends EntityBlock {

	@MappedMethod
	BlockEntityType<? extends BlockEntityExtension> getBlockEntityType();

	@Deprecated
	@Override
	default BlockEntity newBlockEntity(net.minecraft.core.BlockPos pos, net.minecraft.world.level.block.state.BlockState state) {
		return createBlockEntity().create(getBlockEntityType(), new BlockPos(pos), new BlockState(state));
	}

	@MappedMethod
	BlockEntitySupplier createBlockEntity();

	@Deprecated
	@Override
	default <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, net.minecraft.world.level.block.state.BlockState state, net.minecraft.world.level.block.entity.BlockEntityType<T> type) {
		if (type == getBlockEntityType().data) {
			return (world1, pos, state1, blockEntity) -> ((BlockEntityExtension) blockEntity).blockEntityTick();
		} else {
			return null;
		}
	}

	@FunctionalInterface
	interface BlockEntitySupplier {
		@MappedMethod
		BlockEntityExtension create(BlockEntityType<? extends BlockEntityExtension> type, BlockPos blockPos, BlockState blockState);
	}
}
