package org.mtr.mapping.mapper;

import org.junit.jupiter.api.Test;
import org.mtr.mapping.holder.Identifier;
import org.mtr.mapping.render.model.RawMesh;
import org.mtr.mapping.render.vertex.Vertex;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class OptimizedModelObjModelTest {

	private static final String SIMPLE_OBJ = String.join("\n",
			"v 0 0 0",
			"v 1 0 0",
			"v 0 1 0",
			"vt 0.25 0.75",
			"vt 0.5 0.25",
			"vt 0.75 0.5",
			"vn 0 0 1",
			"f 1/1/1 2/2/1 3/3/1"
	);

	@Test
	public void loadRawModelWithoutAtlasIndexAppliesObjModelTransforms() {
		final Map<String, List<RawMesh>> rawModels = OptimizedModel.ObjModel.loadRawModel(SIMPLE_OBJ, mtlString -> "", textureString -> new Identifier("minecraft:test"), null, false, false);
		final List<RawMesh> rawMeshes = rawModels.get("");

		assertFalse(rawModels.isEmpty());
		assertEquals(1, rawMeshes.size());

		final Vertex firstVertex = rawMeshes.get(0).vertices.get(0);
		final Vertex thirdVertex = rawMeshes.get(0).vertices.get(2);
		assertEquals(0, firstVertex.position.getX(), 0.0001F);
		assertEquals(0, firstVertex.position.getY(), 0.0001F);
		assertEquals(0, firstVertex.position.getZ(), 0.0001F);
		assertEquals(0, thirdVertex.position.getX(), 0.0001F);
		assertEquals(-1, thirdVertex.position.getY(), 0.0001F);
		assertEquals(0, thirdVertex.position.getZ(), 0.0001F);
		assertEquals(0.75F, firstVertex.v, 0.0001F);
	}

	@Test
	public void loadRawModelRespectsFlipTextureV() {
		final Map<String, List<RawMesh>> rawModels = OptimizedModel.ObjModel.loadRawModel(SIMPLE_OBJ, mtlString -> "", textureString -> new Identifier("minecraft:test"), null, false, true);
		final Vertex firstVertex = rawModels.get("").get(0).vertices.get(0);
		final Vertex secondVertex = rawModels.get("").get(0).vertices.get(1);

		assertEquals(0.25F, firstVertex.v, 0.0001F);
		assertEquals(0.75F, secondVertex.v, 0.0001F);
	}
}
