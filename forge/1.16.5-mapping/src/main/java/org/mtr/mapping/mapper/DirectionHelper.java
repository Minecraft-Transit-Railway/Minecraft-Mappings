package org.mtr.mapping.mapper;

import net.minecraft.block.DirectionalBlock;
import net.minecraft.block.HorizontalBlock;
import org.mtr.mapping.holder.DirectionProperty;

public interface DirectionHelper {

	DirectionProperty FACING = new DirectionProperty(HorizontalBlock.FACING);
	DirectionProperty FACING_NORMAL = new DirectionProperty(DirectionalBlock.FACING);
}
