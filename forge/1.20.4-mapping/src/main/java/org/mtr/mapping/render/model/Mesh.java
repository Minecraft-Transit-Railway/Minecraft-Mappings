package org.mtr.mapping.render.model;

import org.mtr.mapping.render.batch.MaterialProperties;
import org.mtr.mapping.render.object.IndexBuffer;
import org.mtr.mapping.render.object.VertexBuffer;

import java.io.Closeable;

public final class Mesh implements Closeable {

	public final VertexBuffer vertexBuffer;
	public final IndexBuffer indexBuffer;

	public final MaterialProperties materialProperties;

	public Mesh(VertexBuffer vertexBuffer, IndexBuffer indexBuffer, MaterialProperties materialProperties) {
		this.vertexBuffer = vertexBuffer;
		this.indexBuffer = indexBuffer;
		this.materialProperties = materialProperties;
	}

	@Override
	public void close() {
		vertexBuffer.close();
		indexBuffer.close();
	}
}
