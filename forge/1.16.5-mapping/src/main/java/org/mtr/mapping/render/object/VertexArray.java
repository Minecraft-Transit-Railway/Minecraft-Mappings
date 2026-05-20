package org.mtr.mapping.render.object;

import org.mtr.mapping.render.batch.MaterialProperties;
import org.mtr.mapping.render.model.Mesh;
import org.mtr.mapping.render.vertex.VertexAttributeMapping;

import java.io.Closeable;

public final class VertexArray implements Closeable {
	public final MaterialProperties materialProperties;
	public final IndexBuffer indexBuffer;
	public final VertexAttributeMapping mapping;

	public VertexArray(Mesh mesh, VertexAttributeMapping mapping) {
		materialProperties = mesh.materialProperties;
		indexBuffer = mesh.indexBuffer;
		this.mapping = mapping;
	}

	public VertexArray(VertexArray other, MaterialProperties materialProperties) {
		this.materialProperties = materialProperties;
		indexBuffer = other.indexBuffer;
		mapping = other.mapping;
	}

	public void bind() {
	}

	public static void unbind() {
	}

	public void draw() {
	}

	@Override
	public void close() {
	}
}
