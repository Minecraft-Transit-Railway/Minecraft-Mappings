package org.mtr.mapping.mapper;

import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.MovingSoundInstanceAbstractMapping;
import org.mtr.mapping.holder.SoundCategory;
import org.mtr.mapping.holder.SoundEvent;

public abstract class MovingSoundInstanceExtension extends MovingSoundInstanceAbstractMapping {

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
	public boolean isRelative2() {
		return super.isRelative2();
	}

	@MappedMethod
	@Override
	public boolean isRepeatable2() {
		return super.isRepeatable2();
	}

	@MappedMethod
	protected MovingSoundInstanceExtension(SoundEvent sound, SoundCategory category) {
		super(sound, category);
	}

	@MappedMethod
	protected void setVolume(float volume) {
		this.volume = volume;
	}

	@MappedMethod
	protected void setPitch(float pitch) {
		this.pitch = pitch;
	}

	@MappedMethod
	protected void setRepeatDelay(int repeatDelay) {
		this.repeatDelay = repeatDelay;
	}

	@MappedMethod
	protected void setX(double x) {
		this.x = x;
	}

	@MappedMethod
	protected void setY(double y) {
		this.y = y;
	}

	@MappedMethod
	protected void setZ(double z) {
		this.z = z;
	}
}
