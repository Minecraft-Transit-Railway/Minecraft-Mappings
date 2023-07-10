package org.mtr.mapping.mapper;

import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.world.World;
import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.BlockEntityType;
import org.mtr.mapping.holder.BlockPos;
import org.mtr.mapping.holder.BlockState;

public interface BlockWithEntity extends BlockEntityProvider {

	@MappedMethod
	BlockEntityType<? extends BlockEntityExtension> getBlockEntityType();

	@Deprecated
	@Override
	default BlockEntity createBlockEntity(net.minecraft.util.math.BlockPos pos, net.minecraft.block.BlockState state) {
		return createBlockEntity().create(getBlockEntityType(), new BlockPos(pos), new BlockState(state));
	}

	@MappedMethod
	BlockEntitySupplier createBlockEntity();

	@Deprecated
	@Override
	default <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, net.minecraft.block.BlockState state, net.minecraft.block.entity.BlockEntityType<T> type) {
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
