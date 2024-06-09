package org.mtr.mapping.render.tool;

import org.joml.Matrix3f;
import org.mtr.mapping.holder.Matrix4f;
import org.mtr.mapping.holder.Vector3f;

import java.nio.FloatBuffer;

public final class Utilities {

	public static int exchangeLightmapUVBits(int light) {
		return (light >>> 16) | (((short) light) << 16);
	}

	public static org.mtr.mapping.holder.Matrix3f createMatrix3f() {
		final org.mtr.mapping.holder.Matrix3f matrix3f = new org.mtr.mapping.holder.Matrix3f();
		matrix3f.data.identity();
		return matrix3f;
	}

	public static Matrix4f create() {
		final Matrix4f matrix4f = new Matrix4f();
		matrix4f.data.identity();
		return matrix4f;
	}

	public static Matrix4f create(float m00, float m01, float m02, float m03,
								  float m10, float m11, float m12, float m13,
								  float m20, float m21, float m22, float m23,
								  float m30, float m31, float m32, float m33) {
		return new Matrix4f(new org.joml.Matrix4f(m00, m01, m02, m03, m10, m11, m12, m13, m20, m21, m22, m23, m30, m31, m32, m33));
	}

	public static org.mtr.mapping.holder.Matrix3f create(Matrix4f src){
		return new org.mtr.mapping.holder.Matrix3f(new Matrix3f(src.data));
	}

	public static org.mtr.mapping.holder.Matrix3f copy(org.mtr.mapping.holder.Matrix3f src){
		return new org.mtr.mapping.holder.Matrix3f(new Matrix3f(src.data));
	}

	public static Matrix4f copy(Matrix4f matrix4f) {
		return new Matrix4f(new org.joml.Matrix4f(matrix4f.data));
	}

	public static void store(Matrix4f matrix4f, FloatBuffer buffer) {
		buffer
				.put(0, matrix4f.data.m00())
				.put(1, matrix4f.data.m01())
				.put(2, matrix4f.data.m02())
				.put(3, matrix4f.data.m03())
				.put(4, matrix4f.data.m10())
				.put(5, matrix4f.data.m11())
				.put(6, matrix4f.data.m12())
				.put(7, matrix4f.data.m13())
				.put(8, matrix4f.data.m20())
				.put(9, matrix4f.data.m21())
				.put(10, matrix4f.data.m22())
				.put(11, matrix4f.data.m23())
				.put(12, matrix4f.data.m30())
				.put(13, matrix4f.data.m31())
				.put(14, matrix4f.data.m32())
				.put(15, matrix4f.data.m33());
	}

	public static Vector3f transformPosition(Matrix4f matrix4f, Vector3f src) {
		return new Vector3f(matrix4f.data.transformPosition(copy(src).data));
	}

	public static Vector3f transformDirection(Matrix4f matrix4f, Vector3f src) {
		return new Vector3f(matrix4f.data.transformDirection(copy(src).data));
	}

	public static Vector3f copy(Vector3f vector3f) {
		return new Vector3f(vector3f.getX(), vector3f.getY(), vector3f.getZ());
	}

	public static boolean canUseCustomShader() {
		return !GlStateTracker.isGl4ES(); // TODO and if shader pack is not in use
	}

	public static void mul(org.mtr.mapping.holder.Matrix3f src, org.mtr.mapping.holder.Matrix3f dest){
		src.data.mul(dest.data);
	}

	public static void mul(Matrix4f src, Matrix4f dest){
		src.data.mul(dest.data);
	}

	public static void mul(org.mtr.mapping.holder.Vector4f src, Matrix4f dest){
		src.data.mul(dest.data);
	}

	public static void mul(Matrix4f src, float dest) {
		src.data.scale(dest);
	}

	public static void mul(org.mtr.mapping.holder.Matrix3f src, float dest){
		src.data.scale(dest);
	}

	public static Matrix4f createScaleMatrix(float x, float y, float z) {
		Matrix4f matrix4f = create();
		matrix4f.data.scale(x,y,z);
		return matrix4f;
	}
}
