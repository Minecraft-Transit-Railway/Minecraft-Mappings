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
		Assumptions.assumeFalse(GENERATE_KEY.contains("@"));
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

		if (classObject.isEnum()) {
			mainStringBuilder.append("enum ").append(holderInfo.className).append("{");
			appendIfNotEmpty(mainStringBuilder, classObject.getEnumConstants(), "", "", ",", enumConstant -> String.format("%1$s(%2$s.%1$s)", ((Enum<?>) enumConstant).name(), staticClassName));
			mainStringBuilder.append(";public final ").append(staticClassName).append(" data;").append(holderInfo.className).append("(").append(staticClassName).append(" data){this.data=data;}");
		} else {
			mainStringBuilder.append(holderInfo.abstractMapping ? "abstract" : "final").append(" class ").append(holderInfo.className);
			appendGenerics(mainStringBuilder, classObject, true);
			final StringBuilder classNameStringBuilder = new StringBuilder(staticClassName);
			appendGenerics(classNameStringBuilder, classObject, false);
			mainStringBuilder.append(" extends ");
			final String className = classNameStringBuilder.toString();

			if (holderInfo.abstractMapping) {
				mainStringBuilder.append(className).append("{");
			} else {
				mainStringBuilder.append("org.mtr.mapping.tool.Dummy{public final ").append(className).append(" data;public ").append(holderInfo.className).append("(").append(className).append(" data){this.data=data;}");
			}

			final Map<Class<?>, Map<Type, Type>> classTree = walkClassTree(classObject);
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

			if (isValidExecutable(executable, originalParameterList, parameterList, superList, mappedSuperList, resolvedSignature, forceResolvedSignature, typeMap) && (!Modifier.isFinal(modifiers) && !Modifier.isAbstract(modifiers) || !holderInfo.abstractMapping) && !holderInfo.blacklist.contains(originalMethodName)) {
				final boolean isStatic = Modifier.isStatic(modifiers);
				final boolean isMethod = executable instanceof Method;
				final boolean generateExtraMethod = holderInfo.abstractMapping && !isStatic && isMethod;
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
				final boolean resolvedReturnType;

				if (isMethod) {
					final String methodCall = String.format("%s.%s%s", isStatic ? staticClassName : holderInfo.abstractMapping ? "super" : "this.data", originalMethodName, variables);

					if (isVoid) {
						mainStringBuilder.append(methodCall);
						resolvedReturnType = false;
					} else {
						mainStringBuilder.append("return ");
						final StringBuilder returnStringBuilder = new StringBuilder();
						if (appendGenerics(returnStringBuilder, returnTypeClass, typeMap, true, true, false)) {
							mainStringBuilder.append(appendWrap(returnTypeClass, returnStringBuilder.toString(), methodCall));
							resolvedReturnType = true;
						} else {
							mainStringBuilder.append(methodCall);
							resolvedReturnType = false;
						}
					}

					final StringBuilder forceResolvedReturnStringBuilder = new StringBuilder();
					appendGenerics(forceResolvedReturnStringBuilder, returnTypeClass, typeMap, true, false, true);
					final JsonObject methodObject = new JsonObject();
					methodObject.addProperty("name", originalMethodName);
					methodObject.addProperty("signature", String.format("%s %s(%s)", Modifier.toString(modifiers), forceResolvedReturnStringBuilder, String.join("|", forceResolvedSignature)));
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
			appendGenerics(originalParameterStringBuilder, type, typeMap, false, false, false);
			originalParameterList.add(String.format("%s %s", originalParameterStringBuilder, parameter.getName()));

			final StringBuilder parameterStringBuilder = new StringBuilder();
			final boolean isResolved = appendGenerics(parameterStringBuilder, type, typeMap, true, false, false);
			parameterList.add(String.format("%s %s", parameterStringBuilder, parameter.getName()));
			resolvedSignature.add(parameterStringBuilder.toString());

			final StringBuilder forceResolvedParameterStringBuilder = new StringBuilder();
			appendGenerics(forceResolvedParameterStringBuilder, type, typeMap, true, false, true);
			forceResolvedSignature.add(forceResolvedParameterStringBuilder.toString());

			superList.add(String.format("%s%s", parameter.getName(), isResolved ? ".data" : ""));
			final StringBuilder impliedParameterStringBuilder = new StringBuilder();
			appendGenerics(impliedParameterStringBuilder, type, typeMap, true, true, false);
			mappedSuperList.add(isResolved ? appendWrap(type, impliedParameterStringBuilder.toString(), parameter.getName()) : parameter.getName());

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

	private boolean appendGenerics(StringBuilder stringBuilder, Type type, Map<Type, Type> typeMap, boolean resolve, boolean impliedType, boolean forceResolveAll) {
		return appendGenerics(stringBuilder, type, typeMap, resolve, impliedType, forceResolveAll, true);
	}

	private boolean appendGenerics(StringBuilder stringBuilder, Type type, Map<Type, Type> typeMap, boolean resolve, boolean impliedType, boolean forceResolveAll, boolean isFirst) {
		final boolean isParameterized = type instanceof ParameterizedType;
		final Type mappedType = getOrReturn(typeMap, isParameterized ? ((ParameterizedType) type).getRawType() : type);
		final boolean isResolved;

		if ((forceResolveAll || isFirst) && mappedType instanceof Class) {
			final HolderInfo resolvedClassName = classMap.get(mappedType);
			isResolved = resolve && resolvedClassName != null;
			stringBuilder.append(isResolved ? resolvedClassName.className : formatClassName(mappedType.getTypeName()));
		} else {
			isResolved = false;
			stringBuilder.append(formatClassName(mappedType.getTypeName()));
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

		return isResolved;
	}

	private static void appendGenerics(StringBuilder stringBuilder, GenericDeclaration genericDeclaration, boolean getBounds) {
		appendIfNotEmpty(stringBuilder, genericDeclaration.getTypeParameters(), "<", ">", ",", typeVariable -> {
			if (getBounds) {
				final StringBuilder extendsStringBuilder = new StringBuilder();
				appendIfNotEmpty(extendsStringBuilder, typeVariable.getBounds(), " extends ", "", "&", Type::getTypeName);
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

			final Map<Type, Type> typeMap = new HashMap<>();

			if (genericType instanceof ParameterizedType) {
				iterateTwoArrays(superClassObject.getTypeParameters(), ((ParameterizedType) genericType).getActualTypeArguments(), (type, mappedType) -> typeMap.put(type, genericClassTree.values().stream().map(previousTypeMap -> previousTypeMap.get(mappedType)).filter(Objects::nonNull).findFirst().orElse(mappedType)));
			}

			genericClassTree.put(superClassObject, typeMap);
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
			addMethodMap1("Block", "createCuboidShape", "box");
			addMethodMap1("Block", "emitsRedstonePower", "isSignalSource");
			addMethodMap1("Block", "getBlockFromItem", "byItem");
			addMethodMap1("Block", "getComparatorOutput", "getAnalogOutputSignal");
			addMethodMap1("Block", "getFluidState");
			addMethodMap1("Block", "getInteractionShape", "getRaycastShape");
			addMethodMap1("Block", "getOcclusionShape", "getCullingShape");
			addMethodMap1("Block", "getOutlineShape", "getShape");
			addMethodMap1("Block", "getPlacementState", "getStateForPlacement");
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
			addMethodMap1("Block", "randomDisplayTick", "animateTick");
			addMethodMap1("Block", "replace", "updateOrDestroy");
			addMethodMap1("Block", "scheduledTick", "tick");
			addMethodMap1("Block", "shouldDropItemsOnExplosion", "dropFromExplosion");
			addMethodMap1("BlockEntity", "getPosition", "getBlockPos", "getPos");
			addMethodMap1("BlockEntity", "getWorld", "getLevel");
			addMethodMap1("BlockEntity", "markDirty", "setChanged");
			addMethodMap1("BlockEntity", "markRemoved", "setRemoved");
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
			addMethodMap1("BlockState", "hasProperty", "contains");
			addMethodMap1("BlockState", "with", "setValue");
			addMethodMap1("BooleanProperty", "create", "of");
			addMethodMap1("BooleanProperty", "getValues", "getPossibleValues");
			addMethodMap1("DirectionProperty", "getValues", "getPossibleValues");
			addMethodMap1("EnumProperty", "create", "of");
			addMethodMap1("EnumProperty", "getValues", "getPossibleValues");
			addMethodMap1("IntegerProperty", "create", "of");
			addMethodMap1("IntegerProperty", "getValues", "getPossibleValues");
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
			addMethodMap1("World", "breakBlock", "destroyBlock");
			addMethodMap1("World", "containsFluid", "containsAnyLiquid");
			addMethodMap1("World", "destroyBlockProgress", "setBlockBreakingInfo");
			addMethodMap1("World", "getBlockState");
			addMethodMap1("World", "getChunkAsView", "getChunkForCollisions");
			addMethodMap1("World", "getChunkManager", "getChunkSource");
			addMethodMap1("World", "getDismountHeight", "getBlockFloorHeight");
			addMethodMap1("World", "getEntityById", "getEntity");
			addMethodMap1("World", "getFluidState");
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
			addMethodMap1("World", "raycastBlock", "clipWithInteractionOverride");
			addMethodMap1("World", "removeBlock");
			addMethodMap1("World", "setBlockState", "setBlockAndUpdate", "setBlock");
			addMethodMap1("World", "spawnEntity", "addFreshEntity");
			addMethodMap1("World", "syncWorldEvent", "levelEvent");
			addMethodMap2("Block", "getPickStack", "BlockView|BlockPos|BlockState", "getCloneItemStack");
			addMethodMap2("Block", "rotate", "BlockState|Rotation");
			addMethodMap2("BlockPos", "offset", "Axis|int", "add", "relative");
			addMethodMap2("BlockPos", "offset", "Direction", "add", "relative");
			addMethodMap2("BlockPos", "offset", "Direction|int", "add", "relative");
			addMethodMap2("BlockPos", "offset", "int|int|int", "add", "relative");
			addMethodMap2("BlockPos", "offset", "long|Direction", "add", "relative");
			addMethodMap2("BlockPos", "offset", "long|int|int|int", "add", "relative");
			addMethodMap2("BlockPos", "offset", "Vector3i", "add", "relative");
			addMethodMap2("ChunkManager", "getWorldChunk", "int|int", "getChunkNow");
			addMethodMap2("ChunkManager", "getWorldChunk", "int|int|boolean", "getChunk");
			addMethodMap2("DirectionProperty", "create", "java.lang.String|java.lang.Class<T>", "of");
			addMethodMap2("DirectionProperty", "create", "java.lang.String|java.lang.Class<T>|java.util.Collection<T>", "of");
			addMethodMap2("DirectionProperty", "create", "java.lang.String|java.lang.Class<T>|java.util.function.Predicate<T>", "of");
			addMethodMap2("DirectionProperty", "create", "java.lang.String|java.lang.Class<T>|T[]", "of");
			addMethodMap2("DirectionProperty", "create", "java.lang.String|java.util.Collection<Direction>", "of");
			addMethodMap2("DirectionProperty", "create", "java.lang.String|java.util.function.Predicate<Direction>", "of");
			addMethodMap2("Item", "hasRecipeRemainder", "", "hasCraftingRemainingItem");
			addMethodMap2("Item", "isCorrectToolForDrops", "BlockState", "isSuitableFor");
			addMethodMap2("World", "getBlockEntity", "BlockPos");
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
			methodsArray = abstractMapping ? holderInfo.methodsArray : new JsonArray();
		}

		private String getMappedMethod(StringBuilder stringBuilder, String methodName, List<String> signature) {
			final String newMethodName1 = methodMap.get(methodName);
			if (newMethodName1 != null) {
				stringBuilder.append("@org.mtr.mapping.annotation.MappedMethod ");
				return newMethodName1;
			}

			final String newMethodName2 = methodMap.get(String.format("%s|%s", methodName, String.join("|", signature)));
			if (newMethodName2 == null) {
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
			GLOBAL_METHOD_MAP.computeIfAbsent(className, methodMap -> new HashMap<>());
			final Map<String, String> methodMap = GLOBAL_METHOD_MAP.get(className);
			for (final String method : methods) {
				methodMap.put(method, newMethodName);
			}
		}
	}
}
