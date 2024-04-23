package org.mtr.mapping.mapper;

import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
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
	public final Packet<?> getAddEntityPacket2() {
		return new ClientboundAddEntityPacket(this);
	}

	@Deprecated
	@Override
	protected final void readAdditionalSaveData2(CompoundTag arg0) {
	}

	@Deprecated
	@Override
	protected final void addAdditionalSaveData2(CompoundTag arg0) {
	}

	@MappedMethod
	public void setPosition3(double x, double y, double z) {
		super.setPos2(x, y, z);
	}
}
