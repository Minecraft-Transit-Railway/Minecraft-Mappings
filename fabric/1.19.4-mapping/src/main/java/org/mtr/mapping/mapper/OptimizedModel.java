package org.mtr.mapping.mapper;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.RotationAxis;
import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.Identifier;
import org.mtr.mapping.holder.OverlayTexture;
import org.mtr.mapping.holder.Vector3f;
import org.mtr.mapping.render.batch.MaterialProperties;
import org.mtr.mapping.render.model.RawMesh;
import org.mtr.mapping.render.model.RawModel;
import org.mtr.mapping.render.obj.AtlasManager;
import org.mtr.mapping.render.obj.ObjModelLoader;
import org.mtr.mapping.render.object.VertexArray;
import org.mtr.mapping.render.vertex.CapturingVertexConsumer;
import org.mtr.mapping.render.vertex.VertexAttributeMapping;
import org.mtr.mapping.render.vertex.VertexAttributeSource;
import org.mtr.mapping.render.vertex.VertexAttributeType;
import org.mtr.mapping.tool.DummyClass;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Consumer;

public final class OptimizedModel extends DummyClass {

	final List<VertexArray> uploadedParts;
	private static final AtlasManager ATLAS_MANAGER = new AtlasManager();

	private static final VertexAttributeMapping DEFAULT_MAPPING = new VertexAttributeMapping.Builder()
			.set(VertexAttributeType.POSITION, VertexAttributeSource.VERTEX_BUFFER)
			.set(VertexAttributeType.COLOR, VertexAttributeSource.GLOBAL)
			.set(VertexAttributeType.UV_TEXTURE, VertexAttributeSource.VERTEX_BUFFER)
			.set(VertexAttributeType.UV_OVERLAY, VertexAttributeSource.GLOBAL)
			.set(VertexAttributeType.UV_LIGHTMAP, VertexAttributeSource.GLOBAL)
			.set(VertexAttributeType.NORMAL, VertexAttributeSource.VERTEX_BUFFER)
			.set(VertexAttributeType.MATRIX_MODEL, VertexAttributeSource.GLOBAL)
			.build();

	@MappedMethod
	public static OptimizedModel fromMaterialGroups(Collection<MaterialGroup> materialGroups) {
		final CapturingVertexConsumer capturingVertexConsumer = new CapturingVertexConsumer();

		materialGroups.forEach(materialGroup -> {
			capturingVertexConsumer.beginStage(materialGroup.materialProperties);
			materialGroup.modelPartConsumers.forEach(modelPartConsumer -> modelPartConsumer.accept(capturingVertexConsumer));
		});

		capturingVertexConsumer.rawModel.triangulate();
		final RawModel rawModel = new RawModel();
		capturingVertexConsumer.rawModel.iterateRawMeshList(rawModel::append);
		rawModel.distinct();
		return new OptimizedModel(rawModel.upload(DEFAULT_MAPPING));
	}

	@MappedMethod
	public static OptimizedModel fromObjModels(Collection<ObjModel> objModels) {
		final List<VertexArray> uploadedParts = new ArrayList<>();
		objModels.forEach(objModel -> {
			objModel.rawModel.generateNormals();
			objModel.rawModel.distinct();
			uploadedParts.addAll(objModel.rawModel.upload(DEFAULT_MAPPING));
		});
		return new OptimizedModel(uploadedParts);
	}

	private OptimizedModel(List<VertexArray> uploadedParts) {
		this.uploadedParts = uploadedParts;
	}

	@MappedMethod
	public OptimizedModel(OptimizedModel... optimizedModels) {
		uploadedParts = new ArrayList<>();
		for (final OptimizedModel optimizedModel : optimizedModels) {
			uploadedParts.addAll(optimizedModel.uploadedParts);
		}
	}

	public static final class MaterialGroup {

		private final MaterialProperties materialProperties;
		private final List<Consumer<CapturingVertexConsumer>> modelPartConsumers = new ArrayList<>();

		@MappedMethod
		public MaterialGroup(ShaderType shaderType, Identifier texture) {
			materialProperties = new MaterialProperties(shaderType, texture, null);
		}

		@MappedMethod
		public void addCube(ModelPartExtension modelPart, double x, double y, double z, boolean flipped, int light) {
			if (modelPart.modelPart != null) {
				modelPartConsumers.add(capturingVertexConsumer -> {
					final MatrixStack matrixStack = new MatrixStack();
					matrixStack.translate(x, y, z);
					if (flipped) {
						matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180));
					}
					modelPart.modelPart.render(matrixStack, capturingVertexConsumer, light, OverlayTexture.getDefaultUvMapped());
				});
			}
		}
	}

	public static final class ObjModel {

		private final List<RawMesh> rawMeshes;
		private final RawModel rawModel = new RawModel();

		private ObjModel(List<RawMesh> rawMeshes, boolean flipTextureV) {
			if (flipTextureV) {
				rawMeshes.forEach(rawMesh -> rawMesh.applyUVMirror(false, true));
			}
			this.rawMeshes = rawMeshes;
		}

		@MappedMethod
		public static Map<String, ObjModel> loadModel(Identifier objLocation, @Nullable Identifier atlasIndex, boolean splitModel, boolean flipTextureV) {
			if (atlasIndex != null) {
				ATLAS_MANAGER.load(atlasIndex);
			}

			final Map<String, ObjModel> objModels = new HashMap<>();
			ObjModelLoader.loadModel(objLocation, ATLAS_MANAGER, splitModel).forEach((key, rawMeshes) -> {
				rawMeshes.forEach(rawMesh -> rawMesh.applyRotation(new Vector3f(1, 0, 0), 180));
				objModels.put(key, new ObjModel(rawMeshes, flipTextureV));
			});
			return objModels;
		}

		@MappedMethod
		public void addTransformation(ShaderType shaderType, double x, double y, double z, boolean flipped) {
			rawMeshes.forEach(rawMesh -> {
				final RawMesh newRawMesh = new RawMesh(new MaterialProperties(shaderType, rawMesh.materialProperties.getTexture(), rawMesh.materialProperties.vertexAttributeState.color));
				newRawMesh.append(rawMesh);
				newRawMesh.applyTranslation((float) x, (float) y, (float) z);
				if (flipped) {
					newRawMesh.applyRotation(new Vector3f(0, 1, 0), 180);
				}
				rawModel.append(newRawMesh);
			});
		}
	}

	public enum ShaderType {
		CUTOUT, TRANSLUCENT,
		CUTOUT_BRIGHT, TRANSLUCENT_BRIGHT,
		CUTOUT_GLOWING, TRANSLUCENT_GLOWING
	}
}
