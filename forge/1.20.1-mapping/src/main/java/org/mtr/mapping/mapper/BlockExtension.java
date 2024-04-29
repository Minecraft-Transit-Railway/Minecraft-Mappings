package org.mtr.mapping.mapper;

import net.minecraft.network.chat.Component;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.StateDefinition;
import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class BlockExtension extends BlockAbstractMapping implements BlockHelper {

	@MappedMethod
	public BlockExtension(BlockSettings blockSettings) {
		super(blockSettings);
	}

	@MappedMethod
	public void onBreak3(World world, BlockPos pos, org.mtr.mapping.holder.BlockState state, PlayerEntity player) {
		super.playerWillDestroy2(world, pos, state, player);
	}

	@Deprecated
	@Override
	public final void playerWillDestroy2(World world, BlockPos pos, BlockState state, PlayerEntity player) {
		onBreak3(world, pos, state, player);
	}

	@Nonnull
	@MappedMethod
	public ItemStack getPickStack3(BlockView world, BlockPos pos, BlockState state) {
		return super.getCloneItemStack2(world, pos, state);
	}

	@Nonnull
	@Deprecated
	@Override
	public final ItemStack getCloneItemStack2(BlockView world, BlockPos pos, BlockState state) {
		return getPickStack3(world, pos, state);
	}

	@Deprecated
	@Override
	protected final void createBlockStateDefinition2(StateDefinition.Builder<net.minecraft.world.level.block.Block, net.minecraft.world.level.block.state.BlockState> builder) {
		createBlockStateDefinitionHelper(builder);
	}

	@Deprecated
	@Override
	public final void appendHoverText2(ItemStack stack, @Nullable BlockView world, List<Component> tooltipList, TooltipContext options) {
		appendTooltipHelper(stack, world, tooltipList, options);
	}

	@MappedMethod
	public static void scheduleBlockTick(World world, BlockPos pos, Block block, int ticks) {
		world.scheduleTick(pos, block, ticks);
	}

	@MappedMethod
	public static boolean hasScheduledTick(World world, BlockPos pos, Block block) {
		return world.getBlockTicks().hasScheduledTick(pos.data, block.data);
	}
}
