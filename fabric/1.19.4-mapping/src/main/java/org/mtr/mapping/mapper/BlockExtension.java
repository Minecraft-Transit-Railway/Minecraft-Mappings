package org.mtr.mapping.mapper;

import net.minecraft.state.StateManager;
import net.minecraft.text.Text;
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
	public void onBreak2(World world, BlockPos pos, BlockState state, PlayerEntity player) {
		super.onBreak(world.data, pos.data, state.data, player.data);
	}

	@Deprecated
	@Override
	public final void onBreak(net.minecraft.world.World world, net.minecraft.util.math.BlockPos pos, net.minecraft.block.BlockState state, net.minecraft.entity.player.PlayerEntity player) {
		onBreak2(new World(world), new BlockPos(pos), new BlockState(state), new PlayerEntity(player));
	}

	@Nonnull
	@MappedMethod
	public ItemStack getPickStack2(BlockView world, BlockPos pos, BlockState state) {
		return new ItemStack(super.getPickStack(world.data, pos.data, state.data));
	}

	@Nonnull
	@Deprecated
	@Override
	public final net.minecraft.item.ItemStack getPickStack(net.minecraft.world.BlockView world, net.minecraft.util.math.BlockPos pos, net.minecraft.block.BlockState state) {
		return getPickStack2(new BlockView(world), new BlockPos(pos), new BlockState(state)).data;
	}

	@Deprecated
	@Override
	protected final void appendProperties(StateManager.Builder<net.minecraft.block.Block, net.minecraft.block.BlockState> builder) {
		appendPropertiesHelper(builder);
	}

	@Deprecated
	@Override
	public final void appendTooltip(net.minecraft.item.ItemStack stack, @Nullable net.minecraft.world.BlockView world, List<Text> tooltip, net.minecraft.client.item.TooltipContext options) {
		appendTooltipHelper(new ItemStack(stack), world == null ? null : new BlockView(world), tooltip, new TooltipContext(options));
	}

	@MappedMethod
	public static void scheduleBlockTick(World world, BlockPos pos, Block block, int ticks) {
		world.data.scheduleBlockTick(pos.data, block.data, ticks);
	}

	@MappedMethod
	public static boolean hasScheduledTick(World world, BlockPos pos, Block block) {
		return world.data.getBlockTickScheduler().isQueued(pos.data, block.data);
	}
}
