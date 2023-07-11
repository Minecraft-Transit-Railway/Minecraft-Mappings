package org.mtr.mapping.mapper;

import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.world.World;
import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.BlockPos;
import org.mtr.mapping.holder.BlockState;

public interface BlockWithEntity extends BlockEntityProvider {

	@Deprecated
	@Override
	default BlockEntity createBlockEntity(net.minecraft.util.math.BlockPos pos, net.minecraft.block.BlockState state) {
		return createBlockEntity(new BlockPos(pos), new BlockState(state));
	}

	@MappedMethod
	BlockEntityExtension createBlockEntity(BlockPos pos, BlockState state);

	@Deprecated
	@Override
	default <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, net.minecraft.block.BlockState state, net.minecraft.block.entity.BlockEntityType<T> type) {
		return (world1, pos, state1, blockEntity) -> {
			if (blockEntity.getType() == type && blockEntity instanceof BlockEntityExtension) {
				((BlockEntityExtension) blockEntity).blockEntityTick();
			}
		};
	}
}
