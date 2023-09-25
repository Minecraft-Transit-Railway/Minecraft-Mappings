package org.mtr.mapping.mapper;

import net.minecraft.world.ContainerHelper;
import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.Inventory;
import org.mtr.mapping.holder.ItemStack;
import org.mtr.mapping.tool.HolderBase;

import java.util.List;
import java.util.function.Predicate;

public final class InventoryHelper {

	@MappedMethod
	public static ItemStack splitStack(List<ItemStack> stacks, int slot, int amount) {
		return new ItemStack(ContainerHelper.removeItem(HolderBase.convertCollection(stacks), slot, amount));
	}

	@MappedMethod
	public static ItemStack removeStack(List<ItemStack> stacks, int slot) {
		return new ItemStack(ContainerHelper.takeItem(HolderBase.convertCollection(stacks), slot));
	}

	@MappedMethod
	public static int remove(Inventory inventory, Predicate<ItemStack> shouldRemove, int maxCount, boolean dryRun) {
		return ContainerHelper.clearOrCountMatchingItems(inventory.data, itemStack -> shouldRemove.test(new ItemStack(itemStack)), maxCount, dryRun);
	}

	@MappedMethod
	public static int remove(ItemStack stack, Predicate<ItemStack> shouldRemove, int maxCount, boolean dryRun) {
		return ContainerHelper.clearOrCountMatchingItems(stack.data, itemStack -> shouldRemove.test(new ItemStack(itemStack)), maxCount, dryRun);
	}
}
