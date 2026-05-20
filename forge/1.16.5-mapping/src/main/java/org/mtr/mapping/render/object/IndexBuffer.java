package org.mtr.mapping.render.object;

import java.io.Closeable;

public final class IndexBuffer implements Closeable {
	public final int indexType = 0;
	private final int vertexCount;

	public IndexBuffer(int vertexCount) {
		this.vertexCount = vertexCount;
	}

	public void bind(int target) {
	}

	public int getVertexCount() {
		return vertexCount;
	}

	@Override
	public void close() {
	}
}
