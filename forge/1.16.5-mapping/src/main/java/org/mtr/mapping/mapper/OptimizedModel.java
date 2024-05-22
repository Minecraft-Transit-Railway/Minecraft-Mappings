package org.mtr.mapping.mapper;

import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.Identifier;
import org.mtr.mapping.tool.DummyClass;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public final class OptimizedModel extends DummyClass {

	@MappedMethod
	public static OptimizedModel fromMaterialGroups(Collection<MaterialGroup> materialGroups) {
		return new OptimizedModel();
	}

	@MappedMethod
	public static OptimizedModel fromObjModels(Collection<ObjModel> objModels) {
		return new OptimizedModel();
	}

	@MappedMethod
	public OptimizedModel(OptimizedModel... optimizedModels) {
	}

	public static final class MaterialGroup {

		@MappedMethod
		public MaterialGroup(ShaderType shaderType, Identifier texture) {
		}

		@MappedMethod
		public void addCube(ModelPartExtension modelPart, double x, double y, double z, boolean flipped, int light) {
		}
	}

	public static final class ObjModel {

		@MappedMethod
		public static Map<String, ObjModel> loadModel(Identifier objLocation, @Nullable Identifier atlasIndex, boolean splitModel, boolean flipTextureV) {
			return new HashMap<>();
		}

		@MappedMethod
		public void addTransformation(ShaderType shaderType, double x, double y, double z, boolean flipped) {
		}
	}

	public enum ShaderType {
		CUTOUT, TRANSLUCENT,
		CUTOUT_BRIGHT, TRANSLUCENT_BRIGHT,
		CUTOUT_GLOWING, TRANSLUCENT_GLOWING
	}
}
