package org.mtr.mapping.mapper;

import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.world.BlockView;
import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.BlockEntityType;
import org.mtr.mapping.holder.BlockPos;
import org.mtr.mapping.holder.BlockState;

import javax.annotation.Nullable;

public interface BlockWithEntity extends BlockEntityProvider {

	@MappedMethod
	BlockEntityType<? extends BlockEntityExtension> getBlockEntityType();

	@Deprecated
	@Override
	default BlockEntity createBlockEntity(BlockView world) {
		return createBlockEntity().create(getBlockEntityType(), null, null);
	}

	@MappedMethod
	BlockEntitySupplier createBlockEntity();

	@FunctionalInterface
	interface BlockEntitySupplier {
		@MappedMethod
		BlockEntityExtension create(BlockEntityType<? extends BlockEntityExtension> type, @Nullable BlockPos blockPos, @Nullable BlockState blockState);
	}
}
