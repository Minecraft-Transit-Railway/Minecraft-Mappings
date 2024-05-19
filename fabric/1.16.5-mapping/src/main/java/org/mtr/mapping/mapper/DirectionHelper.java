package org.mtr.mapping.mapper;

import net.minecraft.block.FacingBlock;
import net.minecraft.block.HorizontalFacingBlock;
import org.mtr.mapping.holder.DirectionProperty;

public interface DirectionHelper {

	DirectionProperty FACING = new DirectionProperty(HorizontalFacingBlock.FACING);
	DirectionProperty FACING_NORMAL = new DirectionProperty(FacingBlock.FACING);
}
