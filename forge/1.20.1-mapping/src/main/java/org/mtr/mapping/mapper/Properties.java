package org.mtr.mapping.mapper;

import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.PushReaction;
import org.mtr.mapping.annotation.MappedMethod;

public class Properties {

	protected final BlockBehaviour.Properties blockSettings;

	@MappedMethod
	public Properties() {
		blockSettings = BlockBehaviour.Properties.of();
	}

	private Properties(BlockBehaviour.Properties blockSettings) {
		this.blockSettings = blockSettings;
	}

	@MappedMethod
	public Properties blockPiston(boolean blockPiston) {
		return new Properties(blockSettings.pushReaction(blockPiston ? PushReaction.BLOCK : PushReaction.NORMAL));
	}

	@MappedMethod
	public Properties luminance(int luminance) {
		return new Properties(blockSettings.lightLevel(blockState -> luminance));
	}
}
