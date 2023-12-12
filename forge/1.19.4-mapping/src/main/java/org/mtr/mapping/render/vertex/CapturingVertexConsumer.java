package org.mtr.mapping.render.vertex;

import com.mojang.blaze3d.vertex.VertexConsumer;
import org.mtr.mapping.holder.Vector3f;
import org.mtr.mapping.render.batch.MaterialProperties;
import org.mtr.mapping.render.model.RawMesh;
import org.mtr.mapping.render.model.RawModel;

public final class CapturingVertexConsumer implements VertexConsumer {

	private RawMesh buildingMesh;
	private Vertex buildingVertex = new Vertex();

	public final RawModel rawModel = new RawModel();

	public void beginStage(MaterialProperties materialProperties) {
		buildingMesh = rawModel.getRawMesh(materialProperties);
	}

	@Override
	public VertexConsumer vertex(double x, double y, double z) {
		buildingVertex.position = new Vector3f((float) x, (float) y, (float) z);
		return this;
	}

	@Override
	public VertexConsumer color(int red, int green, int blue, int alpha) {
		// Unused
		return this;
	}

	@Override
	public VertexConsumer uv(float u, float v) {
		buildingVertex.u = u;
		buildingVertex.v = v;
		return this;
	}

	@Override
	public VertexConsumer overlayCoords(int u, int v) {
		// Unused
		return this;
	}

	@Override
	public VertexConsumer uv2(int u, int v) {
		// Unused
		return this;
	}

	@Override
	public VertexConsumer normal(float x, float y, float z) {
		buildingVertex.normal = new Vector3f(x, y, z);
		return this;
	}

	@Override
	public void endVertex() {
		buildingMesh.addVertex(buildingVertex);
		buildingVertex = new Vertex();
	}

	@Override
	public void defaultColor(int red, int green, int blue, int alpha) {
	}

	@Override
	public void unsetDefaultColor() {
	}
}
