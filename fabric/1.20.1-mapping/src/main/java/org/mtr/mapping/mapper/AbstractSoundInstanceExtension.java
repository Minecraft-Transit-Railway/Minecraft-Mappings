package org.mtr.mapping.mapper;

import net.minecraft.client.MinecraftClient;
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
		repeat = isRepeatable;
	}

	@MappedMethod
	@Override
	public boolean isRelative() {
		return super.isRelative();
	}

	@MappedMethod
	@Override
	public boolean isRepeatable() {
		return super.isRepeatable();
	}

	@MappedMethod
	protected AbstractSoundInstanceExtension(SoundEvent sound, SoundCategory category) {
		super(sound, category, new Random(net.minecraft.util.math.random.Random.create()));
	}

	@MappedMethod
	protected AbstractSoundInstanceExtension(Identifier soundId, SoundCategory category) {
		super(soundId, category, new Random(net.minecraft.util.math.random.Random.create()));
	}

	@MappedMethod
	public static void iterateSoundIds(Consumer<Identifier> consumer) {
		MinecraftClient.getInstance().getSoundManager().getKeys().forEach(identifier -> consumer.accept(new Identifier(identifier)));
	}
}
