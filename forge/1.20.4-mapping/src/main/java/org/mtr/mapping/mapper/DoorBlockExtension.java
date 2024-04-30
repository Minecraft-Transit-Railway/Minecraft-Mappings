package org.mtr.mapping.mapper;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.*;
import org.mtr.mapping.tool.HolderBase;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;

public class DoorBlockExtension extends DoorBlockAbstractMapping implements BlockHelper {

	@MappedMethod
	public DoorBlockExtension(boolean canOpenByHand, Consumer<BlockSettings> consumer) {
		super(canOpenByHand ? BlockSetType.OAK : BlockSetType.IRON, getBlockSettings(consumer));
	}

	@Deprecated
	@Override
	protected final void createBlockStateDefinition(StateDefinition.Builder<Block, net.minecraft.world.level.block.state.BlockState> builder) {
		createBlockStateDefinitionHelper(builder);
	}

	@Deprecated
	@Override
	public final void appendHoverText(net.minecraft.world.item.ItemStack stack, @Nullable BlockGetter world, List<Component> tooltipList, TooltipFlag options) {
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

	private static BlockSettings getBlockSettings(Consumer<BlockSettings> consumer) {
		final BlockSettings blockSettings = BlockHelper.createBlockSettings(true);
		consumer.accept(blockSettings);
		return blockSettings;
	}
}
