package org.mtr.mapping;

import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.PushReaction;
import org.mtr.mapping.annotation.MappedMethod;

public class PropertiesMapper {

	protected final BlockBehaviour.Properties blockSettings;

	@MappedMethod
	public PropertiesMapper() {
		blockSettings = BlockBehaviour.Properties.of();
	}

	private PropertiesMapper(BlockBehaviour.Properties blockSettings) {
		this.blockSettings = blockSettings;
	}

	@MappedMethod
	public PropertiesMapper blockPiston(boolean blockPiston) {
		return new PropertiesMapper(blockSettings.pushReaction(blockPiston ? PushReaction.BLOCK : PushReaction.NORMAL));
	}

	@MappedMethod
	public PropertiesMapper luminance(int luminance) {
		return new PropertiesMapper(blockSettings.lightLevel(blockState -> luminance));
	}
}
