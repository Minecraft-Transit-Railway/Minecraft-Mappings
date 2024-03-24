package org.mtr.mapping.mapper;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.state.StateContainer;
import net.minecraft.util.text.ITextComponent;
import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.*;
import org.mtr.mapping.tool.HolderBase;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;

public class DoorBlockExtension extends DoorBlockAbstractMapping implements BlockHelper {

	@MappedMethod
	public DoorBlockExtension(boolean canOpenByHand, Consumer<BlockSettings> consumer) {
		super(getBlockSettings(canOpenByHand, consumer));
	}

	@Deprecated
	@Override
	protected final void createBlockStateDefinition2(StateContainer.Builder<Block, net.minecraft.block.BlockState> builder) {
		createBlockStateDefinitionHelper(builder);
	}

	@Deprecated
	@Override
	public final void appendHoverText2(ItemStack stack, @Nullable BlockView world, List<ITextComponent> tooltipList, TooltipContext options) {
		appendTooltipHelper(stack, world, tooltipList, options);
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
}
