package org.mtr.mapping.mapper;

import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.CompoundTag;
import org.mtr.mapping.holder.EntityAbstractMapping;
import org.mtr.mapping.holder.EntityType;
import org.mtr.mapping.holder.World;

public abstract class EntityExtension extends EntityAbstractMapping {

	@MappedMethod
	public EntityExtension(EntityType<?> type, World world) {
		super(type, world);
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
