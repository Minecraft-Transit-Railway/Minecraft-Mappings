package org.mtr.mapping.mapper;

import net.minecraft.client.Minecraft;
import net.minecraft.util.RandomSource;
import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.*;

import java.util.function.Consumer;

public abstract class AbstractSoundInstanceExtension extends AbstractSoundInstanceAbstractMapping {

	@MappedMethod
	protected void setIsRelativeMapped(boolean isRelative) {
		relative = isRelative;
	}

	@MappedMethod
	protected void setIsRepeatableMapped(boolean isRepeatable) {
		looping = isRepeatable;
	}

	@MappedMethod
	@Override
	public boolean isRelative() {
		return super.isRelative();
	}

	@MappedMethod
	public boolean isRepeatable() {
		return super.isLooping();
	}

	@MappedMethod
	protected AbstractSoundInstanceExtension(SoundEvent sound, SoundCategory category) {
		super(sound, category, new Random(RandomSource.create()));
	}

	@MappedMethod
	protected AbstractSoundInstanceExtension(Identifier soundId, SoundCategory category) {
		super(soundId, category, new Random(RandomSource.create()));
	}

	@MappedMethod
	public static void iterateSoundIds(Consumer<Identifier> consumer) {
		Minecraft.getInstance().getSoundManager().getAvailableSounds().forEach(identifier -> consumer.accept(new Identifier(identifier)));
	}
}
