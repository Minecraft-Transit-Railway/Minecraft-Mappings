package org.mtr.mapping.mapper;

import net.minecraft.block.BlockEntityProvider;
import net.minecraft.world.BlockView;
import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.BlockEntityType;
import org.mtr.mapping.holder.BlockPos;
import org.mtr.mapping.holder.BlockState;

import javax.annotation.Nullable;

public abstract class BlockWithEntity extends BlockExtension implements BlockEntityProvider {

	@MappedMethod
	public BlockWithEntity(BlockExtension.Properties properties) {
		super(properties);
	}

	@MappedMethod
	public abstract BlockEntityType<? extends BlockEntityExtension> getBlockEntityTypeForTicking();

	@Override
	public final net.minecraft.block.entity.BlockEntity createBlockEntity(BlockView world) {
		return createBlockEntity(null, null);
	}

	@MappedMethod
	public abstract BlockEntityExtension createBlockEntity(@Nullable BlockPos blockPos, @Nullable BlockState blockState);
}
