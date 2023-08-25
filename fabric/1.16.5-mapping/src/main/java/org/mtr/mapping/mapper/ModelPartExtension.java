package org.mtr.mapping.mapper;

import net.minecraft.client.model.Model;
import net.minecraft.client.model.ModelPart;
import org.mtr.mapping.annotation.MappedMethod;

public final class ModelPartExtension extends ModelPart {

	ModelPartExtension(Model model) {
		super(model);
	}

	@MappedMethod
	@Override
	public void setPivot(float x, float y, float z) {
		super.setPivot(x, y, z);
	}

	@MappedMethod
	public ModelPartExtension setTextureUVOffset(int textureOffsetU, int textureOffsetV) {
		return (ModelPartExtension) super.setTextureOffset(textureOffsetU, textureOffsetV);
	}

	@MappedMethod
	public void setRotation(float rotationX, float rotationY, float rotationZ) {
		pitch = rotationX;
		yaw = rotationY;
		roll = rotationZ;
	}

	@MappedMethod
	public void addChild(ModelPartExtension modelPartExtension) {
		super.addChild(modelPartExtension);
	}

	@MappedMethod
	public void addCuboid(float x, float y, float z, int sizeX, int sizeY, int sizeZ, float inflation, boolean mirrored) {
		super.addCuboid(x, y, z, sizeX, sizeY, sizeZ, inflation, mirrored);
	}

	@MappedMethod
	public void setOffset(float x, int y, float z) {
		setPivot(x, y, z);
	}

	@MappedMethod
	public void render(GraphicsHolder graphicsHolder, float x, float z, float rotateY, int light, int overlay) {
		render(graphicsHolder, x, 0, z, rotateY, light, overlay);
	}

	@MappedMethod
	public void render(GraphicsHolder graphicsHolder, float x, float y, float z, float rotateY, int light, int overlay) {
		setPivot(x, y, z);
		yaw = rotateY;
		if (graphicsHolder.matrixStack != null && graphicsHolder.vertexConsumer != null) {
			render(graphicsHolder.matrixStack, graphicsHolder.vertexConsumer, light, overlay);
		}
	}
}
