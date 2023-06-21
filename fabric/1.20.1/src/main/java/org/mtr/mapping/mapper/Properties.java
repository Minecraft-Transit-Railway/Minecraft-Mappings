package org.mtr.mapping.mapper;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.piston.PistonBehavior;
import org.mtr.mapping.annotation.MappedMethod;

public class Properties {

	protected final FabricBlockSettings blockSettings;

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
