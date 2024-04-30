package org.mtr.mapping.mapper;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
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
	public final Packet<?> getAddEntityPacket() {
		return new ClientboundAddEntityPacket(this);
	}

	@Deprecated
	@Override
	protected final void readAdditionalSaveData(CompoundTag arg0) {
	}

	@Deprecated
	@Override
	protected final void addAdditionalSaveData(CompoundTag arg0) {
	}

	@MappedMethod
	public void setPosition2(double x, double y, double z) {
		super.setPos(x, y, z);
	}
}
