package org.mtr.mapping.mapper;

import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.BlockPos;
import org.mtr.mapping.holder.BlockState;
import org.mtr.mapping.tool.DummyInterface;

import javax.annotation.Nullable;

public interface BlockWithEntity extends DummyInterface {

	@MappedMethod
	BlockEntityExtension createBlockEntity(@Nullable BlockPos pos, @Nullable BlockState state);
}
