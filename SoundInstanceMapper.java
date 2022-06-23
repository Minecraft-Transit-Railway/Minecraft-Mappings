package @package@;

import net.minecraft.client.resources.sounds.AbstractSoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;

public abstract class SoundInstanceMapper extends AbstractSoundInstance {

	public SoundInstanceMapper(SoundEvent soundEvent, SoundSource soundSource) {
		super(soundEvent, soundSource);
	}
}
