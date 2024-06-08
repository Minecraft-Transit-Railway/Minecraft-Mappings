package org.mtr.mapping.mapper;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.Frustum;
import org.mtr.mapping.holder.Identifier;

public abstract class EntityRenderer<T extends EntityExtension> extends net.minecraft.client.render.entity.EntityRenderer<T> {

	@MappedMethod
	public EntityRenderer(Argument argument) {
		super(argument.data);
	}

	@Deprecated
	@Override
	public final void render(T entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
		GraphicsHolder.createInstanceSafe(matrices, vertexConsumers, graphicsHolder -> render(entity, yaw, tickDelta, graphicsHolder, light));
	}

	@MappedMethod
	public abstract void render(T entity, float yaw, float tickDelta, GraphicsHolder graphicsHolder, int light);

	@Deprecated
	@Override
	public final net.minecraft.util.Identifier getTexture(T entity) {
		return getTexture2(entity).data;
	}

	@MappedMethod
	public abstract Identifier getTexture2(T entity);

	@Deprecated
	@Override
	public final boolean shouldRender(T entity, net.minecraft.client.render.Frustum frustum, double x, double y, double z) {
		return shouldRender2(entity, new Frustum(frustum), x, y, z);
	}

	@MappedMethod
	public boolean shouldRender2(T entity, Frustum frustum, double x, double y, double z) {
		return super.shouldRender(entity, frustum.data, x, y, z);
	}

	@Deprecated
	public static final class Argument {

		private final EntityRenderDispatcher data;

		public Argument(EntityRenderDispatcher data) {
			this.data = data;
		}
	}
}
