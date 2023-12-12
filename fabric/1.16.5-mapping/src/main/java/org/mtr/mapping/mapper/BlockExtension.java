package org.mtr.mapping.mapper;

import net.minecraft.state.StateManager;
import net.minecraft.text.Text;
import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.*;

import javax.annotation.Nonnull;
import java.util.List;

public class BlockExtension extends BlockAbstractMapping implements BlockHelper {

	@MappedMethod
	public BlockExtension(BlockSettings blockSettings) {
		super(blockSettings);
	}

	@MappedMethod
	public void onBreak3(World world, BlockPos pos, BlockState state, PlayerEntity player) {
		super.onBreak2(world, pos, state, player);
	}

	@Deprecated
	@Override
	public final void onBreak2(World world, BlockPos pos, BlockState state, PlayerEntity player) {
		onBreak3(world, pos, state, player);
	}

	@Nonnull
	@MappedMethod
	public ItemStack getPickStack3(BlockView world, BlockPos pos, BlockState state) {
		return super.getPickStack2(world, pos, state);
	}

	@Nonnull
	@Deprecated
	@Override
	public final ItemStack getPickStack2(BlockView world, BlockPos pos, BlockState state) {
		return getPickStack3(world, pos, state);
	}

	@Deprecated
	@Override
	protected final void appendProperties2(StateManager.Builder<net.minecraft.block.Block, net.minecraft.block.BlockState> builder) {
		appendPropertiesHelper(builder);
	}

	@Deprecated
	@Override
	public final void appendTooltip2(ItemStack stack, BlockView world, List<Text> tooltip, TooltipContext options) {
		appendTooltipHelper(stack, world, tooltip, options);
	}

	@MappedMethod
	public static void scheduleBlockTick(World world, BlockPos pos, Block block, int ticks) {
		world.getBlockTickScheduler().schedule(pos.data, block.data, ticks);
	}

	@MappedMethod
	public static boolean hasScheduledTick(World world, BlockPos pos, Block block) {
		return world.getBlockTickScheduler().isScheduled(pos.data, block.data);
	}
}
