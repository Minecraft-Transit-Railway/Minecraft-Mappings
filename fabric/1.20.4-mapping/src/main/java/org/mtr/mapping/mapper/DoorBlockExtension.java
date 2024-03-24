package org.mtr.mapping.mapper;

import net.minecraft.block.Block;
import net.minecraft.block.BlockSetType;
import net.minecraft.state.StateManager;
import net.minecraft.text.Text;
import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.*;
import org.mtr.mapping.tool.HolderBase;

import java.util.List;
import java.util.function.Consumer;

public class DoorBlockExtension extends DoorBlockAbstractMapping implements BlockHelper {

	@MappedMethod
	public DoorBlockExtension(boolean canOpenByHand, Consumer<BlockSettings> consumer) {
		super(canOpenByHand ? BlockSetType.OAK : BlockSetType.IRON, getBlockSettings(consumer));
	}

	@Deprecated
	@Override
	protected final void appendProperties2(StateManager.Builder<Block, net.minecraft.block.BlockState> builder) {
		appendPropertiesHelper(builder);
	}

	@Deprecated
	@Override
	public final void appendTooltip2(ItemStack stack, BlockView world, List<Text> tooltip, TooltipContext options) {
		appendTooltipHelper(stack, world, tooltip, options);
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

	private static BlockSettings getBlockSettings(Consumer<BlockSettings> consumer) {
		final BlockSettings blockSettings = BlockHelper.createBlockSettings(true);
		consumer.accept(blockSettings);
		return blockSettings;
	}
}
