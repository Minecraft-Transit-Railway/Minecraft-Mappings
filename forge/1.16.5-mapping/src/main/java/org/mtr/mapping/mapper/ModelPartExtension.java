package org.mtr.mapping.mapper;

import net.minecraft.client.renderer.model.ModelRenderer;
import org.mtr.mapping.annotation.MappedMethod;

public final class ModelPartExtension extends ModelRenderer {

	ModelPartExtension(ModelExtension modelExtension) {
		super(modelExtension);
	}

	@MappedMethod
	public void setPivot(float x, float y, float z) {
		super.setPos(x, y, z);
	}

	@MappedMethod
	public ModelPartExtension setTextureUVOffset(int textureOffsetU, int textureOffsetV) {
		return (ModelPartExtension) super.texOffs(textureOffsetU, textureOffsetV);
	}

	@MappedMethod
	public void setRotation(float rotationX, float rotationY, float rotationZ) {
		xRot = rotationX;
		yRot = rotationY;
		zRot = rotationZ;
	}

	@MappedMethod
	public void addChild(ModelPartExtension modelPartExtension) {
		super.addChild(modelPartExtension);
	}

	@MappedMethod
	public void addCuboid(float x, float y, float z, int sizeX, int sizeY, int sizeZ, float inflation, boolean mirrored) {
		super.addBox(x, y, z, sizeX, sizeY, sizeZ, inflation, mirrored);
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
		yRot = rotateY;
		if (graphicsHolder.matrixStack != null && graphicsHolder.vertexConsumer != null) {
			render(graphicsHolder.matrixStack, graphicsHolder.vertexConsumer, light, overlay);
		}
	}
}
