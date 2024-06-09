package org.mtr.mapping.render.tool;

import net.minecraft.util.math.vector.Matrix3f;
import net.minecraft.util.math.vector.Vector4f;
import org.mtr.mapping.holder.Matrix4f;
import org.mtr.mapping.holder.Vector3f;

import java.nio.FloatBuffer;

public final class Utilities {

    public static int exchangeLightmapUVBits(int light) {
        return (light >>> 16) | (((short) light) << 16);
    }

    public static org.mtr.mapping.holder.Matrix3f createMatrix3f() {
        final org.mtr.mapping.holder.Matrix3f matrix3f = new org.mtr.mapping.holder.Matrix3f();
        matrix3f.data.setIdentity();
        return matrix3f;
    }

    public static Matrix4f create() {
        final Matrix4f matrix4f = new Matrix4f();
        matrix4f.data.setIdentity();
        return matrix4f;
    }


    public static Matrix4f create(float m00, float m01, float m02, float m03,
                                  float m10, float m11, float m12, float m13,
                                  float m20, float m21, float m22, float m23,
                                  float m30, float m31, float m32, float m33) {
        float[] columnAndRow = new float[]{
                m00, m10, m20, m30,
                m01, m11, m21, m31,
                m02, m12, m22, m32,
                m03, m13, m23, m33};
        return new Matrix4f(new net.minecraft.util.math.vector.Matrix4f(columnAndRow));
    }

    public static org.mtr.mapping.holder.Matrix3f create(Matrix4f src){
        return new org.mtr.mapping.holder.Matrix3f(new Matrix3f(src.data));
    }

    public static org.mtr.mapping.holder.Matrix3f copy(org.mtr.mapping.holder.Matrix3f src){
        return new org.mtr.mapping.holder.Matrix3f(new Matrix3f(src.data));
    }

    public static Matrix4f copy(Matrix4f matrix4f) {
        return new Matrix4f(new net.minecraft.util.math.vector.Matrix4f(matrix4f.data));
    }

    public static void store(Matrix4f matrix4f, FloatBuffer buffer) {
        matrix4f.data.store(buffer);
    }

    public static Vector3f transformPosition(Matrix4f matrix4f, Vector3f src) {
        final Vector4f vector4f = new Vector4f(src.getX(), src.getY(), src.getZ(), 1.0F);
        vector4f.transform(matrix4f.data);
        return new Vector3f(vector4f.x(), vector4f.y(), vector4f.z());
    }

    public static Vector3f transformDirection(Matrix4f matrix4f, Vector3f src) {
        final Vector3f vector3f = copy(src);
        vector3f.data.transform(new Matrix3f(matrix4f.data));
        return vector3f;
    }

    public static Vector3f copy(Vector3f vector3f) {
        return new Vector3f(vector3f.getX(), vector3f.getY(), vector3f.getZ());
    }

    public static void mul(org.mtr.mapping.holder.Matrix3f src, org.mtr.mapping.holder.Matrix3f dest){
        src.data.mul(dest.data);
    }

    public static void mul(Matrix4f src, Matrix4f dest){
        src.data.multiply(dest.data);
    }

    public static void mul(org.mtr.mapping.holder.Vector4f src, Matrix4f dest){
        src.data.transform(dest.data);
    }

    public static void mul(Matrix4f src, float dest) {
        src.data.multiply(dest);
    }

    public static void mul(org.mtr.mapping.holder.Matrix3f src, float dest){
        src.data.mul(dest);
    }

    public static Matrix4f createScaleMatrix(float x, float y, float z) {
        return new Matrix4f(net.minecraft.util.math.vector.Matrix4f.createScaleMatrix(x,y,z));
    }
}