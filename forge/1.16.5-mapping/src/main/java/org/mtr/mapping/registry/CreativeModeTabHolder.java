package org.mtr.mapping.registry;

import net.minecraft.item.ItemGroup;
import org.mtr.mapping.holder.Identifier;
import org.mtr.mapping.holder.ItemStack;

import java.util.function.Supplier;

public final class CreativeModeTabHolder {

	public final ItemGroup creativeModeTab;

	public CreativeModeTabHolder(Identifier identifier, Supplier<ItemStack> iconSupplier) {
		this.creativeModeTab = new CreativeModeTabImplementation(String.format("%s.%s", identifier.data.getNamespace(), identifier.data.getPath()), iconSupplier);
	}

	private static final class CreativeModeTabImplementation extends ItemGroup {

		private final Supplier<ItemStack> iconSupplier;

		public CreativeModeTabImplementation(String label, Supplier<ItemStack> iconSupplier) {
			super(label);
			this.iconSupplier = iconSupplier;
		}

		@Deprecated
		@Override
		public net.minecraft.item.ItemStack makeIcon() {
			return iconSupplier.get().data;
		}
	}
}
