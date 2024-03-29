package org.mtr.mapping.render.tool;

import org.mtr.mapping.holder.Matrix4f;
import org.mtr.mapping.holder.Vector3f;

import java.nio.FloatBuffer;

public final class Utilities {

	public static int exchangeLightmapUVBits(int light) {
		return (light >>> 16) | (((short) light) << 16);
	}

	public static Matrix4f create() {
		final Matrix4f matrix4f = new Matrix4f();
		matrix4f.identity();
		return matrix4f;
	}

	public static Matrix4f copy(Matrix4f matrix4f) {
		return new Matrix4f(new org.joml.Matrix4f(matrix4f.data));
	}

	public static void store(Matrix4f matrix4f, FloatBuffer buffer) {
		buffer
				.put(0, matrix4f.m00())
				.put(1, matrix4f.m01())
				.put(2, matrix4f.m02())
				.put(3, matrix4f.m03())
				.put(4, matrix4f.m10())
				.put(5, matrix4f.m11())
				.put(6, matrix4f.m12())
				.put(7, matrix4f.m13())
				.put(8, matrix4f.m20())
				.put(9, matrix4f.m21())
				.put(10, matrix4f.m22())
				.put(11, matrix4f.m23())
				.put(12, matrix4f.m30())
				.put(13, matrix4f.m31())
				.put(14, matrix4f.m32())
				.put(15, matrix4f.m33());
	}

	public static Vector3f transformPosition(Matrix4f matrix4f, Vector3f src) {
		return new Vector3f(matrix4f.transformPosition(copy(src)).data);
	}

	public static Vector3f transformDirection(Matrix4f matrix4f, Vector3f src) {
		return new Vector3f(matrix4f.transformDirection(copy(src)).data);
	}

	public static Vector3f copy(Vector3f vector3f) {
		return new Vector3f(vector3f.getX(), vector3f.getY(), vector3f.getZ());
	}

	public static boolean canUseCustomShader() {
		return !GlStateTracker.isGl4ES(); // TODO and if shader pack is not in use
	}
}
