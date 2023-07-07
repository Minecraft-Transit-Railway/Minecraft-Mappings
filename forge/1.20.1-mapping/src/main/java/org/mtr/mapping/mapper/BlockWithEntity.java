package org.mtr.mapping.mapper;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.BlockEntityType;
import org.mtr.mapping.holder.BlockPos;
import org.mtr.mapping.holder.BlockState;

public abstract class BlockWithEntity extends BlockExtension implements EntityBlock {

	@MappedMethod
	public BlockWithEntity(BlockExtension.Properties properties) {
		super(properties);
	}

	@MappedMethod
	public abstract BlockEntityType<? extends BlockEntityExtension> getBlockEntityTypeForTicking();

	@Override
	public final net.minecraft.world.level.block.entity.BlockEntity newBlockEntity(net.minecraft.core.BlockPos pos, net.minecraft.world.level.block.state.BlockState state) {
		return createBlockEntity(new BlockPos(pos), new BlockState(state));
	}

	@MappedMethod
	public abstract BlockEntityExtension createBlockEntity(BlockPos blockPos, BlockState blockState);

	@Override
	public final <T extends net.minecraft.world.level.block.entity.BlockEntity> BlockEntityTicker<T> getTicker(Level world, net.minecraft.world.level.block.state.BlockState state, net.minecraft.world.level.block.entity.BlockEntityType<T> type) {
		if (type == getBlockEntityTypeForTicking().data) {
			return (world1, pos, state1, blockEntity) -> ((BlockEntityExtension) blockEntity).blockEntityTick();
		} else {
			return null;
		}
	}
}
