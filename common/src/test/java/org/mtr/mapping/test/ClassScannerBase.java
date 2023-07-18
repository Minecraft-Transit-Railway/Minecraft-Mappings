package org.mtr.mapping.test;

import org.junit.jupiter.api.Assumptions;

import javax.annotation.Nullable;
import java.lang.reflect.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class ClassScannerBase {

	private final Map<Class<?>, ClassInfo> classMap = new HashMap<>();

	static final Path PATH = Paths.get("@path@");
	static final Path NAMESPACE = Paths.get("@namespace@");
	private static final GenerationStatus GENERATION_STATUS = GenerationStatus.valueOf("@generation@");
	private static final Set<String> BLACKLISTED_SIGNATURES = Arrays.stream(Enum.class.getMethods()).map(ClassScannerBase::quickSerialize).collect(Collectors.toSet());

	static {
		BLACKLISTED_SIGNATURES.add(quickSerialize(Modifier.PUBLIC | Modifier.STATIC, "values"));
		BLACKLISTED_SIGNATURES.add(quickSerialize(Modifier.PUBLIC | Modifier.STATIC, "valueOf", "java.lang.String"));
	}

	final void put(String newClassName, Class<?> minecraftClassObject, String... blacklistMethods) {
		classMap.put(minecraftClassObject, new ClassInfo(newClassName, false, minecraftClassObject.isEnum(), blacklistMethods));
	}

	final void putAbstract(String newClassName, Class<?> minecraftClassObject, String... blacklistMethods) {
		classMap.put(minecraftClassObject, new ClassInfo(newClassName, true, minecraftClassObject.isEnum(), blacklistMethods));
	}

	final void generate() {
		preScan();
		classMap.forEach((minecraftClassObject, classInfo) -> {
			generate(minecraftClassObject, classInfo);
			if (classInfo.isAbstractMapping) {
				generate(minecraftClassObject, new ClassInfo(classInfo));
			}
		});
		postScan();
	}

	private void generate(Class<?> minecraftClassObject, ClassInfo classInfo) {
		final String minecraftClassName = formatClassName(minecraftClassObject.getName());
		final Map<Class<?>, Map<Type, Type>> classTree = new HashMap<>();
		walkClassTree(classTree, minecraftClassObject, classObject1 -> new Type[]{classObject1.getGenericSuperclass()}, classObject1 -> new Class<?>[]{classObject1.getSuperclass()});
		walkClassTree(classTree, minecraftClassObject, Class::getGenericInterfaces, Class::getInterfaces);

		iterateClass(
				classInfo,
				minecraftClassName,
				getGenerics(minecraftClassObject, false, true),
				getGenerics(minecraftClassObject, false, false),
				getGenerics(minecraftClassObject, true, false),
				getStringFromMethod(stringBuilder -> appendIfNotEmpty(stringBuilder, minecraftClassObject.getEnumConstants(), "", "", ",", enumConstant -> String.format("%1$s(%2$s.%1$s)", ((Enum<?>) enumConstant).name(), minecraftClassName)))
		);

		iterateExecutables(Modifier.isAbstract(minecraftClassObject.getModifiers()) && !classInfo.isAbstractMapping ? new Executable[0] : minecraftClassObject.getConstructors(), classTree, classInfo, minecraftClassName);
		iterateExecutables(minecraftClassObject.getMethods(), classTree, classInfo, minecraftClassName);
		postIterateClass(classInfo);
	}

	private void iterateExecutables(Executable[] executables, Map<Class<?>, Map<Type, Type>> classTree, ClassInfo classInfo, String minecraftClassName) {
		for (final Executable executable : executables) {
			final Map<Type, Type> typeMap = classTree.get(executable.getDeclaringClass());
			final String minecraftMethodName = formatClassName(executable.getName());
			final boolean isMethod = executable instanceof Method;
			final int modifiers = executable.getModifiers();

			if (Modifier.isPublic(modifiers) && !Modifier.isNative(modifiers) && !executable.isSynthetic() && (!Modifier.isAbstract(modifiers) || !classInfo.isAbstractMapping) && !BLACKLISTED_SIGNATURES.contains(quickSerialize(executable)) && !classInfo.blacklist.contains(minecraftMethodName) && Arrays.stream(executable.getParameters()).allMatch(parameter -> Modifier.isPublic(parameter.getType().getModifiers()))) {
				final String generics = getGenerics(executable, false, true);
				final String exceptions = getStringFromMethod(stringBuilder -> appendIfNotEmpty(stringBuilder, executable.getGenericExceptionTypes(), "throws ", "", ",", Type::getTypeName));
				final List<TypeInfo> parameters = new ArrayList<>();

				iterateTwoArrays(executable.getParameters(), executable.getGenericParameterTypes(), (parameter, type) -> {
					final StringBuilder stringBuilderResolved = new StringBuilder();
					final boolean resolved = getMappedClassName(stringBuilderResolved, type, typeMap, classMap, false);
					parameters.add(new TypeInfo(
							parameter.getName(),
							getStringFromMethod(stringBuilder -> getMappedClassName(stringBuilder, type, typeMap, null, false)),
							getStringFromMethod(stringBuilder -> getMappedClassName(stringBuilder, type, typeMap, null, true)),
							stringBuilderResolved.toString(),
							getStringFromMethod(stringBuilder -> getMappedClassName(stringBuilder, type, typeMap, classMap, true)),
							resolved,
							parameter.getType().isEnum(),
							parameter.isAnnotationPresent(Nullable.class)
					));
				});

				final String resolvedReturnType;
				final TypeInfo returnType;

				if (isMethod) {
					final StringBuilder stringBuilderResolvedImplied = new StringBuilder();
					final boolean isReturnResolved = getMappedClassName(stringBuilderResolvedImplied, ((Method) executable).getGenericReturnType(), typeMap, classMap, true);
					resolvedReturnType = getStringFromMethod(stringBuilder -> getMappedClassName(stringBuilder, ((Method) executable).getGenericReturnType(), typeMap, classMap, false));
					returnType = new TypeInfo(
							null,
							getStringFromMethod(stringBuilder -> getMappedClassName(stringBuilder, ((Method) executable).getGenericReturnType(), typeMap, null, false)),
							getStringFromMethod(stringBuilder -> getMappedClassName(stringBuilder, ((Method) executable).getGenericReturnType(), typeMap, null, true)),
							resolvedReturnType,
							stringBuilderResolvedImplied.toString(),
							isReturnResolved,
							((Method) executable).getReturnType().isEnum(),
							executable.isAnnotationPresent(Nullable.class)
					);
				} else {
					resolvedReturnType = "";
					returnType = new TypeInfo(null, resolvedReturnType, resolvedReturnType, resolvedReturnType, resolvedReturnType, false, false, false);
				}

				final String key = mergeWithSpaces(Modifier.toString(modifiers), generics, resolvedReturnType, String.format("(%s)", parameters.stream().map(typeInfo -> typeInfo.resolvedTypeName).collect(Collectors.joining(","))), exceptions);
				iterateExecutable(classInfo, minecraftClassName, minecraftMethodName, isMethod, Modifier.isStatic(modifiers), Modifier.isFinal(modifiers), Modifier.toString(modifiers & ~Modifier.ABSTRACT & ~Modifier.TRANSIENT), generics, returnType, parameters, exceptions, key);
			}
		}
	}

	abstract void preScan();

	abstract void iterateClass(ClassInfo classInfo, String minecraftClassName, String genericsWithBounds, String generics, String genericsImplied, String enumValues);

	abstract void iterateExecutable(ClassInfo classInfo, String minecraftClassName, String minecraftMethodName, boolean isMethod, boolean isStatic, boolean isFinal, String modifiers, String generics, TypeInfo returnType, List<TypeInfo> parameters, String exceptions, String key);

	abstract void postIterateClass(ClassInfo classInfo);

	abstract void postScan();

	static ClassScannerBase getInstance() {
		Assumptions.assumeFalse(GENERATION_STATUS == GenerationStatus.NONE);
		return GENERATION_STATUS == GenerationStatus.GENERATE ? new ClassScannerGenerateHolders() : new ClassScannerCreateMaps();
	}

	static <T> void appendIfNotEmpty(StringBuilder stringBuilder, T[] array, String prefix, String suffix, String delimiter, Function<T, String> callback) {
		if (array != null && array.length > 0) {
			stringBuilder.append(prefix);
			final List<String> dataList = new ArrayList<>();
			for (T data : array) {
				dataList.add(callback.apply(data));
			}
			stringBuilder.append(String.join(delimiter, dataList)).append(suffix);
		}
	}

	static String getStringFromMethod(Consumer<StringBuilder> consumer) {
		final StringBuilder stringBuilder = new StringBuilder();
		consumer.accept(stringBuilder);
		return stringBuilder.toString();
	}

	private static boolean getMappedClassName(StringBuilder stringBuilder, Type type, Map<Type, Type> typeMap, Map<Class<?>, ClassInfo> classMap, boolean impliedType) {
		final boolean isParameterized = type instanceof ParameterizedType;
		final Type mappedType = getOrReturn(typeMap, isParameterized ? ((ParameterizedType) type).getRawType() : type);
		final boolean isResolved;

		if (classMap != null && mappedType instanceof Class) {
			final ClassInfo resolvedClassName = classMap.get(mappedType);
			isResolved = resolvedClassName != null;
			stringBuilder.append(isResolved ? resolvedClassName.className : formatClassName(mappedType.getTypeName()));
		} else {
			isResolved = false;
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
				appendIfNotEmpty(stringBuilder, ((ParameterizedType) type).getActualTypeArguments(), "<", ">", ",", innerType -> getStringFromMethod(innerStringBuilder -> getMappedClassName(innerStringBuilder, innerType, typeMap, null, false)));
			}
		}

		return isResolved;
	}

	private static String getGenerics(GenericDeclaration genericDeclaration, boolean impliedType, boolean getBounds) {
		return getStringFromMethod(stringBuilder -> appendIfNotEmpty(stringBuilder, genericDeclaration.getTypeParameters(), "<", ">", ",", typeVariable -> {
			if (impliedType) {
				return "";
			} else if (getBounds) {
				return String.format("%s%s", typeVariable.getName(), getStringFromMethod(extendsStringBuilder -> appendIfNotEmpty(extendsStringBuilder, typeVariable.getBounds(), " extends ", "", "&", Type::getTypeName)));
			} else {
				return typeVariable.getName();
			}
		}));
	}

	/**
	 * Generates a mapping of inherited generic types and what they are actually mapped to
	 */
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

	private static <T, U> void iterateTwoArrays(T[] array1, U[] array2, BiConsumer<T, U> callback) {
		for (int i = 0; i < Math.min(array1.length, array2.length); i++) {
			callback.accept(array1[i], array2[i]);
		}
	}

	private static String formatClassName(String className) {
		return className.replace("$", ".");
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

	private static String mergeWithSpaces(String... strings) {
		final List<String> list = new ArrayList<>();
		for (final String string : strings) {
			if (!string.isEmpty()) {
				list.add(string);
			}
		}
		return String.join(" ", list);
	}

	private static <T> T getOrReturn(Map<T, T> map, T data) {
		if (map == null) {
			return data;
		} else {
			return map.getOrDefault(data, data);
		}
	}

	static final class ClassInfo {

		final StringBuilder stringBuilder = new StringBuilder();
		final String className;
		final boolean isAbstractMapping;
		final boolean isEnum;
		private final List<String> blacklist;

		private ClassInfo(String className, boolean isAbstractMapping, boolean isEnum, String... blacklistMethods) {
			this.className = className;
			this.isAbstractMapping = isAbstractMapping;
			this.isEnum = isEnum;
			blacklist = Arrays.asList(blacklistMethods);
		}

		private ClassInfo(ClassInfo classInfo) {
			className = classInfo.className;
			isAbstractMapping = false;
			isEnum = classInfo.isEnum;
			blacklist = classInfo.blacklist;
		}
	}

	static final class TypeInfo {

		final String variableName;
		final String minecraftTypeName;
		final String minecraftTypeNameImplied;
		final String resolvedTypeName;
		final String resolvedTypeNameImplied;
		final boolean isResolved;
		final boolean isEnum;
		final boolean isNullable;

		private TypeInfo(String variableName, String minecraftTypeName, String minecraftTypeNameImplied, String resolvedTypeName, String resolvedTypeNameImplied, boolean isResolved, boolean isEnum, boolean isNullable) {
			this.variableName = variableName;
			this.minecraftTypeName = minecraftTypeName;
			this.minecraftTypeNameImplied = minecraftTypeNameImplied;
			this.resolvedTypeName = resolvedTypeName;
			this.resolvedTypeNameImplied = resolvedTypeNameImplied;
			this.isResolved = isResolved;
			this.isEnum = isEnum;
			this.isNullable = isNullable;
		}
	}

	private enum GenerationStatus {NONE, DRY_RUN, GENERATE}
}
