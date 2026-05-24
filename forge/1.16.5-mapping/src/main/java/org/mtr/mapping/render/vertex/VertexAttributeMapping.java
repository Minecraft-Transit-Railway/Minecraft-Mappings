package org.mtr.mapping.render.vertex;

import org.mtr.mapping.render.object.VertexBuffer;

import java.util.HashMap;
import java.util.Map;

public final class VertexAttributeMapping {

	public final Map<VertexAttributeType, VertexAttributeSource> sources;
	public final Map<VertexAttributeType, Integer> pointers = new HashMap<>();
	public final int strideVertex;
	public final int strideInstance;
	public final int paddingVertex;
	public final int paddingInstance;

	private VertexAttributeMapping(Map<VertexAttributeType, VertexAttributeSource> sources) {
		this.sources = sources;
		int strideVertex = 0;
		int strideInstance = 0;
		for (final VertexAttributeType vertexAttributeType : VertexAttributeType.values()) {
			switch (sources.get(vertexAttributeType)) {
				case VERTEX_BUFFER:
					pointers.put(vertexAttributeType, strideVertex);
					strideVertex += vertexAttributeType.byteSize;
					break;
				case INSTANCE_BUFFER:
					pointers.put(vertexAttributeType, strideInstance);
					strideInstance += vertexAttributeType.byteSize;
					break;
				default:
					break;
			}
		}
		this.strideVertex = strideVertex;
		this.strideInstance = strideInstance;
		paddingVertex = 0;
		paddingInstance = 0;
	}

	public void setupAttributesToVao(VertexBuffer vertexBuffer) {
	}

	public static class Builder {
		private final HashMap<VertexAttributeType, VertexAttributeSource> sources = new HashMap<>();

		public Builder() {
			for (final VertexAttributeType vertexAttributeType : VertexAttributeType.values()) {
				sources.put(vertexAttributeType, VertexAttributeSource.VERTEX_BUFFER);
			}
		}

		public Builder set(VertexAttributeType vertexAttributeType, VertexAttributeSource vertexAttributeSource) {
			sources.put(vertexAttributeType, vertexAttributeSource);
			return this;
		}

		public VertexAttributeMapping build() {
			return new VertexAttributeMapping(sources);
		}
	}
}
