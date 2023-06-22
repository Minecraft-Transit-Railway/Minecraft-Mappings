package org.mtr.mapping.test;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;
import org.mtr.mapping.annotation.MappedMethod;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class SearchForMappedMethodsTest {

	private static final String NAMESPACE = "@namespace@";

	@Test
	public void searchForMappedMethods() throws IOException, ClassNotFoundException {
		Assumptions.assumeFalse(NAMESPACE.contains("@"));
		final List<String> signatures = new ArrayList<>();

		for (final String className : new Reflections("org.mtr").getAll(Scanners.SubTypes)) {
			final Class<?> classObject = ClassLoader.getSystemClassLoader().loadClass(className);
			if (classObject.getPackage().getName().startsWith("org.mtr")) {
				for (final Method method : classObject.getDeclaredMethods()) {
					if (method.isAnnotationPresent(MappedMethod.class)) {
						signatures.add(method.toString());
					} else {
						final int modifiers = method.getModifiers();
						Assertions.assertTrue(Modifier.isFinal(modifiers) || !Modifier.isPublic(modifiers), String.format("%s\n%s\n%s", NAMESPACE, className, method));
					}
				}
				for (final Constructor<?> constructor : classObject.getDeclaredConstructors()) {
					if (constructor.isAnnotationPresent(MappedMethod.class)) {
						signatures.add(constructor.toString());
					}
				}
			}
		}

		Collections.sort(signatures);
		final Path directory = Paths.get("../../build/mappedMethods/");
		Files.createDirectories(directory);
		Files.write(directory.resolve(NAMESPACE + ".txt"), signatures, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
	}
}
