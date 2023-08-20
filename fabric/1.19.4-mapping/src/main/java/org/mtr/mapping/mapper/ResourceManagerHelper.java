package org.mtr.mapping.mapper;

import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.Resource;
import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.Identifier;
import org.mtr.mapping.tool.DummyClass;

import java.io.InputStream;
import java.util.Optional;
import java.util.function.Consumer;

public final class ResourceManagerHelper extends DummyClass {

	@MappedMethod
	public static void readResource(Identifier identifier, Consumer<InputStream> consumer) {
		try {
			final Optional<Resource> optionalResource = MinecraftClient.getInstance().getResourceManager().getResource(identifier.data);
			optionalResource.ifPresent(resource -> {
				try (final InputStream inputStream = resource.getInputStream()) {
					consumer.accept(inputStream);
				} catch (Exception e) {
					e.printStackTrace();
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
