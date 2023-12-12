package org.mtr.mapping.mapper;

import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import org.mtr.mapping.holder.DirectionProperty;

public interface DirectionHelper {

	DirectionProperty FACING = new DirectionProperty(HorizontalDirectionalBlock.FACING);
}
