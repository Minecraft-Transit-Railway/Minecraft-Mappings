package org.mtr.mapping.render.vertex;

import org.mtr.mapping.holder.Matrix4f;

import javax.annotation.Nullable;
import java.util.Objects;

public final class VertexAttributeState {

	public final Integer color;
	public final Integer lightmapUV;
	public final Matrix4f matrix4f;

	public VertexAttributeState(int color, int lightmapUV, Matrix4f matrix4f) {
		this.color = color;
		this.lightmapUV = lightmapUV;
		this.matrix4f = matrix4f;
	}

	public VertexAttributeState(@Nullable Integer color, @Nullable Integer lightmapUV) {
		this.color = color;
		this.lightmapUV = lightmapUV;
		matrix4f = null;
	}

	public void apply() {
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}
		if (!(object instanceof VertexAttributeState)) {
			return false;
		}
		final VertexAttributeState vertexAttributeState = (VertexAttributeState) object;
		return Objects.equals(color, vertexAttributeState.color) && Objects.equals(lightmapUV, vertexAttributeState.lightmapUV) && Objects.equals(matrix4f, vertexAttributeState.matrix4f);
	}

	@Override
	public int hashCode() {
		return Objects.hash(color, lightmapUV, matrix4f);
	}
}
