package org.mtr.mapping.mapper;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.StateDefinition;
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
    protected final void createBlockStateDefinition(StateDefinition.Builder<Block, net.minecraft.world.level.block.state.BlockState> builder) {
        createBlockStateDefinitionHelper(builder);
    }

    @Deprecated
    @Override
    public final void appendHoverText(net.minecraft.world.item.ItemStack stack, @Nullable BlockGetter world, List<Component> tooltipList, TooltipFlag options) {
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
