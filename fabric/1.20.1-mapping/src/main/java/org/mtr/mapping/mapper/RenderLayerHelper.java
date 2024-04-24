package org.mtr.mapping.mapper;

import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.RenderLayer;
import org.mtr.mapping.holder.VertexFormat;

public final class RenderLayerHelper {

	@MappedMethod
	public static RenderLayer createTriangles(String name, VertexFormat vertexFormat, int expectedBufferSize, boolean hasCrumbling, boolean translucent, RenderLayer renderLayerForPhase) {
		return new RenderLayer(net.minecraft.client.render.RenderLayer.of(
				name,
				vertexFormat.data,
				net.minecraft.client.render.VertexFormat.DrawMode.TRIANGLES,
				expectedBufferSize,
				hasCrumbling,
				translucent,
				((net.minecraft.client.render.RenderLayer.MultiPhase) renderLayerForPhase.data).phases
		));
	}

	@MappedMethod
	public static RenderLayer createQuads(String name, VertexFormat vertexFormat, int expectedBufferSize, boolean hasCrumbling, boolean translucent, RenderLayer renderLayerForPhase) {
		return new RenderLayer(net.minecraft.client.render.RenderLayer.of(
				name,
				vertexFormat.data,
				net.minecraft.client.render.VertexFormat.DrawMode.QUADS,
				expectedBufferSize,
				hasCrumbling,
				translucent,
				((net.minecraft.client.render.RenderLayer.MultiPhase) renderLayerForPhase.data).phases
		));
	}
}
