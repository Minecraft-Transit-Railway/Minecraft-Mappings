package org.mtr.mapping.mapper;

import net.minecraft.state.StateContainer;
import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.*;

public abstract class BlockExtension extends BlockAbstractMapping implements BlockHelper {

	@MappedMethod
	public BlockExtension(BlockHelper.Properties properties) {
		super(properties.blockSettings);
	}

	@Deprecated
	@Override
	public final boolean hasTileEntity2(BlockState state) {
		return this instanceof BlockWithEntity;
	}

	@Deprecated
	@Override
	public final org.mtr.mapping.holder.BlockEntity createTileEntity2(BlockState arg0, BlockView arg1) {
		if (this instanceof BlockWithEntity) {
			return new BlockEntity(((BlockWithEntity) this).createBlockEntity(null, null));
		} else {
			return super.createTileEntity2(arg0, arg1);
		}
	}

	@Deprecated
	@Override
	protected final void createBlockStateDefinition(StateContainer.Builder<net.minecraft.block.Block, net.minecraft.block.BlockState> builder) {
		createBlockStateDefinitionHelper(builder);
	}

	@MappedMethod
	public static void scheduleBlockTick(World world, BlockPos pos, Block block, int ticks) {
		world.getBlockTicks().scheduleTick(pos.data, block.data, ticks);
	}

	@MappedMethod
	public static boolean hasScheduledTick(World world, BlockPos pos, Block block) {
		return world.getBlockTicks().hasScheduledTick(pos.data, block.data);
	}
}
