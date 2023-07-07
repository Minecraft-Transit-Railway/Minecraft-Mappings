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
import java.util.stream.Collectors;

public final class GenerateHolders {

	private final Map<Class<?>, HolderInfo> classMap = new HashMap<>();
	private final JsonObject classesObject = new JsonObject();
	private static final Set<String> BLACKLISTED_SIGNATURES = Arrays.stream(Enum.class.getMethods()).map(GenerateHolders::serializeMethod).collect(Collectors.toSet());
	private static final Path PATH = Paths.get("@path@");
	private static final String NAMESPACE = "@namespace@";

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
		FileUtils.deleteDirectory(holdersPath.toFile());

		for (Map.Entry<Class<?>, HolderInfo> classEntry : classMap.entrySet()) {
			final HolderInfo holderInfo = classEntry.getValue();
			if (holderInfo.abstractMapping) {
				generate(holdersPath, classEntry.getKey(), new HolderInfo(holderInfo, holderInfo.className + "AbstractMapping", true));
				generate(holdersPath, classEntry.getKey(), new HolderInfo(holderInfo, holderInfo.className, false));
			} else {
				generate(holdersPath, classEntry.getKey(), holderInfo);
			}
		}

		final Path methodsPath = PATH.resolve("../../build/existingMethods");
		Files.createDirectories(methodsPath);
		Files.write(methodsPath.resolve(String.format("%s.json", NAMESPACE)), classesObject.toString().getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
	}

	private void generate(Path holdersPath, Class<?> classObject, HolderInfo holderInfo) throws IOException {
		final StringBuilder mainStringBuilder = new StringBuilder("package org.mtr.mapping.holder;public ");
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
			final StringBuilder classNameStringBuilder = new StringBuilder(staticClassName);
			appendGenerics(classNameStringBuilder, classObject, false, false);
			final String className = classNameStringBuilder.toString();

			if (holderInfo.abstractMapping) {
				mainStringBuilder.append(className).append("{");
			} else {
				mainStringBuilder.append("org.mtr.mapping.tool.HolderBase<").append(className).append(">{public ").append(holderInfo.className).append("(").append(className).append(" data){super(data);}@org.mtr.mapping.annotation.MappedMethod public static ");
				appendGenerics(mainStringBuilder, classObject, false, true);
				mainStringBuilder.append(holderInfo.className);
				appendGenerics(mainStringBuilder, classObject, false, false);
				mainStringBuilder.append(" cast(Object data){return new ").append(holderInfo.className);
				appendGenerics(mainStringBuilder, classObject, true, false);
				mainStringBuilder.append("((").append(className).append(")data);}");
			}

			processMethods(Modifier.isAbstract(classObject.getModifiers()) && !holderInfo.abstractMapping ? new Executable[0] : classObject.getConstructors(), mainStringBuilder, className, staticClassName, holderInfo, classTree);
			processMethods(classObject.getMethods(), mainStringBuilder, className, staticClassName, holderInfo, classTree);
		}

		mainStringBuilder.append("}");
		Files.createDirectories(holdersPath);
		Files.write(holdersPath.resolve(String.format("%s.java", holderInfo.className)), mainStringBuilder.toString().getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
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

			if (isValidExecutable(executable, originalParameterList, parameterList, superList, mappedSuperList, resolvedSignature, forceResolvedSignature, typeMap) && (!Modifier.isAbstract(modifiers) || !holderInfo.abstractMapping) && !holderInfo.blacklist.contains(originalMethodName)) {
				final boolean isStatic = Modifier.isStatic(modifiers);
				final boolean isFinal = Modifier.isFinal(modifiers);
				final boolean isMethod = executable instanceof Method;
				final boolean generateExtraMethod = holderInfo.abstractMapping && !isStatic && !isFinal && isMethod;
				final String methodName = String.format("%s%s", holderInfo.getMappedMethod(mainStringBuilder, originalMethodName, forceResolvedSignature), holderInfo.abstractMapping ? "2" : "");
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
				final ResolveState resolveState;

				if (isMethod) {
					final String methodCall = String.format("%s.%s%s", isStatic ? staticClassName : holderInfo.abstractMapping ? "super" : "this.data", originalMethodName, variables);

					if (isVoid) {
						mainStringBuilder.append(methodCall);
						resolveState = ResolveState.NONE;
					} else {
						mainStringBuilder.append("return ");
						final StringBuilder returnStringBuilder = new StringBuilder();
						resolveState = appendGenerics(returnStringBuilder, returnTypeClass, typeMap, true, true, false);
						mainStringBuilder.append(appendWrap(returnTypeClass, returnStringBuilder.toString(), methodCall, resolveState));
					}

					final StringBuilder forceResolvedReturnStringBuilder = new StringBuilder();
					appendGenerics(forceResolvedReturnStringBuilder, returnTypeClass, typeMap, true, false, true);
					final JsonObject methodObject = new JsonObject();
					methodObject.addProperty("name", originalMethodName);
					methodObject.addProperty("signature", String.format("%s %s(%s)", Modifier.toString(modifiers), forceResolvedReturnStringBuilder, String.join("|", forceResolvedSignature)));
					holderInfo.methodsArray.add(methodObject);
				} else {
					mainStringBuilder.append("super");
					if (holderInfo.abstractMapping) {
						mainStringBuilder.append(variables);
					} else {
						mainStringBuilder.append("(new ").append(className).append(variables).append(")");
					}
					resolveState = ResolveState.NONE;
				}

				mainStringBuilder.append(";}");

				if (generateExtraMethod) {
					mainStringBuilder.append("@Deprecated public final ");
					final Type returnTypeClass2 = appendMethodHeader(mainStringBuilder, executable, originalMethodName, false, originalParameterList, holderInfo.className, typeMap);
					mainStringBuilder.append(returnTypeClass2 == null || returnTypeClass2 == Void.TYPE ? "" : "return ").append(resolveState.format(String.format("%s(%s)", methodName, String.join(",", mappedSuperList)))).append(";}");
				}
			}
		}
	}

	private boolean isValidExecutable(Executable executable, List<String> originalParameterList, List<String> parameterList, List<String> superList, List<String> mappedSuperList, List<String> resolvedSignature, List<String> forceResolvedSignature, Map<Type, Type> typeMap) {
		if (!Modifier.isPublic(executable.getModifiers()) || executable.isSynthetic() || BLACKLISTED_SIGNATURES.contains(serializeMethod(executable))) {
			return false;
		}

		final boolean[] allPublicParameterTypes = {true};
		iterateTwoArrays(executable.getParameters(), executable.getGenericParameterTypes(), (parameter, type) -> {
			final StringBuilder originalParameterStringBuilder = new StringBuilder();
			appendGenerics(originalParameterStringBuilder, type, typeMap, false, false, false);
			originalParameterList.add(String.format("%s %s", originalParameterStringBuilder, parameter.getName()));

			final StringBuilder parameterStringBuilder = new StringBuilder();
			final ResolveState resolveState = appendGenerics(parameterStringBuilder, type, typeMap, true, false, false);
			parameterList.add(String.format("%s %s", parameterStringBuilder, parameter.getName()));
			resolvedSignature.add(parameterStringBuilder.toString());

			final StringBuilder forceResolvedParameterStringBuilder = new StringBuilder();
			appendGenerics(forceResolvedParameterStringBuilder, type, typeMap, true, false, true);
			forceResolvedSignature.add(forceResolvedParameterStringBuilder.toString());

			superList.add(resolveState.format(parameter.getName()));
			final StringBuilder impliedParameterStringBuilder = new StringBuilder();
			appendGenerics(impliedParameterStringBuilder, type, typeMap, true, true, false);
			mappedSuperList.add(appendWrap(type, impliedParameterStringBuilder.toString(), parameter.getName(), resolveState));

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
			appendGenerics(mainStringBuilder, returnTypeClass, typeMap, resolve, false, false);
			mainStringBuilder.append(" ").append(methodName);
		} else {
			returnTypeClass = null;
			mainStringBuilder.append(className);
		}

		mainStringBuilder.append("(").append(String.join(",", parameterList)).append(")");
		appendIfNotEmpty(mainStringBuilder, executable.getGenericExceptionTypes(), "throws ", "", ",", Type::getTypeName);
		mainStringBuilder.append("{");
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
			case RESOLVED_LIST:
			case RESOLVED_SET:
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

	private ResolveState appendGenerics(StringBuilder stringBuilder, Type type, Map<Type, Type> typeMap, boolean resolve, boolean impliedType, boolean forceResolveAll) {
		return appendGenerics(stringBuilder, type, typeMap, resolve, impliedType, forceResolveAll, true);
	}

	private ResolveState appendGenerics(StringBuilder stringBuilder, Type type, Map<Type, Type> typeMap, boolean resolve, boolean impliedType, boolean forceResolveAll, boolean isFirst) {
		final boolean isParameterized = type instanceof ParameterizedType;
		final Type mappedType = getOrReturn(typeMap, isParameterized ? ((ParameterizedType) type).getRawType() : type);
		final ResolveState resolveState;

		if ((forceResolveAll || isFirst) && mappedType instanceof Class) {
			final HolderInfo resolvedClassName = classMap.get(mappedType);
			final boolean isResolved = resolve && resolvedClassName != null;
			stringBuilder.append(isResolved ? resolvedClassName.className : formatClassName(mappedType.getTypeName()));
			final boolean isList = mappedType.equals(List.class);
			final boolean isSet = mappedType.equals(Set.class);
			final Type[] typeArguments = resolve && isParameterized && (isList || isSet) ? ((ParameterizedType) type).getActualTypeArguments() : new Type[0];

			if (typeArguments.length == 1 && typeArguments[0] instanceof Class) {
				final HolderInfo resolvedCollectionClassName = classMap.get(typeArguments[0]);
				if (resolvedCollectionClassName == null) {
					resolveState = ResolveState.NONE;
				} else {
					stringBuilder.append("<").append(resolvedCollectionClassName.className).append(">");
					return isList ? ResolveState.RESOLVED_LIST : ResolveState.RESOLVED_SET;
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
				appendIfNotEmpty(stringBuilder, ((ParameterizedType) type).getActualTypeArguments(), "<", ">", ",", innerType -> {
					final StringBuilder innerStringBuilder = new StringBuilder();
					appendGenerics(innerStringBuilder, innerType, typeMap, resolve, false, forceResolveAll, false);
					return innerStringBuilder.toString();
				});
			}
		}

		return resolveState;
	}

	private static void appendGenerics(StringBuilder stringBuilder, GenericDeclaration genericDeclaration, boolean impliedType, boolean getBounds) {
		appendIfNotEmpty(stringBuilder, genericDeclaration.getTypeParameters(), "<", ">", ",", typeVariable -> {
			if (impliedType) {
				return "";
			} else if (getBounds) {
				final StringBuilder extendsStringBuilder = new StringBuilder();
				appendIfNotEmpty(extendsStringBuilder, typeVariable.getBounds(), " extends ", "", "&", Type::getTypeName);
				return String.format("%s%s", typeVariable.getName(), extendsStringBuilder);
			} else {
				return typeVariable.getName();
			}
		});
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

	public static final class HolderInfo {

		private final String className;
		private final boolean abstractMapping;
		private final List<String> blacklist;
		private final Map<String, String> methodMap;
		private final JsonArray methodsArray;
		private static final Map<String, Map<String, String>> GLOBAL_METHOD_MAP = new HashMap<>();

		static {
			addMethodMap1("Block", "afterBreak", "playerDestroy");
			addMethodMap1("Block", "appendTooltip", "appendHoverText");
			addMethodMap1("Block", "asItem");
			addMethodMap1("Block", "createCuboidShape", "box");
			addMethodMap1("Block", "emitsRedstonePower", "isSignalSource");
			addMethodMap1("Block", "getBlockFromItem", "byItem");
			addMethodMap1("Block", "getCollisionShape");
			addMethodMap1("Block", "getComparatorOutput", "getAnalogOutputSignal");
			addMethodMap1("Block", "getDefaultState", "defaultBlockState");
			addMethodMap1("Block", "getFluidState");
			addMethodMap1("Block", "getInteractionShape", "getRaycastShape");
			addMethodMap1("Block", "getOcclusionShape", "getCullingShape");
			addMethodMap1("Block", "getOutlineShape", "getShape");
			addMethodMap1("Block", "getPlacementState", "getStateForPlacement");
			addMethodMap1("Block", "getRawIdFromState", "getId");
			addMethodMap1("Block", "getSidesShape", "getBlockSupportShape");
			addMethodMap1("Block", "getStateForNeighborUpdate", "updateShape");
			addMethodMap1("Block", "getStrongRedstonePower", "getDirectSignal");
			addMethodMap1("Block", "getVisualShape", "getCameraCollisionShape");
			addMethodMap1("Block", "getWeakRedstonePower", "getSignal");
			addMethodMap1("Block", "hasComparatorOutput", "hasAnalogOutputSignal");
			addMethodMap1("Block", "hasRandomTicks", "isRandomlyTicking");
			addMethodMap1("Block", "isFaceFullSquare", "isFaceFull");
			addMethodMap1("Block", "isSideInvisible", "skipRendering");
			addMethodMap1("Block", "mirror");
			addMethodMap1("Block", "neighborUpdate", "neighborChanged");
			addMethodMap1("Block", "onBlockBreakStart", "attack");
			addMethodMap1("Block", "onBreak", "playerWillDestroy");
			addMethodMap1("Block", "onBroken", "destroy");
			addMethodMap1("Block", "onDestroyedByExplosion", "wasExploded");
			addMethodMap1("Block", "onDestroyedByExplosion", "wasExploded");
			addMethodMap1("Block", "onEntityCollision", "entityInside");
			addMethodMap1("Block", "onEntityLand", "updateEntityAfterFallOn");
			addMethodMap1("Block", "onPlace", "onBlockAdded");
			addMethodMap1("Block", "onPlaced", "setPlacedBy");
			addMethodMap1("Block", "onRemove", "onStateReplaced");
			addMethodMap1("Block", "onUse", "use");
			addMethodMap1("Block", "randomDisplayTick", "animateTick");
			addMethodMap1("Block", "replace", "updateOrDestroy");
			addMethodMap1("Block", "scheduledTick", "tick");
			addMethodMap1("Block", "shouldDropItemsOnExplosion", "dropFromExplosion");
			addMethodMap1("BlockEntity", "getBlockPos", "getPos");
			addMethodMap1("BlockEntity", "getWorld", "getLevel");
			addMethodMap1("BlockEntity", "markDirty", "setChanged");
			addMethodMap1("BlockEntity", "markRemoved", "setRemoved");
			addMethodMap1("BlockHitResult", "createMissed", "miss");
			addMethodMap1("BlockHitResult", "getBlockPos");
			addMethodMap1("BlockHitResult", "getPos", "getLocation");
			addMethodMap1("BlockHitResult", "getSide", "getDirection");
			addMethodMap1("BlockHitResult", "isInsideBlock", "isInside");
			addMethodMap1("BlockHitResult", "squaredDistanceTo", "distanceTo");
			addMethodMap1("BlockHitResult", "withBlockPos", "withPosition");
			addMethodMap1("BlockHitResult", "withSide", "withDirection");
			addMethodMap1("BlockPos", "asLong");
			addMethodMap1("BlockPos", "asLong");
			addMethodMap1("BlockPos", "crossProduct", "cross");
			addMethodMap1("BlockPos", "down", "below");
			addMethodMap1("BlockPos", "east");
			addMethodMap1("BlockPos", "fromLong", "of");
			addMethodMap1("BlockPos", "getManhattanDistance", "distManhattan");
			addMethodMap1("BlockPos", "getX", "unpackLongX");
			addMethodMap1("BlockPos", "getX", "unpackLongX");
			addMethodMap1("BlockPos", "getY", "unpackLongY");
			addMethodMap1("BlockPos", "getY", "unpackLongY");
			addMethodMap1("BlockPos", "getZ", "unpackLongZ");
			addMethodMap1("BlockPos", "getZ", "unpackLongZ");
			addMethodMap1("BlockPos", "isWithinDistance", "closerThan", "closerToCenterThan");
			addMethodMap1("BlockPos", "north");
			addMethodMap1("BlockPos", "south");
			addMethodMap1("BlockPos", "toImmutable", "immutable");
			addMethodMap1("BlockPos", "up", "above");
			addMethodMap1("BlockPos", "west");
			addMethodMap1("BlockState", "get", "getValue");
			addMethodMap1("BlockState", "getBlock");
			addMethodMap1("BlockState", "hasProperty", "contains");
			addMethodMap1("BlockState", "updateNeighbors", "updateNeighbourShapes");
			addMethodMap1("BlockState", "with", "setValue");
			addMethodMap1("BlockView|World", "getBlockState");
			addMethodMap1("BlockView|World", "getDismountHeight", "getBlockFloorHeight");
			addMethodMap1("BlockView|World", "getFluidState");
			addMethodMap1("BlockView|World", "raycastBlock", "clipWithInteractionOverride");
			addMethodMap1("BooleanProperty", "create", "of");
			addMethodMap1("BooleanProperty|DirectionProperty|EnumProperty|IntegerProperty|Property", "getName", "name");
			addMethodMap1("BooleanProperty|DirectionProperty|EnumProperty|IntegerProperty|Property", "getValues", "getPossibleValues");
			addMethodMap1("CompoundTag", "asString", "getAsString");
			addMethodMap1("CompoundTag", "contains");
			addMethodMap1("CompoundTag", "containsUuid", "hasUUID");
			addMethodMap1("CompoundTag", "copy");
			addMethodMap1("CompoundTag", "copyFrom", "merge");
			addMethodMap1("CompoundTag", "equals");
			addMethodMap1("CompoundTag", "getBoolean");
			addMethodMap1("CompoundTag", "getByte");
			addMethodMap1("CompoundTag", "getByteArray");
			addMethodMap1("CompoundTag", "getCompound");
			addMethodMap1("CompoundTag", "getDouble");
			addMethodMap1("CompoundTag", "getFloat");
			addMethodMap1("CompoundTag", "getInt");
			addMethodMap1("CompoundTag", "getIntArray");
			addMethodMap1("CompoundTag", "getKeys", "getAllKeys");
			addMethodMap1("CompoundTag", "getLong");
			addMethodMap1("CompoundTag", "getLongArray");
			addMethodMap1("CompoundTag", "getShort");
			addMethodMap1("CompoundTag", "getSize", "size");
			addMethodMap1("CompoundTag", "getString");
			addMethodMap1("CompoundTag", "getUuid", "getUUID");
			addMethodMap1("CompoundTag", "isEmpty");
			addMethodMap1("CompoundTag", "putBoolean");
			addMethodMap1("CompoundTag", "putByte");
			addMethodMap1("CompoundTag", "putDouble");
			addMethodMap1("CompoundTag", "putFloat");
			addMethodMap1("CompoundTag", "putInt");
			addMethodMap1("CompoundTag", "putIntArray");
			addMethodMap1("CompoundTag", "putLong");
			addMethodMap1("CompoundTag", "putLongArray");
			addMethodMap1("CompoundTag", "putShort");
			addMethodMap1("CompoundTag", "putString");
			addMethodMap1("CompoundTag", "putUuid", "putUUID");
			addMethodMap1("CompoundTag", "remove");
			addMethodMap1("CompoundTag", "write");
			addMethodMap1("Direction", "asRotation", "toYRot");
			addMethodMap1("Direction", "byId", "from2DDataValue");
			addMethodMap1("Direction", "fromHorizontal", "from3DDataValue");
			addMethodMap1("Direction", "fromRotation", "fromYRot");
			addMethodMap1("Direction", "getAxis");
			addMethodMap1("Direction", "getFacing", "getNearest");
			addMethodMap1("Direction", "getHorizontal", "get2DDataValue");
			addMethodMap1("Direction", "getId", "get3DDataValue");
			addMethodMap1("Direction", "getOffsetX", "getStepX");
			addMethodMap1("Direction", "getOffsetY", "getStepY");
			addMethodMap1("Direction", "getOffsetZ", "getStepZ");
			addMethodMap1("Direction", "getOpposite");
			addMethodMap1("Direction", "getUnitVector", "step");
			addMethodMap1("Direction", "getVector", "getNormal");
			addMethodMap1("Direction", "pointsTo", "isFacingAngle", "method_30928");
			addMethodMap1("Entity|PlayerEntity|ServerPlayerEntity", "addScoreboardTag", "addCommandTag", "addTag");
			addMethodMap1("Entity|PlayerEntity|ServerPlayerEntity", "canBeSpectated", "broadcastToPlayer");
			addMethodMap1("Entity|PlayerEntity|ServerPlayerEntity", "canExplosionDestroyBlock", "shouldBlockExplode");
			addMethodMap1("Entity|PlayerEntity|ServerPlayerEntity", "getArmorItems", "getArmorSlots");
			addMethodMap1("Entity|PlayerEntity|ServerPlayerEntity", "getBlockPos", "blockPosition");
			addMethodMap1("Entity|PlayerEntity|ServerPlayerEntity", "getBodyY", "getY");
			addMethodMap1("Entity|PlayerEntity|ServerPlayerEntity", "getDismountLocationForPassenger", "updatePassengerForDismount");
			addMethodMap1("Entity|PlayerEntity|ServerPlayerEntity", "getEntityName", "getScoreboardName");
			addMethodMap1("Entity|PlayerEntity|ServerPlayerEntity", "getEntityWorld", "getCommandSenderWorld");
			addMethodMap1("Entity|PlayerEntity|ServerPlayerEntity", "getEyeY");
			addMethodMap1("Entity|PlayerEntity|ServerPlayerEntity", "getHandItems", "getHandSlots", "getItemsHand");
			addMethodMap1("Entity|PlayerEntity|ServerPlayerEntity", "getHeightOffset", "getMyRidingOffset");
			addMethodMap1("Entity|PlayerEntity|ServerPlayerEntity", "getHorizontalFacing", "getDirection");
			addMethodMap1("Entity|PlayerEntity|ServerPlayerEntity", "getItemsEquipped", "getAllSlots");
			addMethodMap1("Entity|PlayerEntity|ServerPlayerEntity", "getMountedHeightOffset", "getPassengersRidingOffset");
			addMethodMap1("Entity|PlayerEntity|ServerPlayerEntity", "getMovementDirection", "getMotionDirection");
			addMethodMap1("Entity|PlayerEntity|ServerPlayerEntity", "getParticleX", "getRandomX");
			addMethodMap1("Entity|PlayerEntity|ServerPlayerEntity", "getParticleZ", "getRandomZ");
			addMethodMap1("Entity|PlayerEntity|ServerPlayerEntity", "getRandomBodyY", "getRandomY");
			addMethodMap1("Entity|PlayerEntity|ServerPlayerEntity", "getRootVehicle");
			addMethodMap1("Entity|PlayerEntity|ServerPlayerEntity", "getScoreboardTags", "getCommandTags", "getTags");
			addMethodMap1("Entity|PlayerEntity|ServerPlayerEntity", "getSoundCategory", "getSoundSource");
			addMethodMap1("Entity|PlayerEntity|ServerPlayerEntity", "getSwimHeight", "getFluidJumpThreshold", "method_29241");
			addMethodMap1("Entity|PlayerEntity|ServerPlayerEntity", "getType");
			addMethodMap1("Entity|PlayerEntity|ServerPlayerEntity", "getUuid", "getUUID");
			addMethodMap1("Entity|PlayerEntity|ServerPlayerEntity", "getUuidAsString", "getStringUUID");
			addMethodMap1("Entity|PlayerEntity|ServerPlayerEntity", "getVehicle");
			addMethodMap1("Entity|PlayerEntity|ServerPlayerEntity", "hasPermissionLevel", "hasPermissions");
			addMethodMap1("Entity|PlayerEntity|ServerPlayerEntity", "interact", "interactOn");
			addMethodMap1("Entity|PlayerEntity|ServerPlayerEntity", "interactAt");
			addMethodMap1("Entity|PlayerEntity|ServerPlayerEntity", "kill");
			addMethodMap1("Entity|PlayerEntity|ServerPlayerEntity", "offsetX", "getX");
			addMethodMap1("Entity|PlayerEntity|ServerPlayerEntity", "offsetZ", "getZ");
			addMethodMap1("Entity|PlayerEntity|ServerPlayerEntity", "onPlayerCollision", "playerTouch");
			addMethodMap1("Entity|PlayerEntity|ServerPlayerEntity", "removeScoreboardTag", "removeTag");
			addMethodMap1("Entity|PlayerEntity|ServerPlayerEntity", "setBodyYaw", "setYBodyRot");
			addMethodMap1("Entity|PlayerEntity|ServerPlayerEntity", "setHeadYaw", "setYHeadRot");
			addMethodMap1("Entity|PlayerEntity|ServerPlayerEntity", "shouldRender", "shouldRenderAtSqrDistance");
			addMethodMap1("Entity|PlayerEntity|ServerPlayerEntity", "squaredDistanceTo", "distanceToSqr");
			addMethodMap1("Entity|PlayerEntity|ServerPlayerEntity", "startRiding");
			addMethodMap1("Entity|PlayerEntity|ServerPlayerEntity", "stopRiding");
			addMethodMap1("Entity|PlayerEntity|ServerPlayerEntity", "tick");
			addMethodMap1("Entity|PlayerEntity|ServerPlayerEntity", "tickRiding", "rideTick");
			addMethodMap1("EnumProperty", "create", "of");
			addMethodMap1("IntegerProperty", "create", "of");
			addMethodMap1("Item", "appendTooltip", "appendHoverText");
			addMethodMap1("Item", "asItem");
			addMethodMap1("Item", "byRawId", "byId");
			addMethodMap1("Item", "canMine", "canAttackBlock");
			addMethodMap1("Item", "finishUsing", "finishUsingItem");
			addMethodMap1("Item", "fromBlock", "byBlock");
			addMethodMap1("Item", "getDefaultStack", "getDefaultInstance");
			addMethodMap1("Item", "getRawId", "getId");
			addMethodMap1("Item", "hasGlint", "isFoil");
			addMethodMap1("Item", "inventoryTick");
			addMethodMap1("Item", "isEnchantable");
			addMethodMap1("Item", "isFood", "isEdible");
			addMethodMap1("Item", "isNbtSynced", "shouldOverrideMultiplayerNbt", "shouldSyncTagToClient");
			addMethodMap1("Item", "isNetworkSynced", "isComplex");
			addMethodMap1("Item", "onCraft", "onCraftedBy");
			addMethodMap1("Item", "onStoppedUsing", "releaseUsing");
			addMethodMap1("Item", "postMine", "mineBlock");
			addMethodMap1("Item", "usageTick", "onUseTick");
			addMethodMap1("Item", "useOnBlock", "useOn");
			addMethodMap1("Item", "useOnEntity", "interactLivingEntity");
			addMethodMap1("ItemPlacementContext", "canPlace");
			addMethodMap1("ItemPlacementContext", "canReplaceExisting", "replacingClickedOnBlock");
			addMethodMap1("ItemPlacementContext", "getBlockPos", "getClickedPos");
			addMethodMap1("ItemPlacementContext", "getClickedFace", "getSide");
			addMethodMap1("ItemPlacementContext", "getHand");
			addMethodMap1("ItemPlacementContext", "getHitPos", "getClickLocation");
			addMethodMap1("ItemPlacementContext", "getPlayer");
			addMethodMap1("ItemPlacementContext", "getPlayerFacing", "getHorizontalPlayerFacing", "getHorizontalDirection");
			addMethodMap1("ItemPlacementContext", "getPlayerLookDirection", "getNearestLookingDirection");
			addMethodMap1("ItemPlacementContext", "getPlayerYaw", "getRotation");
			addMethodMap1("ItemPlacementContext", "getStack", "getItemInHand");
			addMethodMap1("ItemPlacementContext", "getWorld", "getLevel");
			addMethodMap1("ItemPlacementContext", "hitsInsideBlock", "isInside");
			addMethodMap1("ItemPlacementContext", "offset", "at");
			addMethodMap1("ItemPlacementContext", "shouldCancelInteraction", "isSecondaryUseActive");
			addMethodMap1("MutableText", "asOrderedText", "getVisualOrderText");
			addMethodMap1("MutableText", "getString", "asTruncatedString");
			addMethodMap1("PacketBuffer", "array");
			addMethodMap1("PacketBuffer", "arrayOffset");
			addMethodMap1("PacketBuffer", "asReadOnly");
			addMethodMap1("PacketBuffer", "bytesBefore");
			addMethodMap1("PacketBuffer", "capacity");
			addMethodMap1("PacketBuffer", "copy");
			addMethodMap1("PacketBuffer", "ensureWritable");
			addMethodMap1("PacketBuffer", "forEachByte");
			addMethodMap1("PacketBuffer", "forEachByteDesc");
			addMethodMap1("PacketBuffer", "getBoolean");
			addMethodMap1("PacketBuffer", "getByte");
			addMethodMap1("PacketBuffer", "getBytes");
			addMethodMap1("PacketBuffer", "getChar");
			addMethodMap1("PacketBuffer", "getCharSequence");
			addMethodMap1("PacketBuffer", "getDouble");
			addMethodMap1("PacketBuffer", "getDoubleLE");
			addMethodMap1("PacketBuffer", "getFloat");
			addMethodMap1("PacketBuffer", "getFloatLE");
			addMethodMap1("PacketBuffer", "getInt");
			addMethodMap1("PacketBuffer", "getIntLE");
			addMethodMap1("PacketBuffer", "getMedium");
			addMethodMap1("PacketBuffer", "getMediumLE");
			addMethodMap1("PacketBuffer", "getUnsignedMedium");
			addMethodMap1("PacketBuffer", "getUnsignedMediumLE");
			addMethodMap1("PacketBuffer", "getUnsignedShort");
			addMethodMap1("PacketBuffer", "getUnsignedShortLE");
			addMethodMap1("PacketBuffer", "hasArray");
			addMethodMap1("PacketBuffer", "hashCode");
			addMethodMap1("PacketBuffer", "hasMemoryAddress");
			addMethodMap1("PacketBuffer", "indexOf");
			addMethodMap1("PacketBuffer", "isDirect");
			addMethodMap1("PacketBuffer", "isReadable");
			addMethodMap1("PacketBuffer", "isReadOnly");
			addMethodMap1("PacketBuffer", "isWritable");
			addMethodMap1("PacketBuffer", "maxCapacity");
			addMethodMap1("PacketBuffer", "maxWritableBytes");
			addMethodMap1("PacketBuffer", "readableBytes");
			addMethodMap1("PacketBuffer", "readBlockHitResult");
			addMethodMap1("PacketBuffer", "readBlockHitResult");
			addMethodMap1("PacketBuffer", "readBlockPos");
			addMethodMap1("PacketBuffer", "readBoolean");
			addMethodMap1("PacketBuffer", "readByte");
			addMethodMap1("PacketBuffer", "readByteArray");
			addMethodMap1("PacketBuffer", "readBytes");
			addMethodMap1("PacketBuffer", "readChar");
			addMethodMap1("PacketBuffer", "readCharSequence");
			addMethodMap1("PacketBuffer", "readDate");
			addMethodMap1("PacketBuffer", "readDouble");
			addMethodMap1("PacketBuffer", "readDoubleLE");
			addMethodMap1("PacketBuffer", "readEnumConstant", "readEnum");
			addMethodMap1("PacketBuffer", "readerIndex");
			addMethodMap1("PacketBuffer", "readFloat");
			addMethodMap1("PacketBuffer", "readFloatLE");
			addMethodMap1("PacketBuffer", "readIdentifier", "readResourceLocation");
			addMethodMap1("PacketBuffer", "readInt");
			addMethodMap1("PacketBuffer", "readIntArray", "readVarIntArray");
			addMethodMap1("PacketBuffer", "readIntLE");
			addMethodMap1("PacketBuffer", "readItemStack", "readItem");
			addMethodMap1("PacketBuffer", "readLong");
			addMethodMap1("PacketBuffer", "readLongLE");
			addMethodMap1("PacketBuffer", "readMedium");
			addMethodMap1("PacketBuffer", "readMediumLE");
			addMethodMap1("PacketBuffer", "readRetainedSlice");
			addMethodMap1("PacketBuffer", "readShort");
			addMethodMap1("PacketBuffer", "readShortLE");
			addMethodMap1("PacketBuffer", "readSlice");
			addMethodMap1("PacketBuffer", "readString", "readUtf");
			addMethodMap1("PacketBuffer", "readUnlimitedNbt", "readAnySizeNbt");
			addMethodMap1("PacketBuffer", "readUnsignedByte");
			addMethodMap1("PacketBuffer", "readUnsignedInt");
			addMethodMap1("PacketBuffer", "readUnsignedIntLE");
			addMethodMap1("PacketBuffer", "readUnsignedMedium");
			addMethodMap1("PacketBuffer", "readUnsignedMediumLE");
			addMethodMap1("PacketBuffer", "readUnsignedShort");
			addMethodMap1("PacketBuffer", "readUnsignedShortLE");
			addMethodMap1("PacketBuffer", "readUuid", "readUUID");
			addMethodMap1("PacketBuffer", "readVarInt");
			addMethodMap1("PacketBuffer", "readVarLong");
			addMethodMap1("PacketBuffer", "refCnt");
			addMethodMap1("PacketBuffer", "release");
			addMethodMap1("PacketBuffer", "retain");
			addMethodMap1("PacketBuffer", "setBoolean");
			addMethodMap1("PacketBuffer", "setBytes");
			addMethodMap1("PacketBuffer", "setCharSequence");
			addMethodMap1("PacketBuffer", "setDouble");
			addMethodMap1("PacketBuffer", "setDoubleLE");
			addMethodMap1("PacketBuffer", "setFloat");
			addMethodMap1("PacketBuffer", "setFloatLE");
			addMethodMap1("PacketBuffer", "skipBytes");
			addMethodMap1("PacketBuffer", "writableBytes");
			addMethodMap1("PacketBuffer", "writeBlockHitResult");
			addMethodMap1("PacketBuffer", "writeBlockPos");
			addMethodMap1("PacketBuffer", "writeBoolean");
			addMethodMap1("PacketBuffer", "writeByte");
			addMethodMap1("PacketBuffer", "writeByteArray");
			addMethodMap1("PacketBuffer", "writeBytes");
			addMethodMap1("PacketBuffer", "writeChar");
			addMethodMap1("PacketBuffer", "writeCharSequence");
			addMethodMap1("PacketBuffer", "writeDate");
			addMethodMap1("PacketBuffer", "writeDouble");
			addMethodMap1("PacketBuffer", "writeDoubleLE");
			addMethodMap1("PacketBuffer", "writeEnumConstant", "writeEnum");
			addMethodMap1("PacketBuffer", "writeFloat");
			addMethodMap1("PacketBuffer", "writeFloatLE");
			addMethodMap1("PacketBuffer", "writeIdentifier", "writeResourceLocation");
			addMethodMap1("PacketBuffer", "writeInt");
			addMethodMap1("PacketBuffer", "writeIntArray", "writeVarIntArray");
			addMethodMap1("PacketBuffer", "writeIntLE");
			addMethodMap1("PacketBuffer", "writeLong");
			addMethodMap1("PacketBuffer", "writeLongArray");
			addMethodMap1("PacketBuffer", "writeLongLE");
			addMethodMap1("PacketBuffer", "writeMedium");
			addMethodMap1("PacketBuffer", "writeMediumLE");
			addMethodMap1("PacketBuffer", "writeResourceLocation", "writeIdentifier");
			addMethodMap1("PacketBuffer", "writerIndex");
			addMethodMap1("PacketBuffer", "writeShort");
			addMethodMap1("PacketBuffer", "writeShortLE");
			addMethodMap1("PacketBuffer", "writeString", "writeUtf");
			addMethodMap1("PacketBuffer", "writeUuid", "writeUUID");
			addMethodMap1("PacketBuffer", "writeVarInt");
			addMethodMap1("PacketBuffer", "writeVarLong");
			addMethodMap1("PacketBuffer", "writeZero");
			addMethodMap1("PlayerEntity|ServerPlayerEntity", "isCreative");
			addMethodMap1("Vector3d", "crossProduct", "cross");
			addMethodMap1("Vector3d", "distanceTo");
			addMethodMap1("Vector3d", "dotProduct", "dot");
			addMethodMap1("Vector3d", "floorAlongAxes", "align");
			addMethodMap1("Vector3d", "getComponentAlongAxis", "get");
			addMethodMap1("Vector3d", "getX", "x");
			addMethodMap1("Vector3d", "getY", "y");
			addMethodMap1("Vector3d", "getZ", "z");
			addMethodMap1("Vector3d", "isInRange", "closerThan");
			addMethodMap1("Vector3d", "length");
			addMethodMap1("Vector3d", "lengthSquared", "lengthSqr");
			addMethodMap1("Vector3d", "multiply", "scale");
			addMethodMap1("Vector3d", "negate", "reverse");
			addMethodMap1("Vector3d", "normalize");
			addMethodMap1("Vector3d", "of", "atLowerCornerOf");
			addMethodMap1("Vector3d", "ofBottomCenter", "atBottomCenterOf");
			addMethodMap1("Vector3d", "ofCenter", "atCenterOf");
			addMethodMap1("Vector3d", "ofCenter", "upFromBottomCenterOf");
			addMethodMap1("Vector3d", "relativize", "vectorTo");
			addMethodMap1("Vector3d", "rotateX", "xRot");
			addMethodMap1("Vector3d", "rotateY", "yRot");
			addMethodMap1("Vector3d", "rotateZ", "zRot");
			addMethodMap1("Vector3d", "squaredDistanceTo", "distanceToSqr");
			addMethodMap1("Vector3d", "subtract");
			addMethodMap1("Vector3d", "unpackRgb", "fromRGB24");
			addMethodMap1("Vector3f", "getX", "x");
			addMethodMap1("Vector3f", "getY", "y");
			addMethodMap1("Vector3f", "getZ", "z");
			addMethodMap1("Vector3i", "crossProduct", "cross");
			addMethodMap1("Vector3i", "down", "above");
			addMethodMap1("Vector3i", "getComponentAlongAxis", "get");
			addMethodMap1("Vector3i", "getManhattanDistance", "distManhattan");
			addMethodMap1("Vector3i", "isWithinDistance", "closerThan", "closerToCenterThan");
			addMethodMap1("Vector3i", "up", "below");
			addMethodMap1("VoxelShapes", "empty");
			addMethodMap1("VoxelShapes", "fullCube", "block");
			addMethodMap1("World", "breakBlock", "destroyBlock");
			addMethodMap1("World", "containsFluid", "containsAnyLiquid");
			addMethodMap1("World", "destroyBlockProgress", "setBlockBreakingInfo");
			addMethodMap1("World", "getChunkAsView", "getChunkForCollisions");
			addMethodMap1("World", "getChunkManager", "getChunkSource");
			addMethodMap1("World", "getEntityById", "getEntity");
			addMethodMap1("World", "getLunarTime", "dayTime");
			addMethodMap1("World", "getNextMapId", "getFreeMapId");
			addMethodMap1("World", "getPlayerByUuid", "getPlayerByUUID");
			addMethodMap1("World", "getRandom");
			addMethodMap1("World", "getRandomPosInChunk", "getBlockRandomPos");
			addMethodMap1("World", "getScoreboard");
			addMethodMap1("World", "getServer");
			addMethodMap1("World", "getTime", "getGameTime");
			addMethodMap1("World", "getTopPosition", "getHeightmapPos");
			addMethodMap1("World", "isAir", "isEmptyBlock");
			addMethodMap1("World", "isClient", "isClientSide");
			addMethodMap1("World", "isDay");
			addMethodMap1("World", "isDirectionSolid", "loadedAndEntityCanStandOnFace");
			addMethodMap1("World", "isEmittingRedstonePower", "hasSignal");
			addMethodMap1("World", "isNight");
			addMethodMap1("World", "isPlayerInRange", "hasNearbyAlivePlayer");
			addMethodMap1("World", "isRaining");
			addMethodMap1("World", "isSavingDisabled", "noSave");
			addMethodMap1("World", "isThundering");
			addMethodMap1("World", "isTopSolid", "loadedAndEntityCanStandOn");
			addMethodMap1("World", "isWater", "isWaterAt");
			addMethodMap1("World", "removeBlock");
			addMethodMap1("World", "setBlockState", "setBlockAndUpdate", "setBlock");
			addMethodMap1("World", "spawnEntity", "addFreshEntity");
			addMethodMap1("World", "syncWorldEvent", "levelEvent");
			addMethodMap1("World", "updateNeighbors", "updateNeighborsAt");
			addMethodMap2("Block", "getPickStack", "BlockView|BlockPos|BlockState", "getCloneItemStack");
			addMethodMap2("Block", "rotate", "BlockState|Rotation");
			addMethodMap2("BlockPos", "offset", "Axis|int", "add", "relative");
			addMethodMap2("BlockPos", "offset", "Direction", "add", "relative");
			addMethodMap2("BlockPos", "offset", "Direction|int", "add", "relative");
			addMethodMap2("BlockPos", "offset", "int|int|int", "add", "relative");
			addMethodMap2("BlockPos", "offset", "long|Direction", "add", "relative");
			addMethodMap2("BlockPos", "offset", "long|int|int|int", "add", "relative");
			addMethodMap2("BlockPos", "offset", "Vector3i", "add", "relative");
			addMethodMap2("BlockState", "canBeReplaced", "ItemPlacementContext", "canReplace");
			addMethodMap2("BlockState", "isOf", "Block", "is");
			addMethodMap2("BlockView|World", "getBlockEntity", "BlockPos");
			addMethodMap2("ChunkManager", "getWorldChunk", "int|int", "getChunkNow");
			addMethodMap2("ChunkManager", "getWorldChunk", "int|int|boolean", "getChunk");
			addMethodMap2("CompoundTag", "putByteArray", "java.lang.String|byte[]");
			addMethodMap2("Direction", "rotateYClockwise", "", "getClockWise");
			addMethodMap2("Direction", "rotateYCounterclockwise", "", "getCounterClockWise");
			addMethodMap2("DirectionProperty", "create", "java.lang.String|java.lang.Class<T>", "of");
			addMethodMap2("DirectionProperty", "create", "java.lang.String|java.lang.Class<T>|java.util.Collection<T>", "of");
			addMethodMap2("DirectionProperty", "create", "java.lang.String|java.lang.Class<T>|java.util.function.Predicate<T>", "of");
			addMethodMap2("DirectionProperty", "create", "java.lang.String|java.lang.Class<T>|T[]", "of");
			addMethodMap2("DirectionProperty", "create", "java.lang.String|java.util.Collection<Direction>", "of");
			addMethodMap2("DirectionProperty", "create", "java.lang.String|java.util.function.Predicate<Direction>", "of");
			addMethodMap2("Entity|PlayerEntity|ServerPlayerEntity", "getPitch", "float", "getViewXRot");
			addMethodMap2("Entity|PlayerEntity|ServerPlayerEntity", "getPosition", "", "getPos", "position");
			addMethodMap2("Entity|PlayerEntity|ServerPlayerEntity", "getYaw", "float", "getViewYRot");
			addMethodMap2("Item", "hasRecipeRemainder", "", "hasCraftingRemainingItem");
			addMethodMap2("Item", "isCorrectToolForDrops", "BlockState", "isSuitableFor");
			addMethodMap2("MutableText", "append", "java.lang.String");
			addMethodMap2("MutableText", "formatted", "TextFormatting", "withStyle");
			addMethodMap2("PacketBuffer", "readLongArray", "long[]");
			addMethodMap2("PacketBuffer", "readLongArray", "long[]|int");
			addMethodMap2("PacketBuffer", "writeItemStack", "ItemStack", "writeItem");
			addMethodMap2("PlayerEntity|ServerPlayerEntity", "isHolding", "Item");
			addMethodMap2("PlayerEntity|ServerPlayerEntity", "sendMessage", "Text|boolean", "displayClientMessage");
			addMethodMap2("Vector3d", "add", "double|double|double");
			addMethodMap2("Vector3d", "add", "Vector3d");
			addMethodMap2("Vector3i", "getSquaredDistance", "Vector3i", "distSqr");
			addMethodMap2("Vector3i", "offset", "Direction|int", "relative");
			addMethodMap2("VoxelShapes", "union", "VoxelShape|VoxelShape", "or");
			addMethodMap2("World", "getClosestPlayer", "double|double|double|double|boolean", "getNearestPlayer");
			addMethodMap2("World", "getClosestPlayer", "double|double|double|double|java.util.function.Predicate", "getNearestPlayer");
			addMethodMap2("World", "getPlayers", "", "players");
			addMethodMap2("World", "isChunkLoaded", "int|int", "hasChunk");
			addMethodMap2("World", "playSound", "double|double|double|SoundEvent|SoundCategory|float|float|boolean", "playLocalSound", "playSoundFromEntity");
			addMethodMap2("World", "playSound", "PlayerEntity|BlockPos|SoundEvent|SoundCategory|float|float", "playLocalSound", "playSoundFromEntity");
			addMethodMap2("World", "playSound", "PlayerEntity|double|double|double|SoundEvent|SoundCategory|float|float", "playLocalSound", "playSoundFromEntity");
			addMethodMap2("World", "playSound", "PlayerEntity|Entity|SoundEvent|SoundCategory|float|float", "playLocalSound", "playSoundFromEntity");
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

		private String getMappedMethod(StringBuilder stringBuilder, String methodName, List<String> signature) {
			final String newMethodName1 = methodMap.get(methodName);
			if (newMethodName1 != null) {
				stringBuilder.append("@org.mtr.mapping.annotation.MappedMethod ");
				return newMethodName1;
			}

			final String newMethodName2 = methodMap.get(String.format("%s|%s", methodName, String.join("|", signature)));
			if (newMethodName2 == null) {
				stringBuilder.append("@Deprecated ");
				return methodName;
			} else {
				stringBuilder.append("@org.mtr.mapping.annotation.MappedMethod ");
				return newMethodName2;
			}
		}

		private static void addMethodMap1(String className, String newMethodName, String... methods) {
			addMethodMap(className, newMethodName, methods);
			addMethodMap(className, newMethodName, newMethodName);
		}

		private static void addMethodMap2(String className, String newMethodName, String signature, String... methods) {
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
		RESOLVED("%s.data"), RESOLVED_LIST("org.mtr.mapping.tool.HolderBase.convertCollection(%s)"), RESOLVED_SET("org.mtr.mapping.tool.HolderBase.convertCollection(%s)"), NONE("%s");

		private final String formatter;

		ResolveState(String formatter) {
			this.formatter = formatter;
		}

		private String format(String data) {
			return String.format(formatter, data);
		}
	}
}
