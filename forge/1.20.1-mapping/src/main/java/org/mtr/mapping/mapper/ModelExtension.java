package org.mtr.mapping.mapper;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.Identifier;
import org.mtr.mapping.holder.ModelAbstractMapping;
import org.mtr.mapping.holder.RenderLayer;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public abstract class ModelExtension extends ModelAbstractMapping implements ModelHelper {

	private final int textureWidth;
	private final int textureHeight;
	private final MeshDefinition modelData = new MeshDefinition();
	private final PartDefinition modelPartData = modelData.getRoot();
	private final List<ModelPartExtension> modelPartExtensions = new ArrayList<>();

	@MappedMethod
	public ModelExtension(Function<Identifier, RenderLayer> layerFactory, int textureWidth, int textureHeight) {
		super(identifier -> layerFactory.apply(new Identifier(identifier)).data);
		this.textureWidth = textureWidth;
		this.textureHeight = textureHeight;
	}

	@Deprecated
	@Override
	public final void renderToBuffer(PoseStack matrixStack, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
		render3(matrixStack, vertexConsumer, light, overlay, red, green, blue, alpha);
	}

	@MappedMethod
	public final ModelPartExtension createModelPart() {
		final ModelPartExtension modelPartExtension = new ModelPartExtension(modelPartData);
		modelPartExtensions.add(modelPartExtension);
		return modelPartExtension;
	}

	@MappedMethod
	public final void buildModel() {
		final ModelPart modelPart = LayerDefinition.create(modelData, textureWidth, textureHeight).bakeRoot();
		modelPartExtensions.forEach(modelPartExtension -> modelPartExtension.setModelPart(modelPart));
	}
}
