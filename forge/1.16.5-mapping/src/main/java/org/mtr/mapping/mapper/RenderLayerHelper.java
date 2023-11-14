package org.mtr.mapping.mapper;

import net.minecraft.client.renderer.RenderType;
import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.RenderLayer;
import org.mtr.mapping.holder.VertexFormat;

public final class RenderLayerHelper {

	@MappedMethod
	public static RenderLayer createTriangles(String name, VertexFormat vertexFormat, int expectedBufferSize, boolean hasCrumbling, boolean translucent, RenderLayer renderLayerForPhase) {
		return new RenderLayer(RenderType.create(
				name,
				vertexFormat.data,
				4,
				expectedBufferSize,
				hasCrumbling,
				translucent,
				((RenderType.Type) renderLayerForPhase.data).state
		));
	}

	@MappedMethod
	public static RenderLayer createQuads(String name, VertexFormat vertexFormat, int expectedBufferSize, boolean hasCrumbling, boolean translucent, RenderLayer renderLayerForPhase) {
		return new RenderLayer(RenderType.create(
				name,
				vertexFormat.data,
				7,
				expectedBufferSize,
				hasCrumbling,
				translucent,
				((RenderType.Type) renderLayerForPhase.data).state
		));
	}
}
