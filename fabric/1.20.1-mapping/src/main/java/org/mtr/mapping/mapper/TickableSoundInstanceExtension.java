package org.mtr.mapping.mapper;

import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.TickableSoundInstance;

public interface TickableSoundInstanceExtension extends TickableSoundInstance {

	@MappedMethod
	@Override
	boolean isRelative();

	@MappedMethod
	@Override
	boolean isRepeatable();

	@MappedMethod
	@Override
	boolean isDone();
}
