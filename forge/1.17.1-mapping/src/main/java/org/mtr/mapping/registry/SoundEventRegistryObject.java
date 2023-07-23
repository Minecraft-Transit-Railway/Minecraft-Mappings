package org.mtr.mapping.registry;

import net.minecraftforge.registries.ForgeRegistries;
import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.Identifier;
import org.mtr.mapping.holder.SoundEvent;
import org.mtr.mapping.tool.RegistryObject;

import java.util.function.Consumer;

public final class SoundEventRegistryObject extends RegistryObject<SoundEvent> {

	private final net.minecraftforge.fmllegacy.RegistryObject<net.minecraft.sounds.SoundEvent> registryObject;

	SoundEventRegistryObject(Identifier identifier) {
		registryObject = net.minecraftforge.fmllegacy.RegistryObject.of(identifier.data, ForgeRegistries.SOUND_EVENTS);
	}

	@MappedMethod
	@Override
	public SoundEvent get() {
		return new SoundEvent(registryObject.get());
	}

	@MappedMethod
	@Override
	public boolean isPresent() {
		return registryObject.isPresent();
	}

	@MappedMethod
	@Override
	public void ifPresent(Consumer<SoundEvent> consumer) {
		registryObject.ifPresent(soundEvent -> consumer.accept(new SoundEvent(soundEvent)));
	}
}
