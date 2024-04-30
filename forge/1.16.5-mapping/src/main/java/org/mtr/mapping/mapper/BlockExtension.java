package org.mtr.mapping.mapper;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.IBlockReader;
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
	public final void playerWillDestroy(net.minecraft.world.World world, net.minecraft.util.math.BlockPos pos, net.minecraft.block.BlockState state, net.minecraft.entity.player.PlayerEntity player) {
		onBreak2(new World(world), new BlockPos(pos), new BlockState(state), new PlayerEntity(player));
	}

	@Nonnull
	@MappedMethod
	public ItemStack getPickStack2(BlockView world, BlockPos pos, BlockState state) {
		return new ItemStack(super.getCloneItemStack(world.data, pos.data, state.data));
	}

	@Nonnull
	@Deprecated
	@Override
	public final net.minecraft.item.ItemStack getCloneItemStack(IBlockReader world, net.minecraft.util.math.BlockPos pos, net.minecraft.block.BlockState state) {
		return getPickStack2(new BlockView(world), new BlockPos(pos), new BlockState(state)).data;
	}

	@Deprecated
	@Override
	public final boolean hasTileEntity(net.minecraft.block.BlockState state) {
		return this instanceof BlockWithEntity;
	}

	@Deprecated
	@Override
	public final TileEntity createTileEntity(net.minecraft.block.BlockState state, IBlockReader world) {
		if (this instanceof BlockWithEntity) {
			return ((BlockWithEntity) this).createBlockEntity(null, null);
		} else {
			return super.createTileEntity(state, world);
		}
	}

	@Deprecated
	@Override
	protected final void createBlockStateDefinition(StateContainer.Builder<net.minecraft.block.Block, net.minecraft.block.BlockState> builder) {
		createBlockStateDefinitionHelper(builder);
	}

	@Deprecated
	@Override
	public final void appendHoverText(net.minecraft.item.ItemStack stack, @Nullable IBlockReader world, List<ITextComponent> tooltipList, ITooltipFlag options) {
		appendTooltipHelper(new ItemStack(stack), world == null ? null : new BlockView(world), tooltipList, new TooltipContext(options));
	}

	@MappedMethod
	public static void scheduleBlockTick(World world, BlockPos pos, Block block, int ticks) {
		world.data.getBlockTicks().scheduleTick(pos.data, block.data, ticks);
	}

	@MappedMethod
	public static boolean hasScheduledTick(World world, BlockPos pos, Block block) {
		return world.data.getBlockTicks().hasScheduledTick(pos.data, block.data);
	}
}
