package org.mtr.mapping.mapper;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
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
}
