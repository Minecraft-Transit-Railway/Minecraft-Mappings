package org.mtr.mapping.tool;

import org.mtr.mapping.annotation.MappedMethod;

import java.util.function.Consumer;

public abstract class RegistryObject<T> {

	@MappedMethod
	public abstract T get();

	@MappedMethod
	public abstract boolean isPresent();

	@MappedMethod
	public abstract void ifPresent(Consumer<T> consumer);
}
