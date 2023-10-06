package org.mtr.mapping.mapper;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;
import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.Identifier;

public abstract class EntityRenderer<T extends EntityExtension> extends net.minecraft.client.renderer.entity.EntityRenderer<T> {

	@MappedMethod
	public EntityRenderer(Argument argument) {
		super(argument.data);
	}

	@Deprecated
	@Override
	public final void render(T entity, float yaw, float tickDelta, MatrixStack matrices, IRenderTypeBuffer vertexConsumers, int light) {
		GraphicsHolder.createInstanceSafe(matrices, vertexConsumers, graphicsHolder -> render(entity, yaw, tickDelta, graphicsHolder, light));
	}

	@MappedMethod
	public abstract void render(T entity, float yaw, float tickDelta, GraphicsHolder graphicsHolder, int light);

	@Deprecated
	@Override
	public final ResourceLocation getTextureLocation(T entity) {
		return getTexture2(entity).data;
	}

	@MappedMethod
	public abstract Identifier getTexture2(T entity);

	@Deprecated
	public static final class Argument {

		private final EntityRendererManager data;

		public Argument(EntityRendererManager data) {
			this.data = data;
		}
	}
}
