package org.mtr.mapping.mapper;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.Property;
import org.mtr.mapping.tool.DummyInterface;

public interface BlockHelper extends DummyInterface {

	@MappedMethod
	default Property<?>[] blockProperties() {
		return new Property[0];
	}

	@Deprecated
	default void createBlockStateDefinitionHelper(StateDefinition.Builder<Block, BlockState> builder) {
		final Property<?>[] oldProperties = blockProperties();
		if (oldProperties.length > 0) {
			final net.minecraft.world.level.block.state.properties.Property<?>[] newProperties = new net.minecraft.world.level.block.state.properties.Property[oldProperties.length];
			for (int i = 0; i < oldProperties.length; i++) {
				newProperties[i] = oldProperties[i].data;
			}
			builder.add(newProperties);
		}
	}
}
