package org.mtr.mapping.render.model;

import org.mtr.mapping.holder.Vector3f;
import org.mtr.mapping.mapper.OptimizedModel;
import org.mtr.mapping.render.batch.MaterialProperties;
import org.mtr.mapping.render.object.IndexBuffer;
import org.mtr.mapping.render.object.VertexBuffer;
import org.mtr.mapping.render.vertex.Vertex;
import org.mtr.mapping.render.vertex.VertexAttributeMapping;

import java.util.ArrayList;
import java.util.List;

public final class RawMesh {
	public final MaterialProperties materialProperties;
	public final List<Vertex> vertices = new ArrayList<>();

	public RawMesh(MaterialProperties materialProperties) {
		this.materialProperties = materialProperties;
	}

	public RawMesh(OptimizedModel.ShaderType shaderType, RawMesh rawMesh) {
		materialProperties = new MaterialProperties(shaderType, rawMesh.materialProperties.getTexture(), rawMesh.materialProperties.vertexAttributeState.color);
		vertices.addAll(rawMesh.vertices);
	}

	public void append(RawMesh nextMesh) {
		vertices.addAll(nextMesh.vertices);
	}

	public void applyTranslation(float x, float y, float z) {
	}

	public void applyRotation(Vector3f axis, float angle) {
	}

	public void applyScale(float x, float y, float z) {
	}

	public void applyMirror(boolean vx, boolean vy, boolean vz, boolean nx, boolean ny, boolean nz) {
	}

	public void applyUVMirror(boolean u, boolean v) {
	}

	public void generateNormals() {
	}

	public void distinct() {
	}

	public void triangulate() {
	}

	public void validateVertexIndex() {
	}

	public Mesh upload(VertexAttributeMapping vertexAttributeMapping) {
		return new Mesh(new VertexBuffer(), new IndexBuffer(0), materialProperties);
	}
}
