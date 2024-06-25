package org.mtr.mapping.mapper;

import net.minecraft.block.Block;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.state.StateContainer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.IBlockReader;
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
    protected final void createBlockStateDefinition(StateContainer.Builder<Block, net.minecraft.block.BlockState> builder) {
        createBlockStateDefinitionHelper(builder);
    }

    @Deprecated
    @Override
    public final void appendHoverText(net.minecraft.item.ItemStack stack, @Nullable IBlockReader world, List<ITextComponent> tooltipList, ITooltipFlag options) {
        appendTooltipHelper(new ItemStack(stack), world == null ? null : new BlockView(world), tooltipList, new TooltipContext(options));
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
