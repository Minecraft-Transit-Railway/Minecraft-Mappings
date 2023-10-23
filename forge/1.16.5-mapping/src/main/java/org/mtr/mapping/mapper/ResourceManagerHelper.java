package org.mtr.mapping.mapper;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.IResource;
import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.Identifier;
import org.mtr.mapping.tool.DummyClass;

import java.io.InputStream;
import java.util.function.Consumer;

public final class ResourceManagerHelper extends DummyClass {

	@MappedMethod
	public static void readResource(Identifier identifier, Consumer<InputStream> consumer) {
		try {
			readResource(Minecraft.getInstance().getResourceManager().getResource(identifier.data), consumer);
		} catch (Exception e) {
			logException(e);
		}
	}

	@MappedMethod
	public static void readAllResources(Identifier identifier, Consumer<InputStream> consumer) {
		try {
			Minecraft.getInstance().getResourceManager().getResources(identifier.data).forEach(resource -> readResource(resource, consumer));
		} catch (Exception e) {
			logException(e);
		}
	}

	private static void readResource(IResource resource, Consumer<InputStream> consumer) {
		try (final IResource newResource = resource) {
			try (final InputStream inputStream = newResource.getInputStream()) {
				consumer.accept(inputStream);
			} catch (Exception e) {
				logException(e);
			}
		} catch (Exception e) {
			logException(e);
		}
	}
}
