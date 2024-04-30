package org.mtr.mapping.mapper;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.Identifier;
import org.mtr.mapping.holder.ModelAbstractMapping;
import org.mtr.mapping.holder.RenderLayer;

import java.util.function.Function;

public abstract class ModelExtension extends ModelAbstractMapping implements ModelHelper {

	@MappedMethod
	public ModelExtension(Function<Identifier, RenderLayer> layerFactory, int textureWidth, int textureHeight) {
		super(identifier -> layerFactory.apply(new Identifier(identifier)).data);
		texWidth = textureWidth;
		texHeight = textureHeight;
	}

	@Deprecated
	@Override
	public final void renderToBuffer(MatrixStack matrixStack, IVertexBuilder vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
		render3(matrixStack, vertexConsumer, light, overlay, red, green, blue, alpha);
	}

	@MappedMethod
	public final ModelPartExtension createModelPart() {
		return new ModelPartExtension(this);
	}

	@MappedMethod
	public final void buildModel() {
	}
}
