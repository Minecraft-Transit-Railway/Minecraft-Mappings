package org.mtr.mapping.registry;

import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.EntityType;
import org.mtr.mapping.mapper.EntityExtension;
import org.mtr.mapping.tool.RegistryObject;

import java.util.function.Consumer;

public final class EntityTypeRegistryObject<T extends EntityExtension> extends RegistryObject<EntityType<T>> {

	private final EntityType<T> entityType;

	EntityTypeRegistryObject(EntityType<T> entityType) {
		this.entityType = entityType;
	}

	@MappedMethod
	@Override
	public EntityType<T> get() {
		return entityType;
	}

	@MappedMethod
	@Override
	public boolean isPresent() {
		return true;
	}

	@MappedMethod
	@Override
	public void ifPresent(Consumer<EntityType<T>> consumer) {
		consumer.accept(entityType);
	}
}
