package org.mtr.mapping.registry;

import net.minecraftforge.registries.ForgeRegistries;
import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.EntityType;
import org.mtr.mapping.holder.Identifier;
import org.mtr.mapping.mapper.EntityExtension;
import org.mtr.mapping.tool.RegistryObject;

import java.util.function.Consumer;

public final class EntityTypeRegistryObject<T extends EntityExtension> extends RegistryObject<EntityType<T>> {

	private final net.minecraftforge.fmllegacy.RegistryObject<net.minecraft.world.entity.EntityType<T>> registryObject;

	EntityTypeRegistryObject(Identifier identifier) {
		registryObject = net.minecraftforge.fmllegacy.RegistryObject.of(identifier.data, ForgeRegistries.ENTITIES);
	}

	@MappedMethod
	@Override
	public EntityType<T> get() {
		return new EntityType<>(registryObject.get());
	}

	@MappedMethod
	@Override
	public boolean isPresent() {
		return registryObject.isPresent();
	}

	@MappedMethod
	@Override
	public void ifPresent(Consumer<EntityType<T>> consumer) {
		registryObject.ifPresent(data -> consumer.accept(new EntityType<>(data)));
	}
}
