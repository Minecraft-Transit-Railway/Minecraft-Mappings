package @package@;

import net.minecraft.client.resources.sounds.AbstractSoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;

public abstract class SoundInstanceMapper extends AbstractSoundInstance {

	public SoundInstanceMapper(SoundEvent soundEvent, SoundSource soundSource) {
		super(soundEvent, soundSource, RandomSource.create());
	}
}
