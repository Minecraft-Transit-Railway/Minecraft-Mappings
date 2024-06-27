package org.mtr.mapping.mapper;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.*;

public class BlockItemExtension extends BlockItemAbstractMapping {

    @MappedMethod
    public BlockItemExtension(Block block, ItemSettings itemSettings) {
        super(block, itemSettings);
    }

    @Deprecated
    @Override
    public final InteractionResultHolder<ItemStack> use(Level world, Player user, InteractionHand hand) {
        useWithoutResult(new World(world), new PlayerEntity(user), Hand.convert(hand));
        return super.use(world, user, hand);
    }

    @MappedMethod
    public void useWithoutResult(World world, PlayerEntity user, Hand hand) {
    }
}
