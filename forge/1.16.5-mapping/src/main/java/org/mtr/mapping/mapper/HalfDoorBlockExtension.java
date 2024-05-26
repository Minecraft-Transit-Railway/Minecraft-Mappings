package org.mtr.mapping.mapper;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.state.StateContainer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.IBlockReader;
import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.*;
import org.mtr.mapping.tool.HolderBase;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;

public class HalfDoorBlockExtension extends DoorBlockAbstractMapping implements BlockHelper {

	@MappedMethod
	public HalfDoorBlockExtension(boolean canOpenByHand, Consumer<BlockSettings> consumer) {
		super(getBlockSettings(canOpenByHand, consumer));
	}

	@Deprecated
	@Override
	protected final void createBlockStateDefinition(StateContainer.Builder<Block, net.minecraft.block.BlockState> builder) {
		createBlockStateDefinitionHelper(builder);
	}

	@Deprecated
	@Override
	public final void appendHoverText(net.minecraft.item.ItemStack stack, @Nullable IBlockReader world, List<ITextComponent> tooltipList, ITooltipFlag options) {
		appendTooltipHelper(new ItemStack(stack), world == null ? null : new BlockView(world), tooltipList, new TooltipContext(options));
	}

	@MappedMethod
	@Override
	public void addBlockProperties(List<HolderBase<?>> properties) {
		properties.add(new Property<>(FACING));
		properties.add(new Property<>(OPEN));
		properties.add(new Property<>(HINGE));
		properties.add(new Property<>(POWERED));
		properties.add(new Property<>(HALF));
	}

	private static BlockSettings getBlockSettings(boolean canOpenWithHand, Consumer<BlockSettings> consumer) {
		final BlockSettings blockSettings = new BlockSettings(Properties.of(canOpenWithHand ? Material.WOOD : Material.METAL));
		consumer.accept(blockSettings);
		return blockSettings;
	}

	@MappedMethod
	@Override
	public void onPlaced2(World arg0, BlockPos arg1, BlockState arg2, LivingEntity arg3, ItemStack arg4) {

	}
}
