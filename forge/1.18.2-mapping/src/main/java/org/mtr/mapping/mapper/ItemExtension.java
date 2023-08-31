package org.mtr.mapping.mapper;

import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResultHolder;
import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.*;

import javax.annotation.Nullable;
import java.util.List;

public class ItemExtension extends ItemAbstractMapping implements ItemHelper {

	public ItemExtension(ItemSettings itemSettings) {
		super(itemSettings);
	}

	@Deprecated
	@Override
	public final InteractionResultHolder<net.minecraft.world.item.ItemStack> use2(World world, PlayerEntity user, Hand hand) {
		useWithoutResult(world, user, hand);
		return super.use2(world, user, hand);
	}

	@Deprecated
	@Override
	public final void appendHoverText2(ItemStack stack, @Nullable World world, List<Component> tooltip, TooltipContext options) {
		appendTooltipHelper(stack, world, tooltip, options);
	}

	@MappedMethod
	public void useWithoutResult(World world, PlayerEntity user, Hand hand) {
	}
}
