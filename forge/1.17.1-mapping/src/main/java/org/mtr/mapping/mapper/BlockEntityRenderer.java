package org.mtr.mapping.mapper;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.phys.Vec3;
import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.Vector3d;

public abstract class BlockEntityRenderer<T extends BlockEntityExtension> implements net.minecraft.client.renderer.blockentity.BlockEntityRenderer<T> {

	@MappedMethod
	public BlockEntityRenderer(Argument argument) {
	}

	@Deprecated
	@Override
	public final void render(T entity, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay) {
		GraphicsHolder.createInstanceSafe(matrices, vertexConsumers, graphicsHolder -> render(entity, tickDelta, graphicsHolder, light, overlay));
	}

	@MappedMethod
	public abstract void render(T entity, float tickDelta, GraphicsHolder graphicsHolder, int light, int overlay);

	@MappedMethod
	public boolean rendersOutsideBoundingBox2(T blockEntity) {
		return net.minecraft.client.renderer.blockentity.BlockEntityRenderer.super.shouldRenderOffScreen(blockEntity);
	}

	@Deprecated
	@Override
	public final boolean shouldRenderOffScreen(T blockEntity) {
		return rendersOutsideBoundingBox2(blockEntity);
	}

	@MappedMethod
	public int getRenderDistance2() {
		return net.minecraft.client.renderer.blockentity.BlockEntityRenderer.super.getViewDistance();
	}

	@Deprecated
	@Override
	public final int getViewDistance() {
		return getRenderDistance2();
	}

	@MappedMethod
	public boolean isInRenderDistance(T blockEntity, Vector3d position) {
		return net.minecraft.client.renderer.blockentity.BlockEntityRenderer.super.shouldRender(blockEntity, position.data);
	}

	@Deprecated
	@Override
	public final boolean shouldRender(T blockEntity, Vec3 position) {
		return isInRenderDistance(blockEntity, new Vector3d(position));
	}

	@Deprecated
	public static final class Argument {

		private final BlockEntityRendererProvider.Context data;

		public Argument(BlockEntityRendererProvider.Context data) {
			this.data = data;
		}
	}
}
