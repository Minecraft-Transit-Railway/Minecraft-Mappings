package org.mtr.mapping.mapper;

import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.Identifier;
import org.mtr.mapping.holder.SoundEvent;

public final class SoundHelper {

	@MappedMethod
	public static SoundEvent createSoundEvent(Identifier identifier) {
		return SoundEvent.createVariableRangeEvent(identifier);
	}
}
