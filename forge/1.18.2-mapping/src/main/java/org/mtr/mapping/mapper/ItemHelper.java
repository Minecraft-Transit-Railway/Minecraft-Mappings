package org.mtr.mapping.mapper;

import net.minecraft.network.chat.Component;
import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.ItemStack;
import org.mtr.mapping.holder.MutableText;
import org.mtr.mapping.holder.TooltipContext;
import org.mtr.mapping.holder.World;
import org.mtr.mapping.tool.DummyInterface;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public interface ItemHelper extends DummyInterface {

	@MappedMethod
	default void addTooltips(ItemStack stack, @Nullable World world, List<MutableText> tooltip, TooltipContext options) {
	}

	@Deprecated
	default void appendTooltipHelper(ItemStack stack, @Nullable World world, List<Component> tooltipList, TooltipContext options) {
		final List<MutableText> newTooltipList = new ArrayList<>();
		addTooltips(stack, world, newTooltipList, options);
		newTooltipList.forEach(mutableText -> tooltipList.add(mutableText.data));
	}
}
