package org.mtr.mapping.mapper;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.HitResult;
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
	public void onBreak2(World world, BlockPos pos, org.mtr.mapping.holder.BlockState state, PlayerEntity player) {
		super.playerWillDestroy(world.data, pos.data, state.data, player.data);
	}

	@Deprecated
	@Override
	public final net.minecraft.world.level.block.state.BlockState playerWillDestroy(Level world, net.minecraft.core.BlockPos pos, net.minecraft.world.level.block.state.BlockState state, Player player) {
		onBreak2(new World(world), new BlockPos(pos), new BlockState(state), new PlayerEntity(player));
		return state;
	}

	@Nonnull
	@MappedMethod
	public ItemStack getPickStack2(BlockView world, BlockPos pos, BlockState state) {
		return world.data instanceof LevelReader ? new ItemStack(super.getCloneItemStack((LevelReader) world.data, pos.data, state.data)) : ItemStack.getEmptyMapped();
	}

	@Nonnull
	@Deprecated
	@Override
	public final net.minecraft.world.item.ItemStack getCloneItemStack(net.minecraft.world.level.block.state.BlockState state, HitResult target, LevelReader world, net.minecraft.core.BlockPos pos, Player player) {
		return getPickStack2(new BlockView(world), new BlockPos(pos), new BlockState(state)).data;
	}

	@Deprecated
	@Override
	protected final void createBlockStateDefinition(StateDefinition.Builder<net.minecraft.world.level.block.Block, net.minecraft.world.level.block.state.BlockState> builder) {
		createBlockStateDefinitionHelper(builder);
	}

	@Deprecated
	@Override
	public final void appendHoverText(net.minecraft.world.item.ItemStack stack, @Nullable BlockGetter world, List<Component> tooltipList, TooltipFlag options) {
		appendTooltipHelper(new ItemStack(stack), world == null ? null : new BlockView(world), tooltipList, new TooltipContext(options));
	}

	@MappedMethod
	public static void scheduleBlockTick(World world, BlockPos pos, Block block, int ticks) {
		world.data.scheduleTick(pos.data, block.data, ticks);
	}

	@MappedMethod
	public static boolean hasScheduledTick(World world, BlockPos pos, Block block) {
		return world.data.getBlockTicks().hasScheduledTick(pos.data, block.data);
	}
}
