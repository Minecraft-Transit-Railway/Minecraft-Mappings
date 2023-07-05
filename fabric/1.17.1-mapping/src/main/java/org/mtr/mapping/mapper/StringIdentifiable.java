package org.mtr.mapping.mapper;

import org.mtr.mapping.annotation.MappedMethod;

public interface StringIdentifiable extends net.minecraft.util.StringIdentifiable {

	@MappedMethod
	@Override
	String asString();
}
