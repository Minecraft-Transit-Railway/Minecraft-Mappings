package org.mtr.mapping.mapper;

import net.minecraft.util.StringRepresentable;
import org.mtr.mapping.annotation.MappedMethod;

public interface StringIdentifiable extends StringRepresentable {

	@MappedMethod
	String asString();

	@Override
	default String getSerializedName() {
		return asString();
	}
}
