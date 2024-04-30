package org.mtr.mapping.mapper;

import net.minecraft.text.Text;
import net.minecraft.util.TypedActionResult;
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
	public final TypedActionResult<net.minecraft.item.ItemStack> use(net.minecraft.world.World world, net.minecraft.entity.player.PlayerEntity user, net.minecraft.util.Hand hand) {
		useWithoutResult(new World(world), new PlayerEntity(user), Hand.convert(hand));
		return super.use(world, user, hand);
	}

	@Deprecated
	@Override
	public final void appendTooltip(net.minecraft.item.ItemStack stack, @Nullable net.minecraft.world.World world, List<Text> tooltip, net.minecraft.client.item.TooltipContext context) {
		appendTooltipHelper(new ItemStack(stack), world == null ? null : new World(world), tooltip, new TooltipContext(context));
	}

	@MappedMethod
	public void useWithoutResult(World world, PlayerEntity user, Hand hand) {
	}
}
