package org.mtr.mapping.mapper;

import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.CompoundTag;
import org.mtr.mapping.holder.EntityAbstractMapping;
import org.mtr.mapping.holder.EntityType;
import org.mtr.mapping.holder.World;

import javax.annotation.Nonnull;

public abstract class EntityExtension extends EntityAbstractMapping {

	@MappedMethod
	public EntityExtension(EntityType<?> type, World world) {
		super(type, world);
	}

	@Deprecated
	@Nonnull
	@Override
	public final Packet<?> createSpawnPacket2() {
		return new EntitySpawnS2CPacket(this);
	}

	@Deprecated
	@Override
	protected final void readCustomDataFromNbt2(CompoundTag nbt) {
	}

	@Deprecated
	@Override
	protected final void writeCustomDataToNbt2(CompoundTag nbt) {
	}

	@MappedMethod
	public void setPosition3(double x, double y, double z) {
		super.setPosition2(x, y, z);
	}
}
