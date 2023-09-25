package org.mtr.mapping.mapper;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.PartDefinition;
import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.tool.DummyClass;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public final class ModelPartExtension extends DummyClass {

	private float tempPivotX, tempPivotY, tempPivotZ;
	private float tempRotationX, tempRotationY, tempRotationZ;
	private int tempU, tempV;
	private ModelPart modelPart;
	private PartDefinition additionalModelPartData;
	private ModelPartExtension parent;

	private final List<String> nameTree = new ArrayList<>();
	private final PartDefinition modelPartData;

	ModelPartExtension(PartDefinition modelPartData) {
		nameTree.add(getRandomPartName());
		this.modelPartData = modelPartData;
	}

	@MappedMethod
	public void setPivot(float x, float y, float z) {
		tempPivotX = x;
		tempPivotY = y;
		tempPivotZ = z;
	}

	@MappedMethod
	public ModelPartExtension setTextureUVOffset(int textureOffsetU, int textureOffsetV) {
		tempU = textureOffsetU;
		tempV = textureOffsetV;
		return this;
	}

	@MappedMethod
	public void setRotation(float rotationX, float rotationY, float rotationZ) {
		tempRotationX = rotationX;
		tempRotationY = rotationY;
		tempRotationZ = rotationZ;
	}

	@MappedMethod
	public void addChild(ModelPartExtension modelPartExtension) {
		modelPartExtension.parent = this;
		modelPartExtension.nameTree.addAll(0, nameTree);
	}

	@MappedMethod
	public void addCuboid(float x, float y, float z, int sizeX, int sizeY, int sizeZ, float inflation, boolean mirrored) {
		final String name = nameTree.isEmpty() ? "" : nameTree.get(nameTree.size() - 1);
		final CubeListBuilder modelPartBuilder = CubeListBuilder.create().mirror(mirrored).addBox(name, x, y, z, sizeX, sizeY, sizeZ, new CubeDeformation(inflation), tempU, tempV);
		final PartPose modelTransform = PartPose.offsetAndRotation(tempPivotX, tempPivotY, tempPivotZ, tempRotationX, tempRotationY, tempRotationZ);

		if (parent == null) {
			if (additionalModelPartData == null) {
				additionalModelPartData = modelPartData.addOrReplaceChild(name, modelPartBuilder, modelTransform);
			} else {
				additionalModelPartData.addOrReplaceChild(getRandomPartName(), modelPartBuilder, PartPose.offsetAndRotation(0, 0, 0, 0, 0, 0));
			}
		} else {
			if (parent.additionalModelPartData == null) {
				parent.addCuboid(0, 0, 0, 0, 0, 0, 0, false);
			}
			additionalModelPartData = parent.additionalModelPartData.addOrReplaceChild(name, modelPartBuilder, modelTransform);
			parent = null;
		}
	}

	@MappedMethod
	public void setOffset(float x, int y, float z) {
		if (modelPart != null) {
			modelPart.setPos(x, y, z);
		}
	}

	@MappedMethod
	public void render(GraphicsHolder graphicsHolder, float x, float z, float rotateY, int light, int overlay) {
		render(graphicsHolder, x, 0, z, rotateY, light, overlay);
	}

	@MappedMethod
	public void render(GraphicsHolder graphicsHolder, float x, float y, float z, float rotateY, int light, int overlay) {
		if (modelPart != null) {
			modelPart.setPos(x, y, z);
			modelPart.yRot = rotateY;
			if (graphicsHolder.matrixStack != null && graphicsHolder.vertexConsumer != null) {
				modelPart.render(graphicsHolder.matrixStack, graphicsHolder.vertexConsumer, light, overlay);
			}
		}
	}

	@Deprecated
	void setModelPart(ModelPart mainModelPart) {
		modelPart = mainModelPart;
		nameTree.forEach(name -> modelPart = modelPart.getChild(name));
	}

	private static String getRandomPartName() {
		return "part" + Math.abs(new Random().nextLong());
	}
}
