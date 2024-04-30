package org.mtr.mapping.mapper;

import net.minecraft.nbt.NbtCompound;
import org.mtr.mapping.annotation.MappedMethod;
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
	protected final void readCustomDataFromNbt(NbtCompound nbt) {
	}

	@Deprecated
	@Override
	protected final void writeCustomDataToNbt(NbtCompound nbt) {
	}

	@MappedMethod
	public void setPosition2(double x, double y, double z) {
		super.setPosition(x, y, z);
	}
}
