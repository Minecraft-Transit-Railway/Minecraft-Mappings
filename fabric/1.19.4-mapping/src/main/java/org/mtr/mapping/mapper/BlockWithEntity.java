package org.mtr.mapping.mapper;

import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.world.World;
import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.BlockEntityType;
import org.mtr.mapping.holder.BlockPos;
import org.mtr.mapping.holder.BlockState;

public abstract class BlockWithEntity extends BlockExtension implements BlockEntityProvider {

	@MappedMethod
	public BlockWithEntity(BlockExtension.Properties properties) {
		super(properties);
	}

	@MappedMethod
	public abstract <T extends BlockEntityExtension> BlockEntityType<T> getBlockEntityTypeForTicking();

	@Override
	public final net.minecraft.block.entity.BlockEntity createBlockEntity(net.minecraft.util.math.BlockPos pos, net.minecraft.block.BlockState state) {
		return createBlockEntity(new BlockPos(pos), new BlockState(state));
	}

	@MappedMethod
	public abstract BlockEntityExtension createBlockEntity(BlockPos blockPos, BlockState blockState);

	@Override
	public final <T extends net.minecraft.block.entity.BlockEntity> BlockEntityTicker<T> getTicker(World world, net.minecraft.block.BlockState state, net.minecraft.block.entity.BlockEntityType<T> type) {
		if (type == getBlockEntityTypeForTicking().data) {
			return (world1, pos, state1, blockEntity) -> ((BlockEntityExtension) blockEntity).blockEntityTick();
		} else {
			return null;
		}
	}
}
