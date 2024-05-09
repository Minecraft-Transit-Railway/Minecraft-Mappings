package org.mtr.mapping.mapper;

import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.Identifier;
import org.mtr.mapping.tool.DummyClass;

import javax.annotation.Nullable;
import java.util.List;

public final class OptimizedModel extends DummyClass {

	@MappedMethod
	public OptimizedModel(List<MaterialGroup> materialGroups) {
	}

	@MappedMethod
	public OptimizedModel(Identifier objLocation, @Nullable Identifier atlasIndex, boolean flipTextureV) {
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
