package org.mtr.mapping.mapper;

import net.minecraft.block.Block;
import net.minecraft.block.BlockSetType;
import net.minecraft.state.StateManager;
import net.minecraft.text.Text;
import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.*;
import org.mtr.mapping.tool.HolderBase;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;

public class HalfDoorBlockExtension extends DoorBlockAbstractMapping implements BlockHelper {

	@MappedMethod
	public HalfDoorBlockExtension(boolean canOpenByHand, Consumer<BlockSettings> consumer) {
		super(canOpenByHand ? BlockSetType.OAK : BlockSetType.IRON, getBlockSettings(consumer));
	}

	@Deprecated
	@Override
	protected final void appendProperties(StateManager.Builder<Block, net.minecraft.block.BlockState> builder) {
		appendPropertiesHelper(builder);
	}

	@Deprecated
	@Override
	public final void appendTooltip(net.minecraft.item.ItemStack stack, @Nullable net.minecraft.world.BlockView world, List<Text> tooltip, net.minecraft.client.item.TooltipContext options) {
		appendTooltipHelper(new ItemStack(stack), world == null ? null : new BlockView(world), tooltip, new TooltipContext(options));
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

	@Override
	public void onPlaced2(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {

	}
}
