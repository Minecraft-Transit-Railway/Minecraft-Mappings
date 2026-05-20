package org.mtr.mapping.render.tool;

import org.mtr.mapping.holder.Matrix4f;
import org.mtr.mapping.holder.Vector3f;

import java.nio.FloatBuffer;

public final class Utilities {
	public static int exchangeLightmapUVBits(int light) {
		return (light >>> 16) | (((short) light) << 16);
	}

	public static Matrix4f create() {
		return new Matrix4f();
	}

	public static Matrix4f copy(Matrix4f matrix4f) {
		return matrix4f;
	}

	public static void store(Matrix4f matrix4f, FloatBuffer buffer) {
	}

	public static Vector3f transformPosition(Matrix4f matrix4f, Vector3f src) {
		return src;
	}

	public static Vector3f transformDirection(Matrix4f matrix4f, Vector3f src) {
		return src;
	}

	public static Vector3f copy(Vector3f vector3f) {
		return vector3f;
	}

	public static boolean canUseCustomShader() {
		return false;
	}
}
