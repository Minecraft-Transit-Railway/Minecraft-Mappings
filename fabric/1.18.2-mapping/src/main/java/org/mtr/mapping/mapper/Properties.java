package org.mtr.mapping.mapper;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Material;
import org.mtr.mapping.annotation.MappedMethod;

public class Properties {

	protected final FabricBlockSettings blockSettings;

	@MappedMethod
	public Properties() {
		blockSettings = FabricBlockSettings.of(Material.METAL);
	}

	private Properties(boolean blockPiston) {
		blockSettings = FabricBlockSettings.of(blockPiston ? Material.REPAIR_STATION : Material.METAL);
	}

	private Properties(FabricBlockSettings blockSettings) {
		this.blockSettings = blockSettings;
	}

	@MappedMethod
	public Properties blockPiston(boolean blockPiston) {
		return new Properties(blockPiston);
	}

	@MappedMethod
	public Properties luminance(int luminance) {
		return new Properties(blockSettings.luminance(luminance));
	}
}
