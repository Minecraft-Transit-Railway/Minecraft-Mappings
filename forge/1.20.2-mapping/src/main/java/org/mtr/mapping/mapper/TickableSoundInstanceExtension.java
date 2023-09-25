package org.mtr.mapping.mapper;

import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.TickableSoundInstance;

public interface TickableSoundInstanceExtension extends TickableSoundInstance {

	@MappedMethod
	@Override
	boolean isRelative2();

	@MappedMethod
	boolean isRepeatable2();

	@Deprecated
	@Override
	default boolean isLooping2() {
		return isRepeatable2();
	}

	@MappedMethod
	boolean isDone2();

	@Deprecated
	@Override
	default boolean isStopped2() {
		return isDone2();
	}
}
