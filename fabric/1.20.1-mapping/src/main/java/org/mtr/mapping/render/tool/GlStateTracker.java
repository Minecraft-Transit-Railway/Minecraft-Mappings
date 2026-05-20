package org.mtr.mapping.render.tool;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gl.ShaderProgram;
import org.lwjgl.opengl.GL33;
import org.mtr.mapping.tool.DummyClass;

import java.util.Locale;

public final class GlStateTracker {

	private static boolean supportVertexAttributeDivisor;
	private static boolean isGl4ES = false;

	private static final int SHADER_TEXTURE_COUNT = 8;

	private static int vertArrayBinding;
	private static int arrayBufBinding;
	private static int elementBufBinding;
	private static int activeTexture;
	private static final int[] shaderTextures = new int[SHADER_TEXTURE_COUNT];
	private static boolean blendEnabled;
	private static boolean depthTestEnabled;
	private static boolean cullEnabled;
	private static boolean depthMask;
	private static int depthFunc;
	private static int blendSrcRgb;
	private static int blendDstRgb;
	private static int blendSrcAlpha;
	private static int blendDstAlpha;
	private static int blendEquationRgb;
	private static int blendEquationAlpha;
	private static ShaderProgram currentShaderProgram;
	private static boolean isStateProtected;

	public static void capture() {
		final int contextVersion = GL33.glGetInteger(GL33.GL_MAJOR_VERSION) * 10 + GL33.glGetInteger(GL33.GL_MINOR_VERSION);
		supportVertexAttributeDivisor = contextVersion >= 33;
		final String glVersion = GL33.glGetString(GL33.GL_VERSION);
		isGl4ES = glVersion != null && glVersion.toLowerCase(Locale.ENGLISH).contains("gl4es");

		if (isStateProtected) {
			return;
		}

		vertArrayBinding = GL33.glGetInteger(GL33.GL_VERTEX_ARRAY_BINDING);
		arrayBufBinding = GL33.glGetInteger(GL33.GL_ARRAY_BUFFER_BINDING);
		elementBufBinding = GL33.glGetInteger(GL33.GL_ELEMENT_ARRAY_BUFFER_BINDING);
		activeTexture = GL33.glGetInteger(GL33.GL_ACTIVE_TEXTURE);
		for (int i = 0; i < SHADER_TEXTURE_COUNT; i++) {
			shaderTextures[i] = RenderSystem.getShaderTexture(i);
		}
		blendEnabled = GL33.glIsEnabled(GL33.GL_BLEND);
		depthTestEnabled = GL33.glIsEnabled(GL33.GL_DEPTH_TEST);
		cullEnabled = GL33.glIsEnabled(GL33.GL_CULL_FACE);
		depthMask = GL33.glGetBoolean(GL33.GL_DEPTH_WRITEMASK);
		depthFunc = GL33.glGetInteger(GL33.GL_DEPTH_FUNC);
		blendSrcRgb = GL33.glGetInteger(GL33.GL_BLEND_SRC_RGB);
		blendDstRgb = GL33.glGetInteger(GL33.GL_BLEND_DST_RGB);
		blendSrcAlpha = GL33.glGetInteger(GL33.GL_BLEND_SRC_ALPHA);
		blendDstAlpha = GL33.glGetInteger(GL33.GL_BLEND_DST_ALPHA);
		blendEquationRgb = GL33.glGetInteger(GL33.GL_BLEND_EQUATION_RGB);
		blendEquationAlpha = GL33.glGetInteger(GL33.GL_BLEND_EQUATION_ALPHA);
		currentShaderProgram = RenderSystem.getShader();
		isStateProtected = true;
	}

	public static void restore() {
		if (!isStateProtected) {
			final IllegalStateException e = new IllegalStateException("GlStateTracker: Not captured");
			DummyClass.logException(e);
			throw e;
		}
		GL33.glBindVertexArray(vertArrayBinding);
		GL33.glBindBuffer(GL33.GL_ARRAY_BUFFER, arrayBufBinding);
		GL33.glBindBuffer(GL33.GL_ELEMENT_ARRAY_BUFFER, elementBufBinding);

		RenderSystem.setShader(() -> currentShaderProgram);
		for (int i = 0; i < SHADER_TEXTURE_COUNT; i++) {
			RenderSystem.setShaderTexture(i, shaderTextures[i]);
		}
		GL33.glActiveTexture(activeTexture);

		setBlend(blendEnabled);
		setDepthTest(depthTestEnabled);
		setCull(cullEnabled);
		RenderSystem.depthMask(depthMask);
		RenderSystem.depthFunc(depthFunc);
		GL33.glBlendFuncSeparate(blendSrcRgb, blendDstRgb, blendSrcAlpha, blendDstAlpha);
		GL33.glBlendEquationSeparate(blendEquationRgb, blendEquationAlpha);

		isStateProtected = false;
	}

	private static void setBlend(boolean enabled) {
		if (enabled) {
			RenderSystem.enableBlend();
		} else {
			RenderSystem.disableBlend();
		}
	}

	private static void setDepthTest(boolean enabled) {
		if (enabled) {
			RenderSystem.enableDepthTest();
		} else {
			RenderSystem.disableDepthTest();
		}
	}

	private static void setCull(boolean enabled) {
		if (enabled) {
			RenderSystem.enableCull();
		} else {
			RenderSystem.disableCull();
		}
	}

	public static void assertProtected() {
		if (!isStateProtected) {
			final IllegalStateException e = new IllegalStateException("GlStateTracker: Not protected");
			DummyClass.logException(e);
			throw e;
		}
	}

	public static boolean supportVertexAttributeDivisor() {
		return supportVertexAttributeDivisor;
	}

	public static boolean isGl4ES() {
		return isGl4ES;
	}
}
