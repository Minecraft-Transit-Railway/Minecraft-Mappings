package org.mtr.mapping.mapper;

import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.world.BlockView;
import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.BlockPos;
import org.mtr.mapping.holder.BlockState;

public interface BlockWithEntity extends BlockEntityProvider {

	@Deprecated
	@Override
	default BlockEntity createBlockEntity(BlockView world) {
		return createBlockEntity(null, null);
	}

	@MappedMethod
	BlockEntityExtension createBlockEntity(BlockPos pos, BlockState state);
}
