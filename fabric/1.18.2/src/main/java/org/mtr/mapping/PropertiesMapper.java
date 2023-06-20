package org.mtr.mapping;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Material;
import org.mtr.mapping.annotation.MappedMethod;

public class PropertiesMapper {

	protected final FabricBlockSettings blockSettings;

	@MappedMethod
	public PropertiesMapper() {
		blockSettings = FabricBlockSettings.of(Material.METAL);
	}

	private PropertiesMapper(boolean blockPiston) {
		blockSettings = FabricBlockSettings.of(blockPiston ? Material.REPAIR_STATION : Material.METAL);
	}

	private PropertiesMapper(FabricBlockSettings blockSettings) {
		this.blockSettings = blockSettings;
	}

	@MappedMethod
	public PropertiesMapper blockPiston(boolean blockPiston) {
		return new PropertiesMapper(blockPiston);
	}

	@MappedMethod
	public PropertiesMapper luminance(int luminance) {
		return new PropertiesMapper(blockSettings.luminance(luminance));
	}
}
