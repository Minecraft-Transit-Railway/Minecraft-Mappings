package org.mtr.mapping.render.object;

import org.mtr.mapping.render.vertex.VertexAttributeMapping;
import org.mtr.mapping.render.vertex.VertexAttributeType;

import java.nio.ByteBuffer;

public final class InstancedDrawHelper {

	public static boolean setupInstanceAttributes(VertexArray vertexArray, VertexBuffer instanceBuffer, VertexAttributeMapping mapping, VertexAttributeType... vertexAttributeTypes) {
		return true;
	}

	public static void setupInstanceAttributePointers(VertexBuffer instanceBuffer, VertexAttributeMapping mapping, int instanceOffsetBytes, VertexAttributeType... vertexAttributeTypes) {
	}

	public static void uploadInstances(VertexBuffer instanceBuffer, ByteBuffer byteBuffer, int size) {
		instanceBuffer.upload(byteBuffer);
	}

	public static void drawElementsInstanced(VertexArray vertexArray, int instanceCount) {
	}

	private InstancedDrawHelper() {
	}
}
