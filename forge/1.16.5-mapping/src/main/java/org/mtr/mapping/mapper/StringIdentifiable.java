package org.mtr.mapping.mapper;

import net.minecraft.util.IStringSerializable;
import org.mtr.mapping.annotation.MappedMethod;

public interface StringIdentifiable extends IStringSerializable {

	@MappedMethod
	String asString();

	@Deprecated
	@Override
	default String getSerializedName() {
		return asString();
	}
}
