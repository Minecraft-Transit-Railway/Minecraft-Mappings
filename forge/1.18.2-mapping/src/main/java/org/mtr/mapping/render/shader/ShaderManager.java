package org.mtr.mapping.render.shader;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import net.minecraft.client.renderer.ShaderInstance;
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

	private final Map<String, ShaderInstance> shaders = new HashMap<>();

	private static final VertexFormatElement MINECRAFT_ELEMENT_MATRIX = new VertexFormatElement(0, VertexFormatElement.Type.FLOAT, VertexFormatElement.Usage.GENERIC, 16);
	private static final VertexFormat MINECRAFT_VERTEX_FORMAT_BLOCK = new VertexFormat(ImmutableMap.<String, VertexFormatElement>builder()
			.put("Position", DefaultVertexFormat.ELEMENT_POSITION)
			.put("Color", DefaultVertexFormat.ELEMENT_COLOR)
			.put("UV0", DefaultVertexFormat.ELEMENT_UV0)
			.put("UV1", DefaultVertexFormat.ELEMENT_UV1)
			.put("UV2", DefaultVertexFormat.ELEMENT_UV2)
			.put("Normal", DefaultVertexFormat.ELEMENT_NORMAL)
			.put("ModelMat", MINECRAFT_ELEMENT_MATRIX)
			.put("Padding", DefaultVertexFormat.ELEMENT_PADDING)
			.build());

	public boolean isReady() {
		return !this.shaders.isEmpty();
	}

	public void reloadShaders() {
		shaders.values().forEach(ShaderInstance::close);
		shaders.clear();
		final PatchingResourceProvider patchingResourceProvider = new PatchingResourceProvider(MinecraftClient.getInstance().getResourceManager());
		loadShader(patchingResourceProvider, getShaderName(OptimizedModel.ShaderType.CUTOUT));
		loadShader(patchingResourceProvider, getShaderName(OptimizedModel.ShaderType.TRANSLUCENT));
		loadShader(patchingResourceProvider, getShaderName(OptimizedModel.ShaderType.CUTOUT_GLOWING));
	}

	private void loadShader(PatchingResourceProvider resourceManager, String shaderName) {
		try {
			shaders.put(shaderName, new ShaderInstance(resourceManager, shaderName, MINECRAFT_VERTEX_FORMAT_BLOCK));
		} catch (Exception e) {
			DummyClass.logException(e);
		}
	}

	public void setupShaderBatchState(MaterialProperties materialProperties) {
		final ShaderInstance shaderProgram;
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
			shaderProgram.setSampler("Sampler" + i, RenderSystem.getShaderTexture(i));
		}
		if (shaderProgram.MODEL_VIEW_MATRIX != null) {
			shaderProgram.MODEL_VIEW_MATRIX.set(Utilities.copy(new Matrix4f(RenderSystem.getModelViewMatrix())).data);
		}
		if (shaderProgram.PROJECTION_MATRIX != null) {
			shaderProgram.PROJECTION_MATRIX.set(RenderSystem.getProjectionMatrix());
		}
		if (shaderProgram.INVERSE_VIEW_ROTATION_MATRIX != null) { // 1.18+ only
			shaderProgram.INVERSE_VIEW_ROTATION_MATRIX.set(RenderSystem.getInverseViewRotationMatrix());
		}
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
		if (shaderProgram.FOG_SHAPE != null) { // 1.18+ only
			shaderProgram.FOG_SHAPE.set(RenderSystem.getShaderFogShape().getIndex());
		}
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
