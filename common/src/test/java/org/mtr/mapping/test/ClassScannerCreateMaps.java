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

	private final JsonObject classesObject = new JsonObject();

	@Override
	void preScan() {
	}

	@Override
	void iterateClass(ClassInfo classInfo, String minecraftClassName, String genericsWithBounds, String generics, String genericsImplied, String enumValues) {
		classesObject.add(classInfo.className, new JsonArray());
	}

	@Override
	void iterateExecutable(ClassInfo classInfo, String minecraftClassName, boolean isClassParameterized, String minecraftMethodName, boolean isMethod, boolean isStatic, boolean isFinal, boolean isAbstract, String modifiers, String generics, TypeInfo returnType, List<TypeInfo> parameters, String exceptions, String key) {
		final JsonObject methodObject = new JsonObject();
		methodObject.addProperty("name", isMethod ? minecraftMethodName : classInfo.className);
		methodObject.addProperty("signature", key);
		final JsonArray parameterNullableArray = new JsonArray();
		parameters.forEach(typeInfo -> parameterNullableArray.add(typeInfo.isNullable));
		methodObject.add("parametersNullable", parameterNullableArray);
		methodObject.addProperty("returnNullable", returnType.isNullable);
		classesObject.getAsJsonArray(classInfo.className).add(methodObject);
	}

	@Override
	void iterateField(ClassInfo classInfo, String minecraftClassName, TypeInfo fieldType, boolean isStatic, boolean isFinal, String key) {
		final JsonObject fieldObject = new JsonObject();
		fieldObject.addProperty("name", fieldType.variableName);
		fieldObject.addProperty("signature", key);
		fieldObject.addProperty("returnNullable", fieldType.isNullable);
		classesObject.getAsJsonArray(classInfo.className).add(fieldObject);
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
