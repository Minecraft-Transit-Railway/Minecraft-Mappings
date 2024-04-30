package org.mtr.mapping.mapper;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.core.particles.SimpleParticleType;
import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.ClientWorld;
import org.mtr.mapping.holder.Particle;
import org.mtr.mapping.holder.SpriteBillboardParticle;
import org.mtr.mapping.holder.SpriteProvider;
import org.mtr.mapping.tool.DummyClass;

public abstract class ParticleFactoryExtension implements ParticleProvider<SimpleParticleType> {

	private final CreateParticle createParticle;
	private final CreateSpriteBillboardParticle createSpriteBillboardParticle;
	private final SpriteProvider spriteProvider;

	@MappedMethod
	public ParticleFactoryExtension(CreateParticle createParticle, SpriteProvider spriteProvider) {
		this.createParticle = createParticle;
		createSpriteBillboardParticle = null;
		this.spriteProvider = spriteProvider;
	}

	@MappedMethod
	public ParticleFactoryExtension(CreateSpriteBillboardParticle createSpriteBillboardParticle, SpriteProvider spriteProvider) {
		createParticle = null;
		this.createSpriteBillboardParticle = createSpriteBillboardParticle;
		this.spriteProvider = spriteProvider;
	}

	@Deprecated
	public final net.minecraft.client.particle.Particle createParticle(SimpleParticleType defaultParticleType, ClientLevel clientWorld, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
		if (createParticle != null) {
			return createParticle.create(new ClientWorld(clientWorld), x, y, z, velocityX, velocityY, velocityZ).data;
		} else if (createSpriteBillboardParticle != null) {
			final SpriteBillboardParticle spriteBillboardParticle = createSpriteBillboardParticle.create(new ClientWorld(clientWorld), x, y, z, velocityX, velocityY, velocityZ);
			spriteBillboardParticle.data.pickSprite(spriteProvider.data);
			return spriteBillboardParticle.data;
		} else {
			final NullPointerException nullPointerException = new NullPointerException("Both createParticle and createSpriteBillboardParticle are null!");
			DummyClass.logException(nullPointerException);
			throw nullPointerException;
		}
	}

	@FunctionalInterface
	public interface CreateParticle {
		@MappedMethod
		Particle create(ClientWorld clientWorld, double x, double y, double z, double velocityX, double velocityY, double velocityZ);
	}

	@FunctionalInterface
	public interface CreateSpriteBillboardParticle {
		@MappedMethod
		SpriteBillboardParticle create(ClientWorld clientWorld, double x, double y, double z, double velocityX, double velocityY, double velocityZ);
	}
}
