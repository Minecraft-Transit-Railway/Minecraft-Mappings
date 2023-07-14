package org.mtr.mapping.test;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Assumptions;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class GenerateHolders {

	private final Map<Class<?>, HolderInfo> classMap = new HashMap<>();
	private final JsonObject classesObject = new JsonObject();
	private static final Set<String> BLACKLISTED_SIGNATURES = Arrays.stream(Enum.class.getMethods()).map(GenerateHolders::serializeMethod).collect(Collectors.toSet());
	private static final Path PATH = Paths.get("@path@");
	private static final String NAMESPACE = "@namespace@";
	private static final boolean WRITE_FILES = Boolean.parseBoolean("@writeFiles@");

	static {
		BLACKLISTED_SIGNATURES.add(serializeMethod(Modifier.PUBLIC | Modifier.STATIC, "values"));
		BLACKLISTED_SIGNATURES.add(serializeMethod(Modifier.PUBLIC | Modifier.STATIC, "valueOf", "java.lang.String"));
	}

	public void put(String newClassName, Class<?> classObject, String... blacklistMethods) {
		final HolderInfo holderInfo = new HolderInfo(newClassName, false, blacklistMethods);
		classMap.put(classObject, holderInfo);
		classesObject.add(newClassName, holderInfo.methodsArray);
	}

	public void putAbstract(String newClassName, Class<?> classObject, String... blacklistMethods) {
		final HolderInfo holderInfo = new HolderInfo(newClassName, true, blacklistMethods);
		classMap.put(classObject, holderInfo);
		classesObject.add(newClassName, holderInfo.methodsArray);
	}

	public void generate() throws IOException {
		Assumptions.assumeFalse(NAMESPACE.contains("@"));
		final Path holdersPath = PATH.resolve("src/main/java/org/mtr/mapping/holder");

		if (WRITE_FILES) {
			FileUtils.deleteDirectory(holdersPath.toFile());
		}

		for (Map.Entry<Class<?>, HolderInfo> classEntry : classMap.entrySet()) {
			final Class<?> classObject = classEntry.getKey();
			final HolderInfo holderInfo = classEntry.getValue();
			if (holderInfo.abstractMapping) {
				generate(holdersPath, classObject, new HolderInfo(holderInfo, holderInfo.className + "AbstractMapping", true));
				generate(holdersPath, classObject, new HolderInfo(holderInfo, holderInfo.className, false));
			} else {
				generate(holdersPath, classObject, holderInfo);
			}
		}

		if (!WRITE_FILES) {
			final Path methodsPath = PATH.resolve("../../build/existingMethods");
			Files.createDirectories(methodsPath);
			Files.write(methodsPath.resolve(String.format("%s.json", NAMESPACE)), classesObject.toString().getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
		}
	}

	private void generate(Path holdersPath, Class<?> classObject, HolderInfo holderInfo) throws IOException {
		final StringBuilder mainStringBuilder = new StringBuilder("package org.mtr.mapping.holder;@javax.annotation.ParametersAreNonnullByDefault public ");
		final String staticClassName = formatClassName(classObject.getName());
		final Map<Class<?>, Map<Type, Type>> classTree = new HashMap<>();
		walkClassTree(classTree, classObject, classObject1 -> new Type[]{classObject1.getGenericSuperclass()}, classObject1 -> new Class<?>[]{classObject1.getSuperclass()});
		walkClassTree(classTree, classObject, Class::getGenericInterfaces, Class::getInterfaces);

		if (classObject.isEnum()) {
			mainStringBuilder.append("enum ").append(holderInfo.className).append("{");
			appendIfNotEmpty(mainStringBuilder, classObject.getEnumConstants(), "", "", ",", enumConstant -> String.format("%1$s(%2$s.%1$s)", ((Enum<?>) enumConstant).name(), staticClassName));
			mainStringBuilder.append(";public final ").append(staticClassName).append(" data;").append(holderInfo.className).append("(").append(staticClassName).append(" data){this.data=data;}public static ").append(holderInfo.className).append(" convert(").append(staticClassName).append(" data){return data==null?null:values()[data.ordinal()];}");
			processMethods(classObject.getMethods(), mainStringBuilder, staticClassName, staticClassName, holderInfo, classTree);
		} else {
			mainStringBuilder.append(holderInfo.abstractMapping ? "abstract" : "final").append(" class ").append(holderInfo.className);
			appendGenerics(mainStringBuilder, classObject, false, true);
			mainStringBuilder.append(" extends ");
			final String className = staticClassName + getStringFromMethod(stringBuilder -> appendGenerics(stringBuilder, classObject, false, false));

			if (holderInfo.abstractMapping) {
				mainStringBuilder.append(className).append("{");
			} else {
				mainStringBuilder.append("org.mtr.mapping.tool.HolderBase<").append(className).append(">{public ").append(holderInfo.className).append("(").append(className).append(" data){super(data);}@org.mtr.mapping.annotation.MappedMethod public static ");
				appendGenerics(mainStringBuilder, classObject, false, true);
				mainStringBuilder.append(holderInfo.className);
				appendGenerics(mainStringBuilder, classObject, false, false);
				mainStringBuilder.append(" cast(org.mtr.mapping.tool.HolderBase<?> data){return new ").append(holderInfo.className);
				appendGenerics(mainStringBuilder, classObject, true, false);
				mainStringBuilder.append("((").append(className).append(")data.data);}@org.mtr.mapping.annotation.MappedMethod public static boolean isInstance(org.mtr.mapping.tool.HolderBase<?> data){return data.data instanceof ");
				mainStringBuilder.append(staticClassName);
				mainStringBuilder.append(";}");
			}

			processMethods(Modifier.isAbstract(classObject.getModifiers()) && !holderInfo.abstractMapping ? new Executable[0] : classObject.getConstructors(), mainStringBuilder, className, staticClassName, holderInfo, classTree);
			processMethods(classObject.getMethods(), mainStringBuilder, className, staticClassName, holderInfo, classTree);
		}

		mainStringBuilder.append("}");

		if (WRITE_FILES) {
			Files.createDirectories(holdersPath);
			Files.write(holdersPath.resolve(String.format("%s.java", holderInfo.className)), mainStringBuilder.toString().getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
		}
	}

	private void processMethods(Executable[] executables, StringBuilder mainStringBuilder, String className, String staticClassName, HolderInfo holderInfo, Map<Class<?>, Map<Type, Type>> classTree) {
		for (final Executable executable : executables) {
			final List<String> originalParameterList = new ArrayList<>();
			final List<String> parameterList = new ArrayList<>();
			final List<String> superList = new ArrayList<>();
			final List<String> mappedSuperList = new ArrayList<>();
			final List<String> resolvedSignature = new ArrayList<>();
			final Map<Type, Type> typeMap = classTree.get(executable.getDeclaringClass());
			final int modifiers = executable.getModifiers();
			final String originalMethodName = executable.getName();

			if (isValidExecutable(executable, originalParameterList, parameterList, superList, mappedSuperList, resolvedSignature, typeMap) && (!Modifier.isAbstract(modifiers) || !holderInfo.abstractMapping) && !holderInfo.blacklist.contains(originalMethodName)) {
				final boolean isStatic = Modifier.isStatic(modifiers);
				final boolean isFinal = Modifier.isFinal(modifiers);
				final boolean isMethod = executable instanceof Method;
				final boolean generateExtraMethod = holderInfo.abstractMapping && !isStatic && !isFinal && isMethod;

				final String signatureKey = String.format("%s %s", Modifier.toString(modifiers), getStringFromMethod(stringBuilder -> appendMethodHeader(stringBuilder, executable, "", true, resolvedSignature, holderInfo.className, typeMap)));
				final String methodName = String.format("%s%s", holderInfo.getMappedMethod(mainStringBuilder, signatureKey, originalMethodName), holderInfo.abstractMapping ? "2" : "");
				appendAnnotations(mainStringBuilder, executable);
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

				if (isMethod) {
					final String methodCall = String.format("%s.%s%s", isStatic ? staticClassName : holderInfo.abstractMapping ? "super" : "this.data", originalMethodName, variables);

					if (isVoid) {
						mainStringBuilder.append(methodCall);
					} else {
						mainStringBuilder.append("return ");
						final StringBuilder returnStringBuilder = new StringBuilder();
						final ResolveState resolveState = appendGenerics(returnStringBuilder, returnTypeClass, typeMap, true, true);
						mainStringBuilder.append(appendWrap(returnTypeClass, returnStringBuilder.toString(), methodCall, resolveState));
					}

					final JsonObject methodObject = new JsonObject();
					methodObject.addProperty("name", originalMethodName);
					methodObject.addProperty("signature", signatureKey);
					holderInfo.methodsArray.add(methodObject);
				} else {
					mainStringBuilder.append("super");
					if (holderInfo.abstractMapping) {
						mainStringBuilder.append(variables);
					} else {
						mainStringBuilder.append("(new ").append(className).append(variables).append(")");
					}
				}

				mainStringBuilder.append(";}");

				if (generateExtraMethod) {
					mainStringBuilder.append("@Deprecated public final ");
					final Type returnTypeClass2 = appendMethodHeader(mainStringBuilder, executable, originalMethodName, false, originalParameterList, holderInfo.className, typeMap);
					final String methodCall = String.format("%s(%s)", methodName, String.join(",", mappedSuperList));

					if (returnTypeClass2 == null || returnTypeClass2 == Void.TYPE) {
						mainStringBuilder.append(methodCall);
					} else {
						mainStringBuilder.append("final ");
						final ResolveState resolveState = appendGenerics(mainStringBuilder, returnTypeClass, typeMap, true, false);
						mainStringBuilder.append(" tempResult=").append(methodCall).append(";").append("return ").append(resolveState.format("tempResult", true));
					}

					mainStringBuilder.append(";}");
				}
			}
		}
	}

	private boolean isValidExecutable(Executable executable, List<String> originalParameterList, List<String> parameterList, List<String> superList, List<String> mappedSuperList, List<String> resolvedSignature, Map<Type, Type> typeMap) {
		if (!Modifier.isPublic(executable.getModifiers()) || executable.isSynthetic() || BLACKLISTED_SIGNATURES.contains(serializeMethod(executable))) {
			return false;
		}

		final boolean[] allPublicParameterTypes = {true};
		iterateTwoArrays(executable.getParameters(), executable.getGenericParameterTypes(), (parameter, type) -> {
			originalParameterList.add(String.format("%s %s", getStringFromMethod(stringBuilder -> appendGenerics(stringBuilder, type, typeMap, false, false)), parameter.getName()));

			final StringBuilder parameterStringBuilder = new StringBuilder();
			final ResolveState resolveState = appendGenerics(parameterStringBuilder, type, typeMap, true, false);
			final StringBuilder annotationsStringBuilder = new StringBuilder();
			final boolean nullable = appendAnnotations(annotationsStringBuilder, parameter);
			parameterList.add(String.format("%s%s %s", annotationsStringBuilder, parameterStringBuilder, parameter.getName()));
			resolvedSignature.add(parameterStringBuilder.toString());

			superList.add(resolveState.format(parameter.getName(), nullable));
			mappedSuperList.add(appendWrap(type, getStringFromMethod(stringBuilder -> appendGenerics(stringBuilder, type, typeMap, true, true)), parameter.getName(), resolveState));

			if (!Modifier.isPublic(parameter.getType().getModifiers())) {
				allPublicParameterTypes[0] = false;
			}
		});

		return allPublicParameterTypes[0];
	}

	private Type appendMethodHeader(StringBuilder mainStringBuilder, Executable executable, String methodName, boolean resolve, List<String> parameterList, String className, Map<Type, Type> typeMap) {
		final Type returnTypeClass;

		if (executable instanceof Method) {
			appendGenerics(mainStringBuilder, executable, false, true);
			returnTypeClass = ((Method) executable).getGenericReturnType();
			appendGenerics(mainStringBuilder, returnTypeClass, typeMap, resolve, false);
			mainStringBuilder.append(" ").append(methodName);
		} else {
			returnTypeClass = null;
			mainStringBuilder.append(className);
		}

		mainStringBuilder.append("(").append(String.join(",", parameterList)).append(")");
		appendIfNotEmpty(mainStringBuilder, executable.getGenericExceptionTypes(), "throws ", "", ",", Type::getTypeName);

		if (!methodName.isEmpty()) {
			mainStringBuilder.append("{");
		}

		return returnTypeClass;
	}

	private String appendWrap(Type returnTypeClass, String resolvedType, String methodCall, ResolveState resolveState) {
		switch (resolveState) {
			case RESOLVED:
				if (returnTypeClass instanceof Class && ((Class<?>) returnTypeClass).isEnum()) {
					return String.format("%s.convert(%s)", resolvedType, methodCall);
				} else {
					return String.format("new %s(%s)", resolvedType, methodCall);
				}
			case RESOLVED_COLLECTION:
				final Type[] types = returnTypeClass instanceof ParameterizedType ? ((ParameterizedType) returnTypeClass).getActualTypeArguments() : new Type[0];
				if (types.length == 1 && types[0] instanceof Class) {
					final Class<?> newReturnClass = (Class<?>) types[0];
					final HolderInfo holderInfo = classMap.get(newReturnClass);
					if (holderInfo != null) {
						final boolean isEnum = newReturnClass.isEnum();
						return String.format("org.mtr.mapping.tool.HolderBase.convert%sCollection(%s,%s::%s)", isEnum ? "Enum" : "", methodCall, holderInfo.className, isEnum ? "convert" : "new");
					}
				}
			default:
				return methodCall;
		}
	}

	private ResolveState appendGenerics(StringBuilder stringBuilder, Type type, Map<Type, Type> typeMap, boolean resolve, boolean impliedType) {
		return appendGenerics(stringBuilder, type, typeMap, resolve, impliedType, true);
	}

	private ResolveState appendGenerics(StringBuilder stringBuilder, Type type, Map<Type, Type> typeMap, boolean resolve, boolean impliedType, boolean isFirst) {
		final boolean isParameterized = type instanceof ParameterizedType;
		final Type mappedType = getOrReturn(typeMap, isParameterized ? ((ParameterizedType) type).getRawType() : type);
		final ResolveState resolveState;

		if (isFirst && mappedType instanceof Class) {
			final HolderInfo resolvedClassName = classMap.get(mappedType);
			final boolean isResolved = resolve && resolvedClassName != null;
			stringBuilder.append(isResolved ? resolvedClassName.className : formatClassName(mappedType.getTypeName()));
			final Type[] typeArguments = resolve && isParameterized && (mappedType.equals(List.class) || mappedType.equals(Set.class)) ? ((ParameterizedType) type).getActualTypeArguments() : new Type[0];

			if (typeArguments.length == 1 && typeArguments[0] instanceof Class) {
				final HolderInfo resolvedCollectionClassName = classMap.get(typeArguments[0]);
				if (resolvedCollectionClassName == null) {
					resolveState = ResolveState.NONE;
				} else {
					stringBuilder.append("<").append(resolvedCollectionClassName.className).append(">");
					return ResolveState.RESOLVED_COLLECTION;
				}
			} else {
				resolveState = isResolved ? ResolveState.RESOLVED : ResolveState.NONE;
			}
		} else {
			resolveState = ResolveState.NONE;
			if (type instanceof WildcardType) {
				stringBuilder.append("?");
				final Type[] upperBounds = Arrays.stream(((WildcardType) type).getUpperBounds()).filter(upperBound -> !upperBound.equals(Object.class)).toArray(Type[]::new);
				final Type[] lowerBounds = Arrays.stream(((WildcardType) type).getLowerBounds()).filter(lowerBound -> !lowerBound.equals(Object.class)).toArray(Type[]::new);
				final boolean useUpper = upperBounds.length > 0;
				appendIfNotEmpty(stringBuilder, useUpper ? upperBounds : lowerBounds, useUpper ? " extends " : " super ", "", "&", boundType -> formatClassName(getOrReturn(typeMap, boundType).getTypeName()));
			} else {
				stringBuilder.append(formatClassName(mappedType.getTypeName()));
			}
		}

		if (isParameterized) {
			if (impliedType) {
				stringBuilder.append("<>");
			} else {
				appendIfNotEmpty(stringBuilder, ((ParameterizedType) type).getActualTypeArguments(), "<", ">", ",", innerType -> getStringFromMethod(innerStringBuilder -> appendGenerics(innerStringBuilder, innerType, typeMap, resolve, false, false)));
			}
		}

		return resolveState;
	}

	private static void appendGenerics(StringBuilder stringBuilder, GenericDeclaration genericDeclaration, boolean impliedType, boolean getBounds) {
		appendIfNotEmpty(stringBuilder, genericDeclaration.getTypeParameters(), "<", ">", ",", typeVariable -> {
			if (impliedType) {
				return "";
			} else if (getBounds) {
				return String.format("%s%s", typeVariable.getName(), getStringFromMethod(extendsStringBuilder -> appendIfNotEmpty(extendsStringBuilder, typeVariable.getBounds(), " extends ", "", "&", Type::getTypeName)));
			} else {
				return typeVariable.getName();
			}
		});
	}

	private static boolean appendAnnotations(StringBuilder stringBuilder, AnnotatedElement annotatedElement) {
		boolean hasNullable = false;
		for (final Annotation annotation : annotatedElement.getAnnotations()) {
			if (annotation.toString().toLowerCase(Locale.ENGLISH).contains("nullable")) {
				hasNullable = true;
			}
		}
		if (hasNullable) {
			stringBuilder.append("@javax.annotation.Nullable ");
		}
		return hasNullable;
	}

	private static void walkClassTree(Map<Class<?>, Map<Type, Type>> genericClassTree, Class<?> classObject, Function<Class<?>, Type[]> getGenericTypes, Function<Class<?>, Class<?>[]> getSuper) {
		final Type[] genericTypes = getGenericTypes.apply(classObject);
		final Class<?>[] newClassObjects = getSuper.apply(classObject);

		for (final Class<?> newClassObject : newClassObjects) {
			if (newClassObject != null) {
				final Map<Type, Type> typeMap = new HashMap<>();

				for (final Type genericType : genericTypes) {
					if (genericType instanceof ParameterizedType) {
						iterateTwoArrays(newClassObject.getTypeParameters(), ((ParameterizedType) genericType).getActualTypeArguments(), (type, mappedType) -> typeMap.put(type, genericClassTree.values().stream().map(previousTypeMap -> previousTypeMap.get(mappedType)).filter(Objects::nonNull).findFirst().orElse(mappedType)));
					}
				}

				genericClassTree.put(newClassObject, typeMap);
				walkClassTree(genericClassTree, newClassObject, getGenericTypes, getSuper);
			}
		}
	}

	private static <T> void appendIfNotEmpty(StringBuilder stringBuilder, T[] array, String prefix, String suffix, String delimiter, Function<T, String> callback) {
		if (array.length > 0) {
			stringBuilder.append(prefix);
			final List<String> dataList = new ArrayList<>();
			for (T data : array) {
				dataList.add(callback.apply(data));
			}
			stringBuilder.append(String.join(delimiter, dataList)).append(suffix);
		}
	}

	private static <T> T getOrReturn(Map<T, T> map, T data) {
		if (map == null) {
			return data;
		} else {
			return map.getOrDefault(data, data);
		}
	}

	private static <T, U> void iterateTwoArrays(T[] array1, U[] array2, BiConsumer<T, U> callback) {
		for (int i = 0; i < Math.min(array1.length, array2.length); i++) {
			callback.accept(array1[i], array2[i]);
		}
	}

	private static String getStringFromMethod(Consumer<StringBuilder> consumer) {
		final StringBuilder stringBuilder = new StringBuilder();
		consumer.accept(stringBuilder);
		return stringBuilder.toString();
	}

	private static String formatClassName(String className) {
		return className.replace("$", ".");
	}

	private static String serializeMethod(Executable executable) {
		final Type[] types = executable.getGenericParameterTypes();
		final String[] typesString = new String[types.length];
		for (int i = 0; i < types.length; i++) {
			typesString[i] = types[i].getTypeName();
		}
		return serializeMethod(executable.getModifiers(), executable.getName(), typesString);
	}

	private static String serializeMethod(int modifiers, String name, String... parameters) {
		return String.format("%s %s %s", modifiers, name, String.join(",", parameters));
	}

	static final class HolderInfo {

		private final String className;
		private final boolean abstractMapping;
		private final List<String> blacklist;
		private final Map<String, String> methodMap;
		private final JsonArray methodsArray;
		private static final Map<String, Map<String, String>> GLOBAL_METHOD_MAP = new HashMap<>();

		static {
			MethodMaps.setMethodMaps();
		}

		private HolderInfo(String className, boolean abstractMapping, String... blacklistMethods) {
			this.className = className;
			this.abstractMapping = abstractMapping;
			blacklist = Arrays.asList(blacklistMethods);
			methodMap = GLOBAL_METHOD_MAP.getOrDefault(className, new HashMap<>());
			methodsArray = new JsonArray();
		}

		private HolderInfo(HolderInfo holderInfo, String className, boolean abstractMapping) {
			this.className = className;
			this.abstractMapping = abstractMapping;
			blacklist = holderInfo.blacklist;
			methodMap = holderInfo.methodMap;
			methodsArray = abstractMapping ? new JsonArray() : holderInfo.methodsArray;
		}

		private String getMappedMethod(StringBuilder stringBuilder, String signatureKey, String methodName) {
			final String newMethodName1 = methodMap.get(String.format("%s|%s", methodName, signatureKey));
			if (newMethodName1 != null) {
				stringBuilder.append("@org.mtr.mapping.annotation.MappedMethod ");
				return newMethodName1;
			}

			final String newMethodName2 = methodMap.get(methodName);
			if (newMethodName2 == null) {
				stringBuilder.append("@Deprecated ");
				return methodName;
			} else {
				stringBuilder.append("@org.mtr.mapping.annotation.MappedMethod ");
				return newMethodName2;
			}
		}

		static void addMethodMap1(String className, String newMethodName, String... methods) {
			addMethodMap(className, newMethodName, methods);
			addMethodMap(className, newMethodName, newMethodName);
		}

		static void addMethodMap2(String className, String newMethodName, String signature, String... methods) {
			for (final String method : methods) {
				addMethodMap(className, newMethodName, String.format("%s|%s", method, signature));
			}
			addMethodMap(className, newMethodName, String.format("%s|%s", newMethodName, signature));
		}

		private static void addMethodMap(String className, String newMethodName, String... methods) {
			for (final String classNameSplit : className.split("\\|")) {
				GLOBAL_METHOD_MAP.computeIfAbsent(classNameSplit, methodMap -> new HashMap<>());
				final Map<String, String> methodMap = GLOBAL_METHOD_MAP.get(classNameSplit);
				for (final String method : methods) {
					methodMap.put(method, newMethodName);
				}
			}
		}
	}

	private enum ResolveState {
		RESOLVED("%1$s.data", "%1$s==null?null:%1$s.data"),
		RESOLVED_COLLECTION("org.mtr.mapping.tool.HolderBase.convertCollection(%1$s)", "%1$s==null?null:org.mtr.mapping.tool.HolderBase.convertCollection(%1$s)"),
		NONE("%s", "%s");

		private final String formatter;
		private final String formatterNullable;

		ResolveState(String formatter, String formatterNullable) {
			this.formatter = formatter;
			this.formatterNullable = formatterNullable;
		}

		private String format(String data, boolean nullable) {
			return String.format(nullable ? formatterNullable : formatter, data);
		}
	}
}
