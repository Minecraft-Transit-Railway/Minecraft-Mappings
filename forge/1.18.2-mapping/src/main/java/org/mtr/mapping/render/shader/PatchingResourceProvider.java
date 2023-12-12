package org.mtr.mapping.render.shader;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceProvider;
import net.minecraft.server.packs.resources.SimpleResource;
import org.apache.commons.io.IOUtils;
import org.mtr.mapping.holder.ResourceManager;
import org.mtr.mapping.render.tool.GlStateTracker;
import org.mtr.mapping.tool.DummyClass;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public final class PatchingResourceProvider implements ResourceProvider {

	private final ResourceProvider resourceFactory;

	public PatchingResourceProvider(ResourceManager resourceManager) {
		resourceFactory = resourceManager.data;
	}

	@Override
	public Resource getResource(ResourceLocation identifier) throws IOException {
		final ResourceLocation newIdentifier = identifier.getPath().contains("_modelmat") ? new ResourceLocation(identifier.getNamespace(), identifier.getPath().replace("_modelmat", "")) : identifier;
		final Resource resource = resourceFactory.getResource(newIdentifier);

		if (resource == null) {
			throw new IOException();
		} else {
			try (final InputStream inputStream = resource.getInputStream()) {
				final String returningContent;

				if (newIdentifier.getPath().endsWith(".json")) {
					final JsonObject dataObject = JsonParser.parseString(IOUtils.toString(inputStream, StandardCharsets.UTF_8)).getAsJsonObject();
					dataObject.addProperty("vertex", dataObject.get("vertex").getAsString() + "_modelmat");
					final JsonArray attributeArray = dataObject.get("attributes").getAsJsonArray();
					for (int i = 0; i < 6 - attributeArray.size(); i++) {
						attributeArray.add("Dummy" + i);
					}
					attributeArray.add("ModelMat");
					returningContent = dataObject.toString();
				} else if (newIdentifier.getPath().endsWith(".vsh")) {
					returningContent = patchVertexShaderSource(IOUtils.toString(inputStream, StandardCharsets.UTF_8));
				} else {
					return resource;
				}

				return new SimpleResource(resource.getSourceName(), identifier, inputStream, null);
			} catch (Exception e) {
				DummyClass.logException(e);
				throw e;
			}
		}
	}

	private static String patchVertexShaderSource(String sourceContent) {
		final String[] contentParts = sourceContent.split("void main");
		contentParts[0] = contentParts[0].replace("uniform mat4 ModelViewMat;", "uniform mat4 ModelViewMat;\nin mat4 ModelMat;");
		if (GlStateTracker.isGl4ES()) {
			contentParts[0] = contentParts[0].replace("ivec2", "vec2");
		}
		contentParts[1] = contentParts[1]
				.replaceAll("\\bPosition\\b", "(MODELVIEWMAT * ModelMat * vec4(Position, 1.0)).xyz")
				.replaceAll("\\bNormal\\b", "normalize(mat3(MODELVIEWMAT * ModelMat) * Normal)")
				.replace("ModelViewMat", "mat4(1.0)")
				.replace("MODELVIEWMAT", "ModelViewMat");
		return contentParts[0] + "void main" + contentParts[1];
	}
}
