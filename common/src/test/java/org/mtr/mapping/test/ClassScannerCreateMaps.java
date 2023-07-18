package org.mtr.mapping.test;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;

public final class ClassScannerCreateMaps extends ClassScannerBase {

	private String currentClassName;
	private final JsonObject classesObject = new JsonObject();

	@Override
	void preScan() {
	}

	@Override
	void iterateClass(ClassInfo classInfo, String minecraftClassName, String genericsWithBounds, String generics, String genericsImplied, String enumValues) {
		currentClassName = classInfo.className;
		classesObject.add(currentClassName, new JsonArray());
	}

	@Override
	void iterateExecutable(ClassInfo classInfo, String minecraftClassName, boolean isClassParameterized, String minecraftMethodName, boolean isMethod, boolean isStatic, boolean isFinal, String modifiers, String generics, TypeInfo returnType, List<TypeInfo> parameters, String exceptions, String key) {
		final JsonObject methodObject = new JsonObject();
		methodObject.addProperty("name", isMethod ? minecraftMethodName : classInfo.className);
		methodObject.addProperty("signature", key);
		final JsonArray parameterNullableArray = new JsonArray();
		parameters.forEach(typeInfo -> parameterNullableArray.add(typeInfo.isNullable));
		methodObject.add("parametersNullable", parameterNullableArray);
		methodObject.addProperty("returnNullable", returnType.isNullable);
		classesObject.getAsJsonArray(currentClassName).add(methodObject);
	}

	@Override
	void postIterateClass(ClassInfo classInfo) {
	}

	@Override
	void postScan() {
		final Path methodsPath = PATH.resolve("../../build/existingMethods");
		try {
			Files.createDirectories(methodsPath);
			Files.write(methodsPath.resolve(NAMESPACE + ".json"), classesObject.toString().getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
