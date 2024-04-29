package org.mtr.mapping.registry;

import net.minecraft.resources.ResourceLocation;
import org.mtr.mapping.holder.Item;
import org.mtr.mapping.holder.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public final class CreativeModeTabHolder {

	public final ResourceLocation identifier;
	public final Supplier<ItemStack> iconSupplier;
	public final List<Supplier<Item>> itemSuppliers = new ArrayList<>();

	public CreativeModeTabHolder(ResourceLocation identifier, Supplier<ItemStack> iconSupplier) {
		this.identifier = identifier;
		this.iconSupplier = iconSupplier;
	}
}
