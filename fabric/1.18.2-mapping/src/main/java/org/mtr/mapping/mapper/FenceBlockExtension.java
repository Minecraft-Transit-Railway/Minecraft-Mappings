package org.mtr.mapping.mapper;

import net.minecraft.block.Block;
import net.minecraft.state.StateManager;
import net.minecraft.text.Text;
import org.mtr.mapping.holder.*;
import org.mtr.mapping.tool.HolderBase;

import javax.annotation.Nullable;
import java.util.List;

public class FenceBlockExtension extends FenceBlockAbstractMapping implements BlockHelper {
    public FenceBlockExtension(BlockSettings settings) {
        super(settings);
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

    @Override
    public void addBlockProperties(List<HolderBase<?>> properties) {
        properties.add(new Property<>(NORTH));
        properties.add(new Property<>(EAST));
        properties.add(new Property<>(SOUTH));
        properties.add(new Property<>(WEST));
        properties.add(new Property<>(WATERLOGGED));
    }
}
