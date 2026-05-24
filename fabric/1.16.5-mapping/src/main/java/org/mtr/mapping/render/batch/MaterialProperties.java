package org.mtr.mapping.render.batch;

import org.mtr.mapping.holder.Identifier;
import org.mtr.mapping.mapper.OptimizedModel;
import org.mtr.mapping.render.vertex.VertexAttributeState;

import javax.annotation.Nullable;
import java.util.Objects;

public final class MaterialProperties {

	private Identifier texture;
	public final OptimizedModel.ShaderType shaderType;
	public final VertexAttributeState vertexAttributeState;
	public final boolean translucent;
	public final boolean writeDepthBuf;
	public final boolean cutoutHack;

	public MaterialProperties(OptimizedModel.ShaderType shaderType, Identifier texture, @Nullable Integer color) {
		this.shaderType = shaderType;
		this.texture = texture;
		translucent = shaderType == OptimizedModel.ShaderType.TRANSLUCENT || shaderType == OptimizedModel.ShaderType.TRANSLUCENT_BRIGHT || shaderType == OptimizedModel.ShaderType.TRANSLUCENT_GLOWING;
		writeDepthBuf = shaderType != OptimizedModel.ShaderType.TRANSLUCENT_GLOWING;
		cutoutHack = shaderType == OptimizedModel.ShaderType.CUTOUT_GLOWING;
		vertexAttributeState = new VertexAttributeState(color, shaderType == OptimizedModel.ShaderType.CUTOUT_BRIGHT || shaderType == OptimizedModel.ShaderType.TRANSLUCENT_BRIGHT ? 15 << 4 | 15 << 20 : null);
	}

	public void setupCompositeState() {
	}

	public Identifier getTexture() {
		return texture;
	}

	public void setTexture(Identifier texture) {
		this.texture = texture;
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}
		if (!(object instanceof MaterialProperties)) {
			return false;
		}
		final MaterialProperties that = (MaterialProperties) object;
		return shaderType == that.shaderType && Objects.equals(texture, that.texture) && Objects.equals(vertexAttributeState, that.vertexAttributeState);
	}

	@Override
	public int hashCode() {
		return Objects.hash(texture, shaderType, vertexAttributeState);
	}
}
