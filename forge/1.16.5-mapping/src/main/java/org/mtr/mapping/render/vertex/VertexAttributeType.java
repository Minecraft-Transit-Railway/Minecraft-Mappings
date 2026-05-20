package org.mtr.mapping.render.vertex;

public enum VertexAttributeType {
	POSITION(0, 12),
	COLOR(1, 4),
	UV_TEXTURE(2, 8),
	UV_OVERLAY(3, 4),
	UV_LIGHTMAP(4, 4),
	NORMAL(5, 3),
	MATRIX_MODEL(6, 64);

	public final int location;
	public final int byteSize;

	VertexAttributeType(int location, int byteSize) {
		this.location = location;
		this.byteSize = byteSize;
	}

	public void toggleAttributeArray(boolean enable) {
	}

	public void setupAttributePointer(int stride, int pointer) {
	}

	public void setAttributeDivisor(int divisor) {
	}
}
