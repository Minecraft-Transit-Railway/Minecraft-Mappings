package @package@;

import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;

public abstract class TickableSoundInstanceMapper extends AbstractTickableSoundInstance {

	public TickableSoundInstanceMapper(SoundEvent soundEvent, SoundSource soundSource) {
		super(soundEvent, soundSource, RandomSource.create());
	}
}
