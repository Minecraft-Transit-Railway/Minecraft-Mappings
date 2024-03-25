package org.mtr.mapping.registry;

import net.minecraft.particles.BasicParticleType;
import net.minecraftforge.registries.ForgeRegistries;
import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.DefaultParticleType;
import org.mtr.mapping.holder.Identifier;
import org.mtr.mapping.tool.RegistryObject;

import java.util.function.Consumer;

public final class ParticleTypeRegistryObject extends RegistryObject<DefaultParticleType> {

	final Identifier identifier;
	private final net.minecraftforge.fml.RegistryObject<BasicParticleType> registryObject;

	ParticleTypeRegistryObject(Identifier identifier) {
		this.identifier = identifier;
		registryObject = net.minecraftforge.fml.RegistryObject.of(identifier.data, ForgeRegistries.PARTICLE_TYPES);
	}

	@MappedMethod
	@Override
	public DefaultParticleType get() {
		return new DefaultParticleType(registryObject.get());
	}

	@MappedMethod
	@Override
	public boolean isPresent() {
		return registryObject.isPresent();
	}

	@MappedMethod
	@Override
	public void ifPresent(Consumer<DefaultParticleType> consumer) {
		registryObject.ifPresent(data -> consumer.accept(new DefaultParticleType(data)));
	}
}
