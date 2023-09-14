package org.mtr.mapping.mapper;

import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.Resource;
import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.Identifier;
import org.mtr.mapping.tool.DummyClass;

import java.io.InputStream;
import java.util.function.Consumer;

public final class ResourceManagerHelper extends DummyClass {

	@MappedMethod
	public static void readResource(Identifier identifier, Consumer<InputStream> consumer) {
		try (final Resource resource = MinecraftClient.getInstance().getResourceManager().getResource(identifier.data)) {
			try (final InputStream inputStream = resource.getInputStream()) {
				consumer.accept(inputStream);
			} catch (Exception e) {
				logException(e);
			}
		} catch (Exception e) {
			logException(e);
		}
	}
}
