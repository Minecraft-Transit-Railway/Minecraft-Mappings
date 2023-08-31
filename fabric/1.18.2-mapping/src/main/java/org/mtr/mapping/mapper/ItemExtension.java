package org.mtr.mapping.mapper;

import net.minecraft.text.Text;
import net.minecraft.util.TypedActionResult;
import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.*;

import java.util.List;

public class ItemExtension extends ItemAbstractMapping implements ItemHelper {

	public ItemExtension(ItemSettings itemSettings) {
		super(itemSettings);
	}

	@Deprecated
	@Override
	public final TypedActionResult<net.minecraft.item.ItemStack> use2(World world, PlayerEntity user, Hand hand) {
		useWithoutResult(world, user, hand);
		return super.use2(world, user, hand);
	}

	@Deprecated
	@Override
	public final void appendTooltip2(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
		appendTooltipHelper(stack, world, tooltip, context);
	}

	@MappedMethod
	public void useWithoutResult(World world, PlayerEntity user, Hand hand) {
	}
}
