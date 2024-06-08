package org.mtr.mapping.render.tool;

import net.minecraft.util.math.Matrix3f;
import net.minecraft.util.math.Vector4f;
import org.mtr.mapping.holder.Matrix4f;
import org.mtr.mapping.holder.Vector3f;
import org.mtr.mapping.render.shader.ModShaderHandler;

import java.nio.FloatBuffer;

public final class Utilities {

	public static int exchangeLightmapUVBits(int light) {
		return (light >>> 16) | (((short) light) << 16);
	}

	public static Matrix4f create() {
		final Matrix4f matrix4f = new Matrix4f();
		matrix4f.data.loadIdentity();
		return matrix4f;
	}

	public static Matrix4f copy(Matrix4f matrix4f) {
		return new Matrix4f(new net.minecraft.util.math.Matrix4f(matrix4f.data));
	}

	public static void store(Matrix4f matrix4f, FloatBuffer buffer) {
		matrix4f.data.writeColumnMajor(buffer);
	}

	public static Vector3f transformPosition(Matrix4f matrix4f, Vector3f src) {
		final Vector4f vector4f = new Vector4f(src.getX(), src.getY(), src.getZ(), 1.0F);
		vector4f.transform(matrix4f.data);
		return new Vector3f(vector4f.getX(), vector4f.getY(), vector4f.getZ());
	}

	public static Vector3f transformDirection(Matrix4f matrix4f, Vector3f src) {
		final Vector3f vector3f = copy(src);
		vector3f.data.transform(new Matrix3f(matrix4f.data));
		return vector3f;
	}

	public static Vector3f copy(Vector3f vector3f) {
		return new Vector3f(vector3f.getX(), vector3f.getY(), vector3f.getZ());
	}

	public static boolean canUseCustomShader() {
		return ModShaderHandler.getInternalHandler().noShaderPackInUse() && !GlStateTracker.isGl4ES();
	}
}
