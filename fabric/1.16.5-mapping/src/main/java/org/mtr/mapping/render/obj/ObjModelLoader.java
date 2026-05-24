package org.mtr.mapping.render.obj;

import org.mtr.mapping.holder.Identifier;
import org.mtr.mapping.render.model.RawMesh;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public final class ObjModelLoader {
	public static Map<String, List<RawMesh>> loadModel(String objString, Function<String, String> mtlResolver, Function<String, Identifier> textureResolver, AtlasManager atlasManager, boolean splitModel) {
		return Collections.emptyMap();
	}
}
