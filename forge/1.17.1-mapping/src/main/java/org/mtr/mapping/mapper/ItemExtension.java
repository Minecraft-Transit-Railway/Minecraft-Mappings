package org.mtr.mapping.mapper;

import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
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
	public final InteractionResultHolder<net.minecraft.world.item.ItemStack> use(Level world, Player user, InteractionHand hand) {
		useWithoutResult(new World(world), new PlayerEntity(user), Hand.convert(hand));
		return super.use(world, user, hand);
	}

	@Deprecated
	@Override
	public final void appendHoverText(net.minecraft.world.item.ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag options) {
		appendTooltipHelper(new ItemStack(stack), world == null ? null : new World(world), tooltip, new TooltipContext(options));
	}

	@MappedMethod
	public void useWithoutResult(World world, PlayerEntity user, Hand hand) {
	}
}
