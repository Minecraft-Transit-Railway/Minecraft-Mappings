package org.mtr.mapping.registry;

import net.minecraft.resources.ResourceLocation;
import org.mtr.mapping.holder.ItemStack;
import org.mtr.mapping.mapper.ItemExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public final class CreativeModeTabHolder {

	public final ResourceLocation resourceLocation;
	public final Supplier<ItemStack> iconSupplier;
	public final List<Supplier<ItemExtension>> itemSuppliers = new ArrayList<>();

	public CreativeModeTabHolder(ResourceLocation resourceLocation, Supplier<ItemStack> iconSupplier) {
		this.resourceLocation = resourceLocation;
		this.iconSupplier = iconSupplier;
	}
}
