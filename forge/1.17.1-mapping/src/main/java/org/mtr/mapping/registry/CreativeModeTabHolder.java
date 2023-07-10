package org.mtr.mapping.registry;

import net.minecraft.world.item.CreativeModeTab;
import org.mtr.mapping.holder.ItemStack;
import org.mtr.mapping.holder.ResourceLocation;

import java.util.function.Supplier;

public final class CreativeModeTabHolder {

	public final CreativeModeTab creativeModeTab;

	public CreativeModeTabHolder(ResourceLocation resourceLocation, Supplier<ItemStack> iconSupplier) {
		this.creativeModeTab = new CreativeModeTabImplementation(resourceLocation.data.getPath(), iconSupplier);
	}

	private static final class CreativeModeTabImplementation extends CreativeModeTab {

		private final Supplier<ItemStack> iconSupplier;

		public CreativeModeTabImplementation(String label, Supplier<ItemStack> iconSupplier) {
			super(label);
			this.iconSupplier = iconSupplier;
		}

		@Deprecated
		@Override
		public net.minecraft.world.item.ItemStack makeIcon() {
			return iconSupplier.get().data;
		}
	}
}
