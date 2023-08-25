package org.mtr.mapping.mapper;

import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.Identifier;
import org.mtr.mapping.holder.ModelAbstractMapping;
import org.mtr.mapping.holder.RenderLayer;

import java.util.function.Function;

public abstract class ModelExtension extends ModelAbstractMapping {

	public ModelExtension(Function<Identifier, RenderLayer> layerFactory, int textureWidth, int textureHeight) {
		super(identifier -> layerFactory.apply(new Identifier(identifier)).data);
		this.textureWidth = textureWidth;
		this.textureHeight = textureHeight;
	}

	@MappedMethod
	public abstract void render(GraphicsHolder graphicsHolder, int light, int overlay, float red, float green, float blue, float alpha);

	@Deprecated
	@Override
	public final void render2(MatrixStack matrixStack, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
		GraphicsHolder.createInstanceSafe(matrixStack, null, graphicsHolder -> {
			graphicsHolder.vertexConsumer = vertexConsumer;
			render(graphicsHolder, light, overlay, red, green, blue, alpha);
		});
	}

	@MappedMethod
	public ModelPartExtension createModelPart() {
		return new ModelPartExtension(this);
	}

	@MappedMethod
	public void buildModel() {
	}
}
