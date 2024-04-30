package org.mtr.mapping.mapper;

import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.EntityAbstractMapping;
import org.mtr.mapping.holder.EntityModelAbstractMapping;
import org.mtr.mapping.holder.Identifier;
import org.mtr.mapping.holder.RenderLayer;

import java.util.function.Function;

public abstract class EntityModelExtension<T extends EntityAbstractMapping> extends EntityModelAbstractMapping<T> implements ModelHelper {

	@MappedMethod
	public EntityModelExtension(Function<Identifier, RenderLayer> layerFactory, int textureWidth, int textureHeight) {
		super(identifier -> layerFactory.apply(new Identifier(identifier)).data);
		this.textureWidth = textureWidth;
		this.textureHeight = textureHeight;
	}

	@MappedMethod
	public EntityModelExtension(int textureWidth, int textureHeight) {
		super();
		this.textureWidth = textureWidth;
		this.textureHeight = textureHeight;
	}

	@Deprecated
	@Override
	public final void render(MatrixStack matrixStack, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
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
