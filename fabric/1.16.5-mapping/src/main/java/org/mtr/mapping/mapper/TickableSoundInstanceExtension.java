package org.mtr.mapping.mapper;

import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.TickableSoundInstance;

public interface TickableSoundInstanceExtension extends TickableSoundInstance {

	@MappedMethod
	boolean isRelative();

	@Deprecated
	@Override
	default boolean isLooping() {
		return isRelative();
	}

	@MappedMethod
	@Override
	boolean isRepeatable();

	@MappedMethod
	@Override
	boolean isDone();
}
