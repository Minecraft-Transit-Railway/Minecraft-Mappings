package org.mtr.mapping.render.shader;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceFactory;
import net.minecraft.resource.ResourceImpl;
import net.minecraft.util.Identifier;
import org.apache.commons.io.IOUtils;
import org.mtr.mapping.holder.ResourceManager;
import org.mtr.mapping.render.tool.GlStateTracker;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public final class PatchingResourceProvider implements ResourceFactory {

	private final ResourceFactory resourceFactory;

	public PatchingResourceProvider(ResourceManager resourceManager) {
		resourceFactory = resourceManager.data;
	}

	@Override
	public Resource getResource(Identifier identifier) throws IOException {
		final Identifier newIdentifier = identifier.getPath().contains("_modelmat") ? new Identifier(identifier.getNamespace(), identifier.getPath().replace("_modelmat", "")) : identifier;
		final Resource resource = resourceFactory.getResource(newIdentifier);

		if (resource == null) {
			throw new IOException();
		} else {
			final InputStream inputStream = resource.getInputStream();
			final String returningContent;

			if (newIdentifier.getPath().endsWith(".json")) {
				final JsonObject dataObject = new JsonParser().parse(IOUtils.toString(inputStream, StandardCharsets.UTF_8)).getAsJsonObject();
				inputStream.close();
				dataObject.addProperty("vertex", dataObject.get("vertex").getAsString() + "_modelmat");
				final JsonArray attributeArray = dataObject.get("attributes").getAsJsonArray();
				for (int i = 0; i < 6 - attributeArray.size(); i++) {
					attributeArray.add("Dummy" + i);
				}
				attributeArray.add("ModelMat");
				returningContent = dataObject.toString();
			} else if (newIdentifier.getPath().endsWith(".vsh")) {
				returningContent = patchVertexShaderSource(IOUtils.toString(inputStream, StandardCharsets.UTF_8));
				inputStream.close();
			} else {
				return resource;
			}

			return new ResourceImpl(resource.getResourcePackName(), identifier, new ByteArrayInputStream(returningContent.getBytes(StandardCharsets.UTF_8)), null);
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
