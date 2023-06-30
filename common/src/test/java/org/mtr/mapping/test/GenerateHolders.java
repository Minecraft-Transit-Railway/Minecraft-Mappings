package org.mtr.mapping.test;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Assumptions;

import java.io.IOException;
import java.lang.reflect.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;

public final class GenerateHolders {

	private final Map<Class<?>, HolderInfo> classMap = new HashMap<>();
	private final JsonObject classesObject = new JsonObject();
	private static final Path PATH = Paths.get("@path@");
	private static final String GENERATE_KEY = "@generate@";
	private static final String NAMESPACE = "@namespace@";

	public HolderInfo put(String newClassName, Class<?> classObject) {
		final HolderInfo holderInfo = new HolderInfo(newClassName, false);
		classMap.put(classObject, holderInfo);
		classesObject.add(newClassName, holderInfo.methodsArray);
		return holderInfo;
	}

	public HolderInfo putAbstract(String newClassName, Class<?> classObject) {
		final HolderInfo holderInfo = new HolderInfo(newClassName, true);
		classMap.put(classObject, holderInfo);
		classesObject.add(newClassName, holderInfo.methodsArray);
		return holderInfo;
	}

	public void generate() throws IOException {
		Assumptions.assumeFalse(GENERATE_KEY.contains("@"));
		final Path holdersPath = PATH.resolve("src/main/java/org/mtr/mapping/holder");
		FileUtils.deleteDirectory(holdersPath.toFile());

		for (Map.Entry<Class<?>, HolderInfo> classEntry : classMap.entrySet()) {
			final Class<?> classObject = classEntry.getKey();
			final HolderInfo holderInfo = classEntry.getValue();
			final StringBuilder mainStringBuilder = new StringBuilder("package org.mtr.mapping.holder;public ");
			final String staticClassName = formatClassName(classObject.getName());

			if (classObject.isEnum()) {
				mainStringBuilder.append("enum ").append(holderInfo.className).append("{");
				appendIfNotEmpty(mainStringBuilder, classObject.getEnumConstants(), "", "", enumConstant -> String.format("%1$s(%2$s.%1$s)", ((Enum<?>) enumConstant).name(), staticClassName));
				mainStringBuilder.append(";public final ").append(staticClassName).append(" data;").append(holderInfo.className).append("(").append(staticClassName).append(" data){this.data=data;}");
			} else {
				mainStringBuilder.append(holderInfo.abstractMapping ? "abstract" : "final").append(" class ").append(holderInfo.className);
				appendGenerics(mainStringBuilder, classObject, true);
				final StringBuilder classNameStringBuilder = new StringBuilder(staticClassName);
				appendGenerics(classNameStringBuilder, classObject, false);
				final String className = classNameStringBuilder.toString();

				if (holderInfo.abstractMapping) {
					mainStringBuilder.append(" extends ").append(className).append("{");
				} else {
					mainStringBuilder.append("{public final ").append(className).append(" data;public ").append(holderInfo.className).append("(").append(className).append(" data){this.data=data;}");
				}

				final Map<Class<?>, Map<Type, Type>> classTree = walkClassTree(classObject);
				processMethods(Modifier.isAbstract(classObject.getModifiers()) && !holderInfo.abstractMapping ? new Executable[0] : classObject.getConstructors(), mainStringBuilder, className, staticClassName, holderInfo, classTree);
				processMethods(classObject.getMethods(), mainStringBuilder, className, staticClassName, holderInfo, classTree);
			}

			mainStringBuilder.append("}");
			Files.createDirectories(holdersPath);
			Files.write(holdersPath.resolve(String.format("%s.java", holderInfo.className)), mainStringBuilder.toString().getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
		}

		final Path methodsPath = PATH.resolve("../../build/existingMethods");
		Files.createDirectories(methodsPath);
		Files.write(methodsPath.resolve(String.format("%s.json", NAMESPACE)), classesObject.toString().getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
	}

	private void processMethods(Executable[] executables, StringBuilder mainStringBuilder, String className, String staticClassName, HolderInfo holderInfo, Map<Class<?>, Map<Type, Type>> classTree) {
		for (final Executable executable : executables) {
			final List<String> originalParameterList = new ArrayList<>();
			final List<String> parameterList = new ArrayList<>();
			final List<String> superList = new ArrayList<>();
			final List<String> mappedSuperList = new ArrayList<>();
			final List<String> resolvedSignature = new ArrayList<>();
			final List<String> forceResolvedSignature = new ArrayList<>();
			final Map<Type, Type> typeMap = classTree.get(executable.getDeclaringClass());
			final int modifiers = executable.getModifiers();
			final String originalMethodName = executable.getName();

			if (isValidExecutable(executable, originalParameterList, parameterList, superList, mappedSuperList, resolvedSignature, forceResolvedSignature, typeMap) && (!Modifier.isFinal(modifiers) || !holderInfo.abstractMapping) && !holderInfo.blacklist.contains(originalMethodName)) {
				final boolean isStatic = Modifier.isStatic(modifiers);
				final boolean isMethod = executable instanceof Method;
				final boolean generateExtraMethod = holderInfo.abstractMapping && !isStatic && isMethod;
				final String methodName = String.format("%s%s", getOrReturn(holderInfo.methodMap, originalMethodName), holderInfo.abstractMapping ? "2" : "");

				if (holderInfo.methodMap.containsKey(originalMethodName)) {
					mainStringBuilder.append("@org.mtr.mapping.annotation.MappedMethod ");
				}

				mainStringBuilder.append("public ");

				if (isStatic) {
					mainStringBuilder.append("static ");
				}

				if (Modifier.isFinal(modifiers)) {
					mainStringBuilder.append("final ");
				}

				final Type returnTypeClass = appendMethodHeader(mainStringBuilder, executable, methodName, true, parameterList, holderInfo.className, typeMap);
				final boolean isVoid = returnTypeClass == null || returnTypeClass == Void.TYPE;
				final String variables = String.format("(%s)", String.join(",", superList));
				final boolean resolvedReturnType;

				if (isMethod) {
					final String methodCall = String.format("%s.%s%s", isStatic ? staticClassName : holderInfo.abstractMapping ? "super" : "this.data", executable.getName(), variables);

					if (isVoid) {
						mainStringBuilder.append(methodCall);
						resolvedReturnType = false;
					} else {
						mainStringBuilder.append("return ");
						final StringBuilder returnStringBuilder = new StringBuilder();
						if (appendGenerics(returnStringBuilder, returnTypeClass, typeMap, true, false)) {
							mainStringBuilder.append(appendWrap(returnTypeClass, returnStringBuilder.toString(), methodCall));
							resolvedReturnType = true;
						} else {
							mainStringBuilder.append(methodCall);
							resolvedReturnType = false;
						}
					}

					final StringBuilder forceResolvedReturnStringBuilder = new StringBuilder();
					appendGenerics(forceResolvedReturnStringBuilder, returnTypeClass, typeMap, true, true);
					final JsonObject methodObject = new JsonObject();
					methodObject.addProperty("name", originalMethodName);
					methodObject.addProperty("signature", String.format("%s %s(%s)", Modifier.toString(modifiers), forceResolvedReturnStringBuilder, String.join(",", forceResolvedSignature)));
					holderInfo.methodsArray.add(methodObject);
				} else {
					if (holderInfo.abstractMapping) {
						mainStringBuilder.append("super");
					} else {
						mainStringBuilder.append("this.data=new ").append(className);
					}
					mainStringBuilder.append(variables);
					resolvedReturnType = false;
				}

				mainStringBuilder.append(";}");

				if (generateExtraMethod) {
					mainStringBuilder.append("public final ");
					final Type returnTypeClass2 = appendMethodHeader(mainStringBuilder, executable, originalMethodName, false, originalParameterList, holderInfo.className, typeMap);
					mainStringBuilder.append(returnTypeClass2 == null || returnTypeClass2 == Void.TYPE ? "" : "return ").append(methodName).append("(").append(String.join(",", mappedSuperList)).append(")").append(resolvedReturnType ? ".data" : "").append(";}");
				}
			}
		}
	}

	private boolean isValidExecutable(Executable executable, List<String> originalParameterList, List<String> parameterList, List<String> superList, List<String> mappedSuperList, List<String> resolvedSignature, List<String> forceResolvedSignature, Map<Type, Type> typeMap) {
		final Class<?> declaringClass = executable.getDeclaringClass();
		if (!Modifier.isPublic(executable.getModifiers()) || executable.isSynthetic() || declaringClass.equals(Object.class)) {
			return false;
		}

		final boolean[] allPublicParameterTypes = {true};
		iterateTwoArrays(executable.getParameters(), executable.getGenericParameterTypes(), (parameter, type) -> {
			final StringBuilder originalParameterStringBuilder = new StringBuilder();
			appendGenerics(originalParameterStringBuilder, type, typeMap, false, false);
			originalParameterList.add(String.format("%s %s", originalParameterStringBuilder, parameter.getName()));

			final StringBuilder parameterStringBuilder = new StringBuilder();
			final boolean isResolved = appendGenerics(parameterStringBuilder, type, typeMap, true, false);
			parameterList.add(String.format("%s %s", parameterStringBuilder, parameter.getName()));
			resolvedSignature.add(parameterStringBuilder.toString());

			final StringBuilder forceResolvedParameterStringBuilder = new StringBuilder();
			appendGenerics(forceResolvedParameterStringBuilder, type, typeMap, true, true);
			forceResolvedSignature.add(forceResolvedParameterStringBuilder.toString());

			superList.add(String.format("%s%s", parameter.getName(), isResolved ? ".data" : ""));
			mappedSuperList.add(isResolved ? appendWrap(type, parameterStringBuilder.toString(), parameter.getName()) : parameter.getName());

			if (!Modifier.isPublic(parameter.getType().getModifiers())) {
				allPublicParameterTypes[0] = false;
			}
		});

		return allPublicParameterTypes[0];
	}

	private Type appendMethodHeader(StringBuilder mainStringBuilder, Executable executable, String methodName, boolean resolve, List<String> parameterList, String className, Map<Type, Type> typeMap) {
		final Type returnTypeClass;

		if (executable instanceof Method) {
			appendGenerics(mainStringBuilder, executable, true);
			returnTypeClass = ((Method) executable).getGenericReturnType();
			appendGenerics(mainStringBuilder, returnTypeClass, typeMap, resolve, false);
			mainStringBuilder.append(" ").append(methodName);
		} else {
			returnTypeClass = null;
			mainStringBuilder.append(className);
		}

		mainStringBuilder.append("(").append(String.join(",", parameterList)).append(")");
		appendIfNotEmpty(mainStringBuilder, executable.getGenericExceptionTypes(), "throws ", "", Type::getTypeName);
		mainStringBuilder.append("{");
		return returnTypeClass;
	}

	private boolean appendGenerics(StringBuilder stringBuilder, Type type, Map<Type, Type> typeMap, boolean resolve, boolean forceResolveAll) {
		return appendGenerics(stringBuilder, type, typeMap, resolve, forceResolveAll, true);
	}

	private boolean appendGenerics(StringBuilder stringBuilder, Type type, Map<Type, Type> typeMap, boolean resolve, boolean forceResolveAll, boolean isFirst) {
		final boolean isParameterized = type instanceof ParameterizedType;
		final Type mappedType = getOrReturn(typeMap, isParameterized ? ((ParameterizedType) type).getRawType() : type);
		final boolean isResolved;

		if ((forceResolveAll || isFirst) && mappedType instanceof Class) {
			final HolderInfo resolvedClassName = classMap.get(mappedType);
			isResolved = resolve && resolvedClassName != null && !resolvedClassName.abstractMapping;
			stringBuilder.append(isResolved ? resolvedClassName.className : formatClassName(mappedType.getTypeName()));
		} else {
			isResolved = false;
			stringBuilder.append(formatClassName(mappedType.getTypeName()));
		}

		if (isParameterized) {
			appendIfNotEmpty(stringBuilder, ((ParameterizedType) type).getActualTypeArguments(), "<", ">", innerType -> {
				final StringBuilder innerStringBuilder = new StringBuilder();
				appendGenerics(innerStringBuilder, innerType, typeMap, resolve, forceResolveAll, false);
				return innerStringBuilder.toString();
			});
		}

		return isResolved;
	}

	private static void appendGenerics(StringBuilder stringBuilder, GenericDeclaration genericDeclaration, boolean getBounds) {
		appendIfNotEmpty(stringBuilder, genericDeclaration.getTypeParameters(), "<", ">", typeVariable -> {
			if (getBounds) {
				final StringBuilder extendsStringBuilder = new StringBuilder();
				appendIfNotEmpty(extendsStringBuilder, typeVariable.getBounds(), " extends ", "", Type::getTypeName);
				return String.format("%s%s", typeVariable.getName(), extendsStringBuilder);
			} else {
				return typeVariable.getName();
			}
		});
	}

	private static Map<Class<?>, Map<Type, Type>> walkClassTree(Class<?> classObject) {
		final Map<Class<?>, Map<Type, Type>> genericClassTree = new HashMap<>();
		Class<?> superClassObject = classObject;

		while (true) {
			final Type genericType = superClassObject.getGenericSuperclass();
			superClassObject = superClassObject.getSuperclass();

			if (superClassObject == null) {
				break;
			}

			final Map<Type, Type> actualTypes = new HashMap<>();

			if (genericType instanceof ParameterizedType) {
				iterateTwoArrays(superClassObject.getTypeParameters(), ((ParameterizedType) genericType).getActualTypeArguments(), actualTypes::put);
			}

			genericClassTree.put(superClassObject, actualTypes);
		}

		return genericClassTree;
	}

	private static String appendWrap(Type returnTypeClass, String resolvedType, String methodCall) {
		if (returnTypeClass instanceof Class && ((Class<?>) returnTypeClass).isEnum()) {
			return String.format("%s.valueOf(%s.toString())", resolvedType, methodCall);
		} else {
			return String.format("new %s(%s)", resolvedType, methodCall);
		}
	}

	private static <T> void appendIfNotEmpty(StringBuilder stringBuilder, T[] array, String prefix, String suffix, Function<T, String> callback) {
		if (array.length > 0) {
			stringBuilder.append(prefix);
			final List<String> dataList = new ArrayList<>();
			for (T data : array) {
				dataList.add(callback.apply(data));
			}
			stringBuilder.append(String.join(",", dataList)).append(suffix);
		}
	}

	private static <T> T getOrReturn(Map<T, T> map, T data) {
		if (map == null) {
			return data;
		} else {
			final T newData = map.get(data);
			return newData == null ? data : newData;
		}
	}

	private static <T, U> void iterateTwoArrays(T[] array1, U[] array2, BiConsumer<T, U> callback) {
		for (int i = 0; i < Math.min(array1.length, array2.length); i++) {
			callback.accept(array1[i], array2[i]);
		}
	}

	private static String formatClassName(String className) {
		return className.replace("$", ".");
	}

	public static final class HolderInfo {

		private final String className;
		private final boolean abstractMapping;
		private final Set<String> blacklist = new HashSet<>();
		private final Map<String, String> methodMap = new HashMap<>();
		private final JsonArray methodsArray = new JsonArray();

		private HolderInfo(String className, boolean abstractMapping) {
			this.className = className;
			this.abstractMapping = abstractMapping;
		}

		public HolderInfo blacklist(String methodName) {
			blacklist.add(methodName);
			return this;
		}

		public HolderInfo map(String newMethodName, String oldMethodName) {
			methodMap.put(oldMethodName, newMethodName);
			return this;
		}
	}
}
