package org.mtr.mapping.mapper;

import net.minecraft.client.model.*;
import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.tool.DummyClass;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public final class ModelPartExtension extends DummyClass {

	ModelPart modelPart;
	private float tempPivotX, tempPivotY, tempPivotZ;
	private float tempRotationX, tempRotationY, tempRotationZ;
	private int tempU, tempV;

	private final List<String> nameTree = new ArrayList<>();
	private final ModelPartData modelPartData;

	ModelPartExtension(ModelPartData modelPartData) {
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
	public ModelPartExtension addChild() {
		final ModelPartExtension modelPartExtension = new ModelPartExtension(modelPartData.addChild(getLastName(), ModelPartBuilder.create(), getModelTransform()));
		modelPartExtension.nameTree.addAll(0, nameTree);
		return modelPartExtension;
	}

	@MappedMethod
	public void addCuboid(float x, float y, float z, int sizeX, int sizeY, int sizeZ, float inflation, boolean mirrored) {
		final String name = getLastName();
		final ModelPartBuilder modelPartBuilder = ModelPartBuilder.create().mirrored(mirrored).cuboid(name, x, y, z, sizeX, sizeY, sizeZ, new Dilation(inflation), tempU, tempV);
		modelPartData.addChild(name, modelPartBuilder, getModelTransform());
	}

	@MappedMethod
	public void setOffset(float x, int y, float z) {
		if (modelPart != null) {
			modelPart.setPivot(x, y, z);
		}
	}

	@MappedMethod
	public void render(GraphicsHolder graphicsHolder, float x, float z, float rotateY, int light, int overlay) {
		render(graphicsHolder, x, 0, z, rotateY, light, overlay);
	}

	@MappedMethod
	public void render(GraphicsHolder graphicsHolder, float x, float y, float z, float rotateY, int light, int overlay) {
		if (modelPart != null) {
			modelPart.setPivot(x, y, z);
			modelPart.yaw = rotateY;
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

	private String getLastName() {
		return nameTree.isEmpty() ? "" : nameTree.get(nameTree.size() - 1);
	}

	private ModelTransform getModelTransform() {
		return ModelTransform.of(tempPivotX, tempPivotY, tempPivotZ, tempRotationX, tempRotationY, tempRotationZ);
	}

	private static String getRandomPartName() {
		return "part" + Math.abs(new Random().nextLong());
	}
}
