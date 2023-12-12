package org.mtr.mapping.render.shader;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.Shader;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormatElement;
import net.minecraft.client.render.VertexFormats;
import org.mtr.mapping.holder.Matrix4f;
import org.mtr.mapping.holder.MinecraftClient;
import org.mtr.mapping.holder.Window;
import org.mtr.mapping.mapper.OptimizedModel;
import org.mtr.mapping.render.batch.MaterialProperties;
import org.mtr.mapping.render.tool.Utilities;
import org.mtr.mapping.tool.DummyClass;

import java.util.HashMap;
import java.util.Map;

public final class ShaderManager {

	private final Map<String, Shader> shaders = new HashMap<>();

	private static final VertexFormatElement MINECRAFT_ELEMENT_MATRIX = new VertexFormatElement(0, VertexFormatElement.DataType.FLOAT, VertexFormatElement.Type.GENERIC, 16);
	private static final VertexFormat MINECRAFT_VERTEX_FORMAT_BLOCK = new VertexFormat(ImmutableMap.<String, VertexFormatElement>builder()
			.put("Position", VertexFormats.POSITION_ELEMENT)
			.put("Color", VertexFormats.COLOR_ELEMENT)
			.put("UV0", VertexFormats.TEXTURE_0_ELEMENT)
			.put("UV1", VertexFormats.OVERLAY_ELEMENT)
			.put("UV2", VertexFormats.LIGHT_ELEMENT)
			.put("Normal", VertexFormats.NORMAL_ELEMENT)
			.put("ModelMat", MINECRAFT_ELEMENT_MATRIX)
			.put("Padding", VertexFormats.PADDING_ELEMENT)
			.build());

	public boolean isReady() {
		return !this.shaders.isEmpty();
	}

	public void reloadShaders() {
		shaders.values().forEach(Shader::close);
		shaders.clear();
		final PatchingResourceProvider patchingResourceProvider = new PatchingResourceProvider(MinecraftClient.getInstance().getResourceManager());
		loadShader(patchingResourceProvider, getShaderName(OptimizedModel.ShaderType.CUTOUT));
		loadShader(patchingResourceProvider, getShaderName(OptimizedModel.ShaderType.TRANSLUCENT));
		loadShader(patchingResourceProvider, getShaderName(OptimizedModel.ShaderType.CUTOUT_GLOWING));
	}

	private void loadShader(PatchingResourceProvider resourceManager, String shaderName) {
		try {
			shaders.put(shaderName, new Shader(resourceManager, shaderName, MINECRAFT_VERTEX_FORMAT_BLOCK));
		} catch (Exception e) {
			DummyClass.logException(e);
		}
	}

	public void setupShaderBatchState(MaterialProperties materialProperties) {
		final Shader shaderProgram;
		if (Utilities.canUseCustomShader()) {
			shaderProgram = shaders.get(getShaderName(materialProperties.shaderType));
			materialProperties.setupCompositeState();
		} else {
			materialProperties.getBlazeRenderType().startDrawing();
			shaderProgram = RenderSystem.getShader();
		}

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
		if (shaderProgram.viewRotationMat != null) { // 1.18+ only
			shaderProgram.viewRotationMat.method_39978(RenderSystem.getInverseViewRotationMatrix());
		}
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
		if (shaderProgram.fogShape != null) { // 1.18+ only
			shaderProgram.fogShape.set(RenderSystem.getShaderFogShape().getId());
		}
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
			final Shader shaderProgram = RenderSystem.getShader();
			if (shaderProgram != null && shaderProgram.modelViewMat != null) {
				// ModelViewMatrix might have got set in VertexAttributeState, reset it
				shaderProgram.modelViewMat.set(RenderSystem.getModelViewMatrix());
				shaderProgram.bind();
			}
		}
	}

	private static String getShaderName(OptimizedModel.ShaderType shaderType) {
		switch (shaderType) {
			default:
				return "rendertype_entity_cutout";
			case TRANSLUCENT:
			case TRANSLUCENT_BRIGHT:
				return "rendertype_entity_translucent_cull";
			case CUTOUT_GLOWING:
			case TRANSLUCENT_GLOWING:
				return "rendertype_beacon_beam";
		}
	}
}
