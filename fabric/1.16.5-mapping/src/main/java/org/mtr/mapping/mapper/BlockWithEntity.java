package org.mtr.mapping.mapper;

import net.minecraft.world.BlockView;
import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.BlockEntityType;
import org.mtr.mapping.holder.BlockPos;
import org.mtr.mapping.holder.BlockState;

import javax.annotation.Nullable;

public abstract class BlockWithEntity extends net.minecraft.block.BlockWithEntity {

	@MappedMethod
	public BlockWithEntity(Block.Properties properties) {
		super(properties.blockSettings);
	}

	@MappedMethod
	public abstract <T extends BlockEntity> BlockEntityType<T> getBlockEntityTypeForTicking();

	@Override
	public final net.minecraft.block.entity.BlockEntity createBlockEntity(BlockView world) {
		return createBlockEntity(null, null);
	}

	@MappedMethod
	public abstract BlockEntity createBlockEntity(@Nullable BlockPos blockPos, @Nullable BlockState blockState);
}
