package org.mtr.mapping.render.batch;

import org.mtr.mapping.render.object.VertexArray;
import org.mtr.mapping.render.shader.ShaderManager;
import org.mtr.mapping.render.vertex.VertexAttributeState;

import java.util.*;

public final class BatchManager {

	private final Map<MaterialProperties, Queue<RenderCall>> batches = new HashMap<>();

	public void queue(List<VertexArray> vertexArrays, VertexAttributeState vertexAttributeState) {
		vertexArrays.forEach(vertexArray -> queue(vertexArray, vertexAttributeState));
	}

	public void queue(VertexArray vertexArray, VertexAttributeState vertexAttributeState) {
		batches.computeIfAbsent(vertexArray.materialProperties, key -> new LinkedList<>()).add(new RenderCall(vertexArray, vertexAttributeState));
	}

	public void drawAll(ShaderManager shaderManager) {
		batches.forEach((materialProperties, renderCalls) -> {
			if (!materialProperties.translucent && !materialProperties.cutoutHack) {
				drawBatch(shaderManager, materialProperties, renderCalls);
			}
		});
		batches.forEach((materialProperties, renderCalls) -> {
			if (materialProperties.cutoutHack) {
				drawBatch(shaderManager, materialProperties, renderCalls);
			}
		});
		batches.forEach((materialProperties, renderCalls) -> {
			if (materialProperties.translucent) {
				drawBatch(shaderManager, materialProperties, renderCalls);
			}
		});
		batches.clear();
	}

	private void drawBatch(ShaderManager shaderManager, MaterialProperties materialProperties, Queue<RenderCall> renderCalls) {
		shaderManager.setupShaderBatchState(materialProperties);
		while (!renderCalls.isEmpty()) {
			renderCalls.poll().draw();
		}
		shaderManager.cleanupShaderBatchState();
	}

	private static class RenderCall {

		public final VertexArray vertexArray;
		public final VertexAttributeState vertexAttributeState;

		public RenderCall(VertexArray vertexArray, VertexAttributeState vertexAttributeState) {
			this.vertexArray = vertexArray;
			this.vertexAttributeState = vertexAttributeState;
		}

		public void draw() {
			vertexArray.bind();
			vertexAttributeState.apply();
			vertexArray.materialProperties.vertexAttributeState.apply();
			vertexArray.draw();
		}
	}
}
