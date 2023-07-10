package org.mtr.mapping.mapper;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.state.StateContainer;
import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.Property;
import org.mtr.mapping.tool.DummyInterface;

public interface BlockHelper extends DummyInterface {

	@MappedMethod
	default Property<?>[] blockProperties() {
		return new Property[0];
	}

	@Deprecated
	default void createBlockStateDefinitionHelper(StateContainer.Builder<Block, BlockState> builder) {
		final Property<?>[] oldProperties = blockProperties();
		if (oldProperties.length > 0) {
			final net.minecraft.state.Property<?>[] newProperties = new net.minecraft.state.Property[oldProperties.length];
			for (int i = 0; i < oldProperties.length; i++) {
				newProperties[i] = oldProperties[i].data;
			}
			builder.add(newProperties);
		}
	}

	final class Properties {

		final AbstractBlock.Properties blockSettings;

		@MappedMethod
		public Properties() {
			blockSettings = AbstractBlock.Properties.of(Material.METAL);
		}

		private Properties(boolean blockPiston) {
			blockSettings = AbstractBlock.Properties.of(blockPiston ? Material.HEAVY_METAL : Material.METAL);
		}

		private Properties(AbstractBlock.Properties blockSettings) {
			this.blockSettings = blockSettings;
		}

		@MappedMethod
		public Properties blockPiston(boolean blockPiston) {
			return new Properties(blockPiston);
		}

		@MappedMethod
		public Properties luminance(int luminance) {
			return new Properties(blockSettings.lightLevel(blockState -> luminance));
		}
	}
}
