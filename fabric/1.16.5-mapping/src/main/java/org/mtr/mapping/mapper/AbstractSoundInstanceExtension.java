package org.mtr.mapping.mapper;

import net.minecraft.client.MinecraftClient;
import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.AbstractSoundInstanceAbstractMapping;
import org.mtr.mapping.holder.Identifier;
import org.mtr.mapping.holder.SoundCategory;
import org.mtr.mapping.holder.SoundEvent;

import java.util.function.Consumer;

public abstract class AbstractSoundInstanceExtension extends AbstractSoundInstanceAbstractMapping {

	@MappedMethod
	protected void setIsRelativeMapped(boolean isRelative) {
		looping = isRelative;
	}

	@MappedMethod
	protected void setIsRepeatableMapped(boolean isRepeatable) {
		repeat = isRepeatable;
	}

	@MappedMethod
	public boolean isRelative() {
		return super.isLooping();
	}

	@MappedMethod
	@Override
	public boolean isRepeatable() {
		return super.isRepeatable();
	}

	@MappedMethod
	protected AbstractSoundInstanceExtension(SoundEvent sound, SoundCategory category) {
		super(sound, category);
	}

	@MappedMethod
	protected AbstractSoundInstanceExtension(Identifier soundId, SoundCategory category) {
		super(soundId, category);
	}

	@MappedMethod
	public static void iterateSoundIds(Consumer<Identifier> consumer) {
		MinecraftClient.getInstance().getSoundManager().getKeys().forEach(identifier -> consumer.accept(new Identifier(identifier)));
	}
}
