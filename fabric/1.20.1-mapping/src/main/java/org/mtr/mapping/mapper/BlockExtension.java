package org.mtr.mapping.mapper;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.state.StateManager;
import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.BlockAbstractMapping;
import org.mtr.mapping.holder.Property;

public abstract class BlockExtension extends BlockAbstractMapping {

	@MappedMethod
	public BlockExtension(Properties properties) {
		super(properties.blockSettings);
	}

	@MappedMethod
	protected Property<?>[] blockProperties() {
		return new Property[0];
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		final Property<?>[] oldProperties = blockProperties();
		if (oldProperties.length > 0) {
			final net.minecraft.state.property.Property<?>[] newProperties = new net.minecraft.state.property.Property[oldProperties.length];
			for (int i = 0; i < oldProperties.length; i++) {
				newProperties[i] = oldProperties[i].data;
			}
			builder.add(newProperties);
		}
	}

	public static final class Properties {

		final FabricBlockSettings blockSettings;

		@MappedMethod
		public Properties() {
			blockSettings = FabricBlockSettings.create();
		}

		private Properties(FabricBlockSettings blockSettings) {
			this.blockSettings = blockSettings;
		}

		@MappedMethod
		public Properties blockPiston(boolean blockPiston) {
			return new Properties(blockSettings.pistonBehavior(blockPiston ? PistonBehavior.BLOCK : PistonBehavior.NORMAL));
		}

		@MappedMethod
		public Properties luminance(int luminance) {
			return new Properties(blockSettings.luminance(luminance));
		}
	}
}
