package org.mtr.mapping.mapper;

import net.minecraft.client.Minecraft;
import net.minecraft.server.packs.resources.Resource;
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
			final Optional<Resource> optionalResource = Minecraft.getInstance().getResourceManager().getResource(identifier.data);
			optionalResource.ifPresent(resource -> {
				try (final InputStream inputStream = resource.open()) {
					consumer.accept(inputStream);
				} catch (Exception e) {
					logException(e);
				}
			});
		} catch (Exception e) {
			logException(e);
		}
	}
}
