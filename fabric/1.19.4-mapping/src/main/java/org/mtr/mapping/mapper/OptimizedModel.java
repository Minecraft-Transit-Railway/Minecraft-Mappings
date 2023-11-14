package org.mtr.mapping.mapper;

import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.Identifier;

import java.util.List;

public final class OptimizedModel {

	@MappedMethod
	public OptimizedModel(List<MaterialGroup> materialGroups) {
	}

	public static final class MaterialGroup {

		@MappedMethod
		public MaterialGroup(ShaderType shaderType, Identifier texture) {
		}

		@MappedMethod
		public void addCube(ModelPartExtension modelPart, double x, double y, double z, boolean flipped, int light) {
		}
	}

	public enum ShaderType {
		CUTOUT, TRANSLUCENT,
		CUTOUT_BRIGHT, TRANSLUCENT_BRIGHT,
		CUTOUT_GLOWING, TRANSLUCENT_GLOWING
	}
}
