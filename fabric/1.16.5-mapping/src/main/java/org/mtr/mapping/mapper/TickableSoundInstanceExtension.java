package org.mtr.mapping.mapper;

import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.TickableSoundInstance;

public interface TickableSoundInstanceExtension extends TickableSoundInstance {

	@MappedMethod
	boolean isRelative2();

	@Deprecated
	@Override
	default boolean isLooping2() {
		return isRelative2();
	}

	@MappedMethod
	@Override
	boolean isRepeatable2();

	@MappedMethod
	@Override
	boolean isDone2();
}
