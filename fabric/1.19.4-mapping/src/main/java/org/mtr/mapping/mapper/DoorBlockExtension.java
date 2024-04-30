package org.mtr.mapping.mapper;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSetType;
import net.minecraft.block.Material;
import net.minecraft.state.StateManager;
import net.minecraft.text.Text;
import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.*;
import org.mtr.mapping.tool.HolderBase;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;

public class DoorBlockExtension extends DoorBlockAbstractMapping implements BlockHelper {

	@MappedMethod
	public DoorBlockExtension(boolean canOpenByHand, Consumer<BlockSettings> consumer) {
		super(getBlockSettings(canOpenByHand, consumer), canOpenByHand ? BlockSetType.OAK : BlockSetType.IRON);
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

	private static BlockSettings getBlockSettings(boolean canOpenWithHand, Consumer<BlockSettings> consumer) {
		final BlockSettings blockSettings = new BlockSettings(FabricBlockSettings.of(canOpenWithHand ? Material.WOOD : Material.METAL));
		consumer.accept(blockSettings);
		return blockSettings;
	}
}
