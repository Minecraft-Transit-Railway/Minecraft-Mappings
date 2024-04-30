package org.mtr.mapping.mapper;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.server.SSpawnObjectPacket;
import org.mtr.mapping.annotation.MappedMethod;
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
	public final IPacket<?> getAddEntityPacket() {
		return new SSpawnObjectPacket(this);
	}

	@Deprecated
	@Override
	protected final void readAdditionalSaveData(CompoundNBT arg0) {
	}

	@Deprecated
	@Override
	protected final void addAdditionalSaveData(CompoundNBT arg0) {
	}

	@MappedMethod
	public void setPosition2(double x, double y, double z) {
		super.setPos(x, y, z);
	}
}
