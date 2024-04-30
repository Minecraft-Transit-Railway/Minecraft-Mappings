package org.mtr.mapping.mapper;

import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.TickableSoundInstance;

public interface TickableSoundInstanceExtension extends TickableSoundInstance {

	@MappedMethod
	@Override
	boolean isRelative();

	@MappedMethod
	boolean isRepeatable();

	@Deprecated
	@Override
	default boolean isLooping() {
		return isRepeatable();
	}

	@MappedMethod
	boolean isDone();

	@Deprecated
	@Override
	default boolean isStopped() {
		return isDone();
	}
}
