package org.mtr.mapping.render.obj;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.mtr.mapping.holder.Identifier;
import org.mtr.mapping.mapper.ResourceManagerHelper;
import org.mtr.mapping.render.model.RawMesh;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class AtlasManager {

	private final Map<Identifier, AtlasSprite> sprites = new HashMap<>();
	private final Set<Identifier> noAtlasList = new HashSet<>();

	public void load(Identifier atlasConfiguration) {
		final JsonObject atlasConfigurationObject = JsonParser.parseString(ResourceManagerHelper.readResource(atlasConfiguration)).getAsJsonObject();
		final String basePath = atlasConfigurationObject.get("basePath").getAsString();
		atlasConfigurationObject.get("sheets").getAsJsonArray().forEach(sheetObject -> {
			final Identifier sheetConfiguration = ObjModelLoader.resolveRelativePath(atlasConfiguration, sheetObject.getAsString(), ".json");
			final Identifier sheetTexture = ObjModelLoader.resolveRelativePath(atlasConfiguration, sheetObject.getAsString(), ".png");
			final JsonObject sheetConfigurationObject = JsonParser.parseString(ResourceManagerHelper.readResource(sheetConfiguration)).getAsJsonObject();
			final int sheetWidth = sheetConfigurationObject.get("meta").getAsJsonObject().get("size").getAsJsonObject().get("w").getAsInt();
			final int sheetHeight = sheetConfigurationObject.get("meta").getAsJsonObject().get("size").getAsJsonObject().get("h").getAsInt();
			sheetConfigurationObject.get("frames").getAsJsonObject().entrySet().forEach(entry -> {
				final Identifier texture = ObjModelLoader.resolveRelativePath(sheetConfiguration, basePath + entry.getKey(), ".png");
				final JsonObject spriteObject = entry.getValue().getAsJsonObject();
				sprites.put(texture, new AtlasSprite(
						sheetTexture, sheetWidth, sheetHeight,
						spriteObject.get("frame").getAsJsonObject().get("x").getAsInt(), spriteObject.get("frame").getAsJsonObject().get("y").getAsInt(),
						spriteObject.get("frame").getAsJsonObject().get("w").getAsInt(), spriteObject.get("frame").getAsJsonObject().get("h").getAsInt(),
						spriteObject.get("spriteSourceSize").getAsJsonObject().get("x").getAsInt(), spriteObject.get("spriteSourceSize").getAsJsonObject().get("y").getAsInt(),
						spriteObject.get("spriteSourceSize").getAsJsonObject().get("w").getAsInt(), spriteObject.get("spriteSourceSize").getAsJsonObject().get("h").getAsInt(),
						spriteObject.get("sourceSize").getAsJsonObject().get("w").getAsInt(), spriteObject.get("sourceSize").getAsJsonObject().get("h").getAsInt(),
						spriteObject.get("rotated").getAsBoolean()
				));
			});
		});
		atlasConfigurationObject.get("noAtlas").getAsJsonArray().forEach(noAtlasObject -> noAtlasList.add(ObjModelLoader.resolveRelativePath(atlasConfiguration, basePath + noAtlasObject.getAsString(), ".png")));
	}

	public void applyToMesh(RawMesh mesh) {
		if (noAtlasList.contains(mesh.materialProperties.getTexture())) {
			return;
		}
		final AtlasSprite sprite = sprites.get(mesh.materialProperties.getTexture());
		if (sprite != null) {
			sprite.applyToMesh(mesh);
		}
	}

	public void clear() {
		sprites.clear();
	}
}
