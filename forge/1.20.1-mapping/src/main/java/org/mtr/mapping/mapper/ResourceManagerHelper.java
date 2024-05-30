package org.mtr.mapping.mapper;

import net.minecraft.DetectedVersion;
import net.minecraft.client.Minecraft;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.Resource;
import org.apache.commons.io.IOUtils;
import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.Identifier;
import org.mtr.mapping.tool.DummyClass;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public final class ResourceManagerHelper extends DummyClass {

	@MappedMethod
	public static void readResource(Identifier identifier, Consumer<InputStream> consumer) {
		try {
			final Optional<Resource> optionalResource = Minecraft.getInstance().getResourceManager().getResource(identifier.data);
			optionalResource.ifPresent(resource -> readResource(resource, consumer));
		} catch (Exception e) {
			logException(e);
		}
	}

	@MappedMethod
	public static String readResource(Identifier identifier) {
		final String[] string = {""};
		readResource(identifier, inputStream -> {
			try {
				string[0] = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
			} catch (Exception e) {
				logException(e);
			}
		});
		return string[0];
	}

	@MappedMethod
	public static void readAllResources(Identifier identifier, Consumer<InputStream> consumer) {
		try {
			Minecraft.getInstance().getResourceManager().getResourceStack(identifier.data).forEach(resource -> readResource(resource, consumer));
		} catch (Exception e) {
			logException(e);
		}
	}

	@MappedMethod
	public static void readDirectory(String path, BiConsumer<Identifier, InputStream> consumer) {
		try {
			Minecraft.getInstance().getResourceManager()
					.listResourceStacks(path, identifier -> true)
					.forEach((identifier, resources) -> resources.forEach(resource -> readResource(resource, inputStream -> consumer.accept(new Identifier(identifier), inputStream))));
		} catch (Exception e) {
			logException(e);
		}
	}

	private static void readResource(Resource resource, Consumer<InputStream> consumer) {
		try (final InputStream inputStream = resource.open()) {
			consumer.accept(inputStream);
		} catch (Exception e) {
			logException(e);
		}
	}

	@MappedMethod
	public static int getResourcePackVersion() {
		return DetectedVersion.tryDetectVersion().getPackVersion(PackType.CLIENT_RESOURCES);
	}

	@MappedMethod
	public static int getDataPackVersion() {
		return DetectedVersion.tryDetectVersion().getPackVersion(PackType.SERVER_DATA);
	}
}
