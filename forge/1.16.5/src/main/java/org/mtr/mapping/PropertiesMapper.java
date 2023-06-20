package org.mtr.mapping;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.material.Material;
import org.mtr.mapping.annotation.MappedMethod;

public class PropertiesMapper {

	protected final AbstractBlock.Properties blockSettings;

	@MappedMethod
	public PropertiesMapper() {
		blockSettings = AbstractBlock.Properties.of(Material.METAL);
	}

	private PropertiesMapper(boolean blockPiston) {
		blockSettings = AbstractBlock.Properties.of(blockPiston ? Material.HEAVY_METAL : Material.METAL);
	}

	private PropertiesMapper(AbstractBlock.Properties blockSettings) {
		this.blockSettings = blockSettings;
	}

	@MappedMethod
	public PropertiesMapper blockPiston(boolean blockPiston) {
		return new PropertiesMapper(blockPiston);
	}

	@MappedMethod
	public PropertiesMapper luminance(int luminance) {
		return new PropertiesMapper(blockSettings.lightLevel(blockState -> luminance));
	}
}
