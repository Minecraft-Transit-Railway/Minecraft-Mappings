package org.mtr.mapping.mapper;

import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.item.ItemStack;
import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.Hand;
import org.mtr.mapping.holder.ItemAbstractMapping;
import org.mtr.mapping.holder.PlayerEntity;
import org.mtr.mapping.holder.World;

public abstract class ItemExtension extends ItemAbstractMapping implements ItemHelper {

	public ItemExtension(ItemHelper.Properties properties) {
		super(properties.itemSettings);
	}

	@Deprecated
	@Override
	public final InteractionResultHolder<ItemStack> use2(World world, PlayerEntity user, Hand hand) {
		useWithoutResult(world, user, hand);
		return super.use2(world, user, hand);
	}

	@MappedMethod
	public void useWithoutResult(World world, PlayerEntity user, Hand hand) {
	}
}
