package org.mtr.mapping.mapper;

import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.BlockEntityType;
import org.mtr.mapping.holder.BlockPos;
import org.mtr.mapping.holder.BlockState;
import org.mtr.mapping.tool.DummyInterface;

import javax.annotation.Nullable;

public interface BlockWithEntity extends DummyInterface {

	@MappedMethod
	BlockEntityType<? extends BlockEntityExtension> getBlockEntityType();

	@MappedMethod
	BlockEntitySupplier createBlockEntity();

	@FunctionalInterface
	interface BlockEntitySupplier {
		@MappedMethod
		BlockEntityExtension create(BlockEntityType<? extends BlockEntityExtension> type, @Nullable BlockPos blockPos, @Nullable BlockState state);
	}
}
