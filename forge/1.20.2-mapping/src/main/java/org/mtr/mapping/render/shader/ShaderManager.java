package org.mtr.mapping.render.shader;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.renderer.ShaderInstance;
import org.mtr.mapping.holder.Matrix4f;
import org.mtr.mapping.holder.MinecraftClient;
import org.mtr.mapping.holder.Window;
import org.mtr.mapping.render.batch.MaterialProperties;
import org.mtr.mapping.render.tool.Utilities;

import java.util.HashMap;
import java.util.Map;

public final class ShaderManager {

	private final Map<String, ShaderInstance> shaders = new HashMap<>();

	public boolean isReady() {
		return !this.shaders.isEmpty();
	}

	public void reloadShaders() {
		shaders.values().forEach(ShaderInstance::close);
		shaders.clear();
		// TODO
	}

	public void setupShaderBatchState(MaterialProperties materialProperties) {
		materialProperties.getBlazeRenderType().startDrawing();
		final ShaderInstance shaderProgram = RenderSystem.getShader();

		if (shaderProgram == null) {
			throw new IllegalArgumentException("Cannot get shader: " + materialProperties.shaderType);
		}

		for (int i = 0; i < 8; i++) {
			shaderProgram.setSampler("Sampler" + i, RenderSystem.getShaderTexture(i));
		}
		if (shaderProgram.MODEL_VIEW_MATRIX != null) {
			shaderProgram.MODEL_VIEW_MATRIX.set(Utilities.copy(new Matrix4f(RenderSystem.getModelViewMatrix())).data);
		}
		if (shaderProgram.PROJECTION_MATRIX != null) {
			shaderProgram.PROJECTION_MATRIX.set(RenderSystem.getProjectionMatrix());
		}
//#if MC_VERSION >= "11800"
		if (shaderProgram.INVERSE_VIEW_ROTATION_MATRIX != null) {
			shaderProgram.INVERSE_VIEW_ROTATION_MATRIX.set(RenderSystem.getInverseViewRotationMatrix());
		}
//#endif
		if (shaderProgram.COLOR_MODULATOR != null) {
			shaderProgram.COLOR_MODULATOR.set(RenderSystem.getShaderColor());
		}
		if (shaderProgram.FOG_START != null) {
			shaderProgram.FOG_START.set(RenderSystem.getShaderFogStart());
		}
		if (shaderProgram.FOG_END != null) {
			shaderProgram.FOG_END.set(RenderSystem.getShaderFogEnd());
		}
		if (shaderProgram.FOG_COLOR != null) {
			shaderProgram.FOG_COLOR.set(RenderSystem.getShaderFogColor());
		}
//#if MC_VERSION >= "11800"
		if (shaderProgram.FOG_SHAPE != null) {
			shaderProgram.FOG_SHAPE.set(RenderSystem.getShaderFogShape().getIndex());
		}
//#endif
		if (shaderProgram.TEXTURE_MATRIX != null) {
			shaderProgram.TEXTURE_MATRIX.set(RenderSystem.getTextureMatrix());
		}
		if (shaderProgram.GAME_TIME != null) {
			shaderProgram.GAME_TIME.set(RenderSystem.getShaderGameTime());
		}
		if (shaderProgram.SCREEN_SIZE != null) {
			final Window window = MinecraftClient.getInstance().getWindow();
			shaderProgram.SCREEN_SIZE.set(window.getWidth(), window.getHeight());
		}

		RenderSystem.setupShaderLights(shaderProgram);
		shaderProgram.apply();
	}

	public void cleanupShaderBatchState() {
		if (!Utilities.canUseCustomShader()) {
			final ShaderInstance shaderProgram = RenderSystem.getShader();
			if (shaderProgram != null && shaderProgram.MODEL_VIEW_MATRIX != null) {
				// ModelViewMatrix might have got set in VertexAttributeState, reset it
				shaderProgram.MODEL_VIEW_MATRIX.set(RenderSystem.getModelViewMatrix());
				shaderProgram.apply();
			}
		}
	}
}
