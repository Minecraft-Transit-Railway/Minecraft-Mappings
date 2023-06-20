package org.mtr.mapping;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.piston.PistonBehavior;
import org.mtr.mapping.annotation.MappedMethod;

public class PropertiesMapper {

	protected final FabricBlockSettings blockSettings;

	@MappedMethod
	public PropertiesMapper() {
		blockSettings = FabricBlockSettings.create();
	}

	private PropertiesMapper(FabricBlockSettings blockSettings) {
		this.blockSettings = blockSettings;
	}

	@MappedMethod
	public PropertiesMapper blockPiston(boolean blockPiston) {
		return new PropertiesMapper(blockSettings.pistonBehavior(blockPiston ? PistonBehavior.BLOCK : PistonBehavior.NORMAL));
	}

	@MappedMethod
	public PropertiesMapper luminance(int luminance) {
		return new PropertiesMapper(blockSettings.luminance(luminance));
	}
}
