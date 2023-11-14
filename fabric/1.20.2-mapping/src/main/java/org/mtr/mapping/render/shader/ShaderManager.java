package org.mtr.mapping.render.shader;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gl.ShaderProgram;
import org.mtr.mapping.holder.Matrix4f;
import org.mtr.mapping.holder.MinecraftClient;
import org.mtr.mapping.holder.Window;
import org.mtr.mapping.render.batch.MaterialProperties;
import org.mtr.mapping.render.tool.Utilities;

import java.util.HashMap;
import java.util.Map;

public final class ShaderManager {

	private final Map<String, ShaderProgram> shaders = new HashMap<>();

	public boolean isReady() {
		return !this.shaders.isEmpty();
	}

	public void reloadShaders() {
		shaders.values().forEach(ShaderProgram::close);
		shaders.clear();
		// TODO
	}

	public void setupShaderBatchState(MaterialProperties materialProperties) {
		materialProperties.getBlazeRenderType().startDrawing();
		final ShaderProgram shaderProgram = RenderSystem.getShader();

		if (shaderProgram == null) {
			throw new IllegalArgumentException("Cannot get shader: " + materialProperties.shaderType);
		}

		for (int i = 0; i < 8; i++) {
			shaderProgram.addSampler("Sampler" + i, RenderSystem.getShaderTexture(i));
		}
		if (shaderProgram.modelViewMat != null) {
			shaderProgram.modelViewMat.set(Utilities.copy(new Matrix4f(RenderSystem.getModelViewMatrix())).data);
		}
		if (shaderProgram.projectionMat != null) {
			shaderProgram.projectionMat.set(RenderSystem.getProjectionMatrix());
		}
//#if MC_VERSION >= "11800"
		if (shaderProgram.viewRotationMat != null) {
			shaderProgram.viewRotationMat.set(RenderSystem.getInverseViewRotationMatrix());
		}
//#endif
		if (shaderProgram.colorModulator != null) {
			shaderProgram.colorModulator.set(RenderSystem.getShaderColor());
		}
		if (shaderProgram.fogStart != null) {
			shaderProgram.fogStart.set(RenderSystem.getShaderFogStart());
		}
		if (shaderProgram.fogEnd != null) {
			shaderProgram.fogEnd.set(RenderSystem.getShaderFogEnd());
		}
		if (shaderProgram.fogColor != null) {
			shaderProgram.fogColor.set(RenderSystem.getShaderFogColor());
		}
//#if MC_VERSION >= "11800"
		if (shaderProgram.fogShape != null) {
			shaderProgram.fogShape.set(RenderSystem.getShaderFogShape().getId());
		}
//#endif
		if (shaderProgram.textureMat != null) {
			shaderProgram.textureMat.set(RenderSystem.getTextureMatrix());
		}
		if (shaderProgram.gameTime != null) {
			shaderProgram.gameTime.set(RenderSystem.getShaderGameTime());
		}
		if (shaderProgram.screenSize != null) {
			final Window window = MinecraftClient.getInstance().getWindow();
			shaderProgram.screenSize.set(window.getWidth(), window.getHeight());
		}

		RenderSystem.setupShaderLights(shaderProgram);
		shaderProgram.bind();
	}

	public void cleanupShaderBatchState() {
		if (!Utilities.canUseCustomShader()) {
			final ShaderProgram shaderProgram = RenderSystem.getShader();
			if (shaderProgram != null && shaderProgram.modelViewMat != null) {
				// ModelViewMatrix might have got set in VertexAttributeState, reset it
				shaderProgram.modelViewMat.set(RenderSystem.getModelViewMatrix());
				shaderProgram.bind();
			}
		}
	}
}
