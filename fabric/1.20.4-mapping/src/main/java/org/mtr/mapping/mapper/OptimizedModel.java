package org.mtr.mapping.mapper;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.RotationAxis;
import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.Identifier;
import org.mtr.mapping.holder.OverlayTexture;
import org.mtr.mapping.render.batch.MaterialProperties;
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
import java.util.ArrayList;
import java.util.List;
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
	public OptimizedModel(List<MaterialGroup> materialGroups) {
		final CapturingVertexConsumer capturingVertexConsumer = new CapturingVertexConsumer();

		materialGroups.forEach(materialGroup -> {
			capturingVertexConsumer.beginStage(materialGroup.materialProperties);
			materialGroup.modelPartConsumers.forEach(modelPartConsumer -> modelPartConsumer.accept(capturingVertexConsumer));
		});

		capturingVertexConsumer.rawModel.triangulate();
		final RawModel parts = new RawModel();
		capturingVertexConsumer.rawModel.iterateRawMeshList(parts::append);
		parts.distinct();
		uploadedParts = parts.upload(DEFAULT_MAPPING);
	}

	@MappedMethod
	public OptimizedModel(Identifier objLocation, @Nullable Identifier atlasIndex) {
		if (atlasIndex != null) {
			ATLAS_MANAGER.load(atlasIndex);
		}

		final RawModel rawModel = ObjModelLoader.loadModel(objLocation, ATLAS_MANAGER);
		if (rawModel == null) {
			uploadedParts = new ArrayList<>();
		} else {
			uploadedParts = rawModel.upload(DEFAULT_MAPPING);
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

	public enum ShaderType {
		CUTOUT, TRANSLUCENT,
		CUTOUT_BRIGHT, TRANSLUCENT_BRIGHT,
		CUTOUT_GLOWING, TRANSLUCENT_GLOWING
	}
}
