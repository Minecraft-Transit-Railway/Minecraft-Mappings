package org.mtr.mapping.mapper;

import com.mojang.blaze3d.vertex.PoseStack;
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
					final PoseStack matrixStack = new PoseStack();
					matrixStack.translate(x, y, z);
					if (flipped) {
						matrixStack.mulPose(com.mojang.math.Vector3f.YP.rotationDegrees(180));
					}
					modelPart.modelPart.render(matrixStack, capturingVertexConsumer, light, OverlayTexture.getDefaultUvMapped());
				});
			}
		}
	}

	public static final class ObjModel {

		private final float minX;
		private final float minY;
		private final float minZ;
		private final float maxX;
		private final float maxY;
		private final float maxZ;
		private final List<RawMesh> rawMeshes;
		private final RawModel rawModel = new RawModel();

		private ObjModel(
				List<RawMesh> rawMeshes, boolean flipTextureV,
				float minX, float minY, float minZ,
				float maxX, float maxY, float maxZ
		) {
			if (flipTextureV) {
				rawMeshes.forEach(rawMesh -> rawMesh.applyUVMirror(false, true));
			}
			this.minX = minX;
			this.minY = minY;
			this.minZ = minZ;
			this.maxX = maxX;
			this.maxY = maxY;
			this.maxZ = maxZ;
			this.rawMeshes = rawMeshes;
		}

		@MappedMethod
		public static Map<String, ObjModel> loadModel(Identifier objLocation, Identifier defaultTexture, @Nullable Identifier atlasIndex, boolean splitModel, boolean flipTextureV) {
			if (atlasIndex != null) {
				ATLAS_MANAGER.load(atlasIndex);
			}

			final Map<String, ObjModel> objModels = new HashMap<>();
			ObjModelLoader.loadModel(objLocation, defaultTexture, ATLAS_MANAGER, splitModel).forEach((key, rawMeshes) -> {
				final float[] bounds = {Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE, -Float.MAX_VALUE, -Float.MAX_VALUE, -Float.MAX_VALUE};
				rawMeshes.forEach(rawMesh -> {
					rawMesh.applyRotation(new Vector3f(1, 0, 0), 180);
					rawMesh.vertices.forEach(vertex -> {
						final float x = vertex.position.getX();
						final float y = vertex.position.getY();
						final float z = vertex.position.getZ();
						bounds[0] = Math.min(bounds[0], x);
						bounds[1] = Math.min(bounds[1], y);
						bounds[2] = Math.min(bounds[2], z);
						bounds[3] = Math.max(bounds[3], x);
						bounds[4] = Math.max(bounds[4], y);
						bounds[5] = Math.max(bounds[5], z);
					});
				});
				objModels.put(key, new ObjModel(rawMeshes, flipTextureV, bounds[0], bounds[1], bounds[2], bounds[3], bounds[4], bounds[5]));
			});

			return objModels;
		}

		@MappedMethod
		public void addTransformation(ShaderType shaderType, double x, double y, double z, boolean flipped) {
			rawMeshes.forEach(rawMesh -> {
				final RawMesh newRawMesh = new RawMesh(shaderType, rawMesh);
				newRawMesh.applyTranslation((float) x, (float) y, (float) z);
				if (flipped) {
					newRawMesh.applyRotation(new Vector3f(0, 1, 0), 180);
				}
				rawModel.append(newRawMesh);
			});
		}

		@MappedMethod
		public void applyTranslation(double x, double y, double z) {
			rawMeshes.forEach(rawMesh -> rawMesh.applyTranslation((float) x, (float) y, (float) z));
		}

		@MappedMethod
		public void applyRotation(double x, double y, double z) {
			rawMeshes.forEach(rawMesh -> {
				rawMesh.applyRotation(new Vector3f(1, 0, 0), (float) x);
				rawMesh.applyRotation(new Vector3f(0, 1, 0), (float) y);
				rawMesh.applyRotation(new Vector3f(0, 0, 1), (float) z);
			});
		}

		@MappedMethod
		public void applyScale(double x, double y, double z) {
			rawMeshes.forEach(rawMesh -> rawMesh.applyScale((float) x, (float) y, (float) z));
		}

		@MappedMethod
		public void applyMirror(boolean x, boolean y, boolean z) {
			rawMeshes.forEach(rawMesh -> rawMesh.applyMirror(x, y, z, x, y, z));
		}

		@MappedMethod
		public float getMinX() {
			return minX;
		}

		@MappedMethod
		public float getMinY() {
			return minY;
		}

		@MappedMethod
		public float getMinZ() {
			return minZ;
		}

		@MappedMethod
		public float getMaxX() {
			return maxX;
		}

		@MappedMethod
		public float getMaxY() {
			return maxY;
		}

		@MappedMethod
		public float getMaxZ() {
			return maxZ;
		}
	}

	public enum ShaderType {
		CUTOUT, TRANSLUCENT,
		CUTOUT_BRIGHT, TRANSLUCENT_BRIGHT,
		CUTOUT_GLOWING, TRANSLUCENT_GLOWING
	}
}
