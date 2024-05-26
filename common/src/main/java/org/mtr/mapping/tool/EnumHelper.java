package org.mtr.mapping.tool;

import org.mtr.mapping.annotation.MappedMethod;

import java.lang.reflect.Executable;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public final class EnumHelper {

	private static final Set<String> SIGNATURES;

	static {
		SIGNATURES = Arrays.stream(Enum.class.getMethods()).map(EnumHelper::quickSerialize).collect(Collectors.toSet());
		SIGNATURES.add(EnumHelper.quickSerialize(Modifier.PUBLIC, "equals", "java.lang.Object"));
		SIGNATURES.add(EnumHelper.quickSerialize(Modifier.PUBLIC | Modifier.STATIC, "values"));
		SIGNATURES.add(EnumHelper.quickSerialize(Modifier.PUBLIC | Modifier.STATIC, "valueOf", "java.lang.String"));
	}

	@MappedMethod
	public static boolean containsSignature(Executable executable) {
		return SIGNATURES.contains(quickSerialize(executable));
	}

	private static String quickSerialize(Executable executable) {
		final Type[] types = executable.getGenericParameterTypes();
		final String[] typesString = new String[types.length];
		for (int i = 0; i < types.length; i++) {
			typesString[i] = types[i].getTypeName();
		}
		return quickSerialize(executable.getModifiers(), executable.getName(), typesString);
	}

	private static String quickSerialize(int modifiers, String name, String... parameters) {
		return String.format("%s %s %s", modifiers, name, String.join(",", parameters));
	}
}
