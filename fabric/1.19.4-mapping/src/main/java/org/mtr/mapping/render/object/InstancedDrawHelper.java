package org.mtr.mapping.render.object;

import org.lwjgl.opengl.ARBInstancedArrays;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL33;
import org.mtr.mapping.render.vertex.VertexAttributeMapping;
import org.mtr.mapping.render.vertex.VertexAttributeType;

import java.nio.ByteBuffer;

public final class InstancedDrawHelper {

	public static boolean setupInstanceAttributes(VertexArray vertexArray, VertexBuffer instanceBuffer, VertexAttributeMapping mapping, VertexAttributeType... vertexAttributeTypes) {
		vertexArray.bind();
		instanceBuffer.bind(GL33.GL_ARRAY_BUFFER);
		boolean supported = true;
		for (final VertexAttributeType vertexAttributeType : vertexAttributeTypes) {
			supported &= setupInstanceAttribute(mapping, vertexAttributeType);
		}
		VertexArray.unbind();
		return supported;
	}

	public static void setupInstanceAttributePointers(VertexBuffer instanceBuffer, VertexAttributeMapping mapping, int instanceOffsetBytes, VertexAttributeType... vertexAttributeTypes) {
		instanceBuffer.bind(GL33.GL_ARRAY_BUFFER);
		for (final VertexAttributeType vertexAttributeType : vertexAttributeTypes) {
			vertexAttributeType.setupAttributePointer(mapping.strideInstance, instanceOffsetBytes + mapping.pointers.get(vertexAttributeType));
		}
	}

	public static void uploadInstances(VertexBuffer instanceBuffer, ByteBuffer byteBuffer, int size) {
		instanceBuffer.bind(GL33.GL_ARRAY_BUFFER);
		instanceBuffer.upload(byteBuffer, size, VertexBuffer.USAGE_STREAM_DRAW);
	}

	public static void drawElementsInstanced(VertexArray vertexArray, int instanceCount) {
		GL33.glDrawElementsInstanced(GL33.GL_TRIANGLES, vertexArray.indexBuffer.getVertexCount(), vertexArray.indexBuffer.indexType, 0, instanceCount);
	}

	private static boolean setupInstanceAttribute(VertexAttributeMapping mapping, VertexAttributeType vertexAttributeType) {
		vertexAttributeType.toggleAttributeArray(true);
		vertexAttributeType.setupAttributePointer(mapping.strideInstance, mapping.pointers.get(vertexAttributeType));
		return applyInstanceAttributeDivisor(vertexAttributeType);
	}

	private static boolean applyInstanceAttributeDivisor(VertexAttributeType vertexAttributeType) {
		boolean supported = true;
		for (int i = 0; i < vertexAttributeType.span; i++) {
			supported &= applyInstanceAttributeDivisor(vertexAttributeType.location + i);
		}
		return supported;
	}

	private static boolean applyInstanceAttributeDivisor(int location) {
		try {
			final org.lwjgl.opengl.GLCapabilities capabilities = GL.getCapabilities();
			if (capabilities.OpenGL33) {
				GL33.glVertexAttribDivisor(location, 1);
				return true;
			} else if (capabilities.GL_ARB_instanced_arrays) {
				ARBInstancedArrays.glVertexAttribDivisorARB(location, 1);
				return true;
			}
		} catch (IllegalStateException ignored) {
		}
		return false;
	}

	private InstancedDrawHelper() {
	}
}
