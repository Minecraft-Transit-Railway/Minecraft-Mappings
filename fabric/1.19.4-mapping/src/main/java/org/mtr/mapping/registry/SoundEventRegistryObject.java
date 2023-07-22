package org.mtr.mapping.registry;

import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.Identifier;
import org.mtr.mapping.holder.SoundEvent;
import org.mtr.mapping.tool.RegistryObject;

import java.util.function.Consumer;

public final class SoundEventRegistryObject extends RegistryObject<SoundEvent> {

	private final SoundEvent soundEvent;

	SoundEventRegistryObject(SoundEvent soundEvent) {
		this.soundEvent = soundEvent;
	}

	@MappedMethod
	@Override
	public SoundEvent get() {
		return soundEvent;
	}

	@MappedMethod
	@Override
	public boolean isPresent() {
		return true;
	}

	@MappedMethod
	@Override
	public void ifPresent(Consumer<SoundEvent> consumer) {
		consumer.accept(soundEvent);
	}

	@MappedMethod
	public static SoundEvent createSoundEvent(Identifier identifier) {
		return SoundEvent.of(identifier);
	}
}
