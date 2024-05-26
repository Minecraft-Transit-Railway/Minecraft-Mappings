package org.mtr.mapping;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.jonafanho.apitools.ModId;
import com.jonafanho.apitools.ModLoader;
import com.jonafanho.apitools.ModProvider;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.gradle.api.Project;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public final class BuildTools {

	public final String minecraftVersion;
	public final String loader;
	public final int javaLanguageVersion;

	private final Path path;
	private final Path rootPath;
	private final String version;
	private final boolean isGeneratorProject;
	private final boolean isCommon;

	private static boolean needsSetup = true;
	private static final int MAPPINGS_TO_USE = 5; // Fabric 1.20.x

	public BuildTools(Project project, String generateProperty) throws IOException {
		path = project.getProjectDir().toPath();
		version = project.getVersion().toString();
		final String[] projectNameSplit = path.getFileName().toString().split("-");
		minecraftVersion = projectNameSplit[0];
		isCommon = minecraftVersion.equals("common");
		final int majorVersion = isCommon ? 0 : Integer.parseInt(minecraftVersion.split("\\.")[1]);
		javaLanguageVersion = majorVersion <= 16 ? 8 : majorVersion == 17 ? 16 : 17;
		isGeneratorProject = projectNameSplit.length > 1 && projectNameSplit[1].equals("generator");
		final Path parentPath = path.getParent();
		loader = parentPath.getFileName().toString();
		rootPath = isCommon ? parentPath : parentPath.getParent();
		final GenerationStatus generationStatus = generateProperty.isEmpty() ? GenerationStatus.NONE : generateProperty.equals("normal") ? GenerationStatus.GENERATE : GenerationStatus.DRY_RUN;

		if (generationStatus == GenerationStatus.GENERATE && needsSetup) {
			setup(rootPath);
		}

		if (!isGeneratorProject && !isCommon) {
			final Path testFolder = path.resolve("src/test/java/org/mtr/mapping/test");
			Files.createDirectories(testFolder);
			final String namespace = String.format("%s-%s", loader, minecraftVersion);
			final Path generatorTestFolder = parentPath.resolve(String.format("%s-generator/src/test/java/org/mtr/mapping/test", minecraftVersion));

			copyFile(rootPath, testFolder, path, "SearchForMappedMethodsTest", text -> generationStatus == GenerationStatus.NONE ? text.replace("@namespace@", namespace) : text);
			copyFile(rootPath, generatorTestFolder, path, "ClassScannerBase", text -> text.replace("@namespace@", namespace).replace("@generation@", generationStatus.toString()));
			copyFile(rootPath, generatorTestFolder, path, "ClassScannerCreateMaps", text -> text);
			copyFile(rootPath, generatorTestFolder, path, "ClassScannerGenerateHolders", text -> text);
			Files.deleteIfExists(generatorTestFolder.resolve("MethodMaps.java"));

			final Path directory = rootPath.resolve("build/mixins").resolve(loader).resolve(minecraftVersion);
			try {
				Files.createDirectories(directory);
				Files.write(
						directory.resolve("access-widener"),
						FileUtils.readFileToString(path.resolve("src/main/resources").resolve(loader.equals("fabric") ? "minecraft-mappings.accesswidener" : "accesstransformer.cfg").toFile(), StandardCharsets.UTF_8).getBytes(StandardCharsets.UTF_8),
						StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING
				);
			} catch (Exception e) {
				e.printStackTrace();
			}

			try (final Stream<Path> stream = Files.list(path.resolve("src/main/java/org/mtr/mapping/mixin"))) {
				stream.forEach(mixinPath -> {
					final String mixinFileName = mixinPath.getFileName().toString().split("\\.")[0];
					if (!mixinFileName.equals("package-info")) {
						try {
							Files.write(
									directory.resolve(mixinFileName),
									FileUtils.readFileToString(mixinPath.toFile(), StandardCharsets.UTF_8).replace("package org.mtr.mapping.mixin;", "package @package@;").getBytes(StandardCharsets.UTF_8),
									StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING
							);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
			}
		}

		if (isCommon) {
			final Path libraryPath = path.resolve("src/main/java/de/javagl/obj");
			try {
				FileUtils.copyURLToFile(new URL("https://github.com/javagl/Obj/archive/refs/heads/master.zip"), libraryPath.resolve("master.zip").toFile());
				try (final ZipInputStream zipInputStream = new ZipInputStream(Files.newInputStream(libraryPath.resolve("master.zip")))) {
					ZipEntry zipEntry = zipInputStream.getNextEntry();
					while (zipEntry != null) {
						final Path zipPath = Paths.get(zipEntry.getName());
						if (!zipEntry.isDirectory() && zipPath.startsWith("Obj-master/src/main/java/de/javagl/obj")) {
							final String fileName = zipPath.getFileName().toString();
							final String content = IOUtils.toString(zipInputStream, StandardCharsets.UTF_8);
							final String newContent;
							switch (fileName) {
								case "DefaultObj.java":
									newContent = appendAfter(
											content,
											"startedGroupNames.put(face, nextActiveGroupNames);", "startedMaterialGroupNames.put(face, activeMaterialGroupName);"
									);
									break;
								case "ObjReader.java":
									newContent = appendAfter(
											content,
											"ObjFaceParser objFaceParser = new ObjFaceParser();", "String groupOrObject = \"\";",
											"case \"g\":", "case \"o\": if (!groupOrObject.equals(identifier) && !groupOrObject.isEmpty()) break;",
											"output.setActiveGroupNames(Arrays.asList(groupNames));", "groupOrObject = identifier;"
									);
									break;
								default:
									newContent = content;
									break;
							}
							Files.write(libraryPath.resolve(fileName), newContent.getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
						}
						zipEntry = zipInputStream.getNextEntry();
					}
					zipInputStream.closeEntry();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public String getFabricVersion() {
		return getJson("https://meta.fabricmc.net/v2/versions/loader/" + minecraftVersion).getAsJsonArray().get(0).getAsJsonObject().getAsJsonObject("loader").get("version").getAsString();
	}

	public String getYarnVersion() {
		return getJson("https://meta.fabricmc.net/v2/versions/yarn/" + minecraftVersion).getAsJsonArray().get(0).getAsJsonObject().get("version").getAsString();
	}

	public String getFabricApiVersion() {
		final String modIdString = "fabric-api";
		return new ModId(modIdString, ModProvider.MODRINTH).getModFiles(minecraftVersion, ModLoader.FABRIC, "").get(0).fileName.split(".jar")[0].replace(modIdString + "-", "");
	}

	public String getForgeVersion() {
		if (minecraftVersion.equals("1.18.2")) {
			return "40.2.17"; // TODO 40.2.18 is not working
		}
		return getJson("https://files.minecraftforge.net/net/minecraftforge/forge/promotions_slim.json").getAsJsonObject().getAsJsonObject("promos").get(minecraftVersion + "-latest").getAsString();
	}

	public String getNeoForgeVersion() {
		final JsonArray jsonArray = getJson("https://maven.neoforged.net/api/maven/versions/releases/net/neoforged/neoforge").getAsJsonObject().getAsJsonArray("versions");
		final String[] latestVersion = {""};
		jsonArray.forEach(version -> {
			if (("1." + version.getAsString()).startsWith(minecraftVersion)) {
				latestVersion[0] = version.getAsString();
			}
		});
		return latestVersion[0];
	}

	public void copyBuildFile(boolean isDev) throws IOException {
		if (!isGeneratorProject) {
			final Path directory = rootPath.resolve("build/release");
			Files.createDirectories(directory);
			if (isCommon) {
				Files.copy(
						path.resolve(String.format("build/libs/common-%s.jar", version)),
						directory.resolve(String.format("Minecraft-Mappings-common-%s.jar", version)),
						StandardCopyOption.REPLACE_EXISTING
				);
			} else {
				Files.copy(
						path.resolve(String.format(loader.equals("fabric") && isDev ? "build/devlibs/%s-mapping-%s-dev.jar" : "build/libs/%s-mapping-%s.jar", minecraftVersion, version)),
						directory.resolve(String.format("Minecraft-Mappings-%s-%s-%s%s.jar", loader, minecraftVersion, version, isDev ? "-dev" : "")),
						StandardCopyOption.REPLACE_EXISTING
				);
			}
		}
	}

	private static void copyFile(Path rootPath, Path testFolder, Path projectPath, String fileName, Function<String, String> replacements) throws IOException {
		final String testFile = FileUtils.readFileToString(rootPath.resolve(String.format("common/src/test/java/org/mtr/mapping/test/%s.java", fileName)).toFile(), StandardCharsets.UTF_8);
		Files.write(testFolder.resolve(fileName + ".java"), replacements.apply(testFile).replace("@path@", projectPath.toString().replace("\\", "/")).getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
	}

	private static JsonElement getJson(String url) {
		for (int i = 0; i < 5; i++) {
			try {
				return JsonParser.parseString(IOUtils.toString(new URL(url), StandardCharsets.UTF_8));
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				Thread.sleep(1000);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return new JsonObject();
	}

	private static void setup(Path rootPath) throws IOException {
		final Map<String, Map<String, List<MethodInfo>>> allContent = new HashMap<>();
		final Path jsonPath = rootPath.resolve("build/existingMethods");
		final Path outputPath = rootPath.resolve("build/libraryMethods");
		Files.createDirectories(outputPath);
		final List<String> versions = new ArrayList<>();

		try (final DirectoryStream<Path> directoryStream = Files.newDirectoryStream(jsonPath)) {
			for (final Path path : directoryStream) {
				final String version = path.getFileName().toString();
				if (version.contains("-")) {
					versions.add(version);
					final JsonObject classesObject = JsonParser.parseString(FileUtils.readFileToString(path.toFile(), StandardCharsets.UTF_8)).getAsJsonObject();
					classesObject.keySet().forEach(className -> {
						allContent.computeIfAbsent(className, innerContent -> new HashMap<>());
						classesObject.getAsJsonArray(className).forEach(method -> {
							final MethodInfo methodInfo = new MethodInfo(method.getAsJsonObject(), version);
							allContent.get(className).computeIfAbsent(methodInfo.signature, methods -> new ArrayList<>());
							allContent.get(className).get(methodInfo.signature).add(methodInfo);
						});
					});
				}
			}
		}

		final int versionCount = versions.size();
		Collections.sort(versions);
		final JsonObject combinedObject = new JsonObject();

		final Map<String, Map<String, List<List<String>>>> additionalMap = new HashMap<>();
		final Map<String, Map<String, List<String>>> blacklistMap = new HashMap<>();
		MethodMaps.setMethodMaps(
				(className, newMethodName, methods) -> writeToMap(additionalMap, className, newMethodName, "", (methodsToAdd, list) -> list.add(methodsToAdd), methods),
				(className, newMethodName, signature, methods) -> writeToMap(additionalMap, className, newMethodName, signature, (methodsToAdd, list) -> list.add(methodsToAdd), methods),
				(className, method, signature) -> writeToMap(blacklistMap, className, method, signature, (methodsToAdd, list) -> list.addAll(methodsToAdd))
		);

		allContent.forEach((className, signatures) -> {
			final List<String> signaturesSorted = new ArrayList<>(signatures.keySet());
			Collections.sort(signaturesSorted);
			final List<String> fileContent = new ArrayList<>();
			fileContent.add(String.join(",", versions));
			final JsonObject classObject = new JsonObject();
			final JsonArray mappingsArray = new JsonArray();
			final JsonArray nullableArray = new JsonArray();
			classObject.add("mappings", mappingsArray);
			classObject.add("nullable", nullableArray);
			combinedObject.add(className, classObject);

			signaturesSorted.forEach(signature -> {
				final List<MethodInfo> methods = signatures.get(signature);
				final List<List<MethodInfo>> additionalMethodGroups = getAdditionalMethodGroups(methods, additionalMap, className, signature);
				final List<List<MethodInfo>> additionalMethodGroupsNoSignature = getAdditionalMethodGroups(methods, additionalMap, className, "");
				final List<String> blacklistNames = blacklistMap.getOrDefault(className, new HashMap<>()).getOrDefault(signature, new ArrayList<>());
				final boolean[] resolveNoSignature = {true};
				final Set<String> duplicateMappingCheck = new HashSet<>();

				additionalMethodGroups.forEach(additionalMethods -> {
					writeJson(mappingsArray, nullableArray, additionalMethods, 0, className, duplicateMappingCheck);
					additionalMethods.forEach(methods::remove);
				});

				while (true) {
					final Map<String, List<MethodInfo>> nameToVersionMap = new HashMap<>();
					final String[] names = new String[versionCount];

					methods.forEach(methodInfo -> {
						if (!blacklistNames.contains(methodInfo.name)) {
							nameToVersionMap.computeIfAbsent(methodInfo.name, versionSet -> new ArrayList<>());
							nameToVersionMap.get(methodInfo.name).add(methodInfo);
							names[versions.indexOf(methodInfo.version)] = methodInfo.name;
						}
					});

					final boolean[] end = {true};

					nameToVersionMap.forEach((name, methodsForName) -> {
						if (methodsForName.size() == versionCount) {
							writeJson(mappingsArray, nullableArray, methodsForName, MAPPINGS_TO_USE, className, duplicateMappingCheck);
							methodsForName.forEach(methods::remove);
							end[0] = false;
						}
					});

					if (resolveNoSignature[0]) {
						additionalMethodGroupsNoSignature.forEach(additionalMethodsNoSignature -> {
							writeJson(mappingsArray, nullableArray, additionalMethodsNoSignature, 0, className, duplicateMappingCheck);
							additionalMethodsNoSignature.forEach(methods::remove);
						});
						resolveNoSignature[0] = false;
						end[0] = false;
					}

					if (end[0]) {
						if (methods.size() == versionCount && Arrays.stream(names).noneMatch(Objects::isNull) && methods.stream().noneMatch(method -> blacklistNames.contains(method.name))) {
							writeJson(mappingsArray, nullableArray, methods, MAPPINGS_TO_USE, className, duplicateMappingCheck);
							methods.clear();
						}
						break;
					}
				}

				methods.sort(Comparator.comparing(methodInfo -> methodInfo.name));
				boolean isFirst = true;

				while (!methods.isEmpty()) {
					if (isFirst) {
						fileContent.add("");
					}

					final String[] row = new String[versionCount];
					Arrays.fill(row, "");
					int i = 0;

					while (i < methods.size()) {
						final int index = versions.indexOf(methods.get(i).version);
						if (row[index].isEmpty()) {
							row[index] = methods.remove(i).name;
						} else {
							i++;
						}
					}

					fileContent.add(String.format("%s%s", String.join(",", row), isFirst ? String.format(",,\"%s\"", signature) : ""));
					isFirst = false;
				}
			});

			try {
				Files.write(outputPath.resolve(className + ".csv"), fileContent, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
			} catch (IOException e) {
				e.printStackTrace();
			}
		});

		try {
			Files.write(jsonPath.resolve("combined.json"), combinedObject.toString().getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
		} catch (IOException e) {
			e.printStackTrace();
		}

		needsSetup = false;
	}

	private static void writeJson(JsonArray mappingsArray, JsonArray nullableArray, List<MethodInfo> methods, int mappingToUseIndex, String className, Set<String> duplicateMappingCheck) {
		if (methods.size() > mappingToUseIndex) {
			methods.sort(Comparator.comparing(methodInfo -> methodInfo.version));
			final String name = methods.get(mappingToUseIndex).name;
			final List<String> otherNames = methods.stream().map(methodInfo -> methodInfo.name).distinct().sorted().collect(Collectors.toList());
			final String signature = methods.get(0).signature;

			if (duplicateMappingCheck.stream().anyMatch(otherNames::contains)) {
				System.out.printf("Warning: Duplicate methods added for \"%s\" with signature \"%s\": %s%n", className, signature, String.join(",", otherNames));
			}
			duplicateMappingCheck.addAll(otherNames);

			otherNames.remove(name);
			final JsonArray namesArray = new JsonArray();
			namesArray.add(name);
			otherNames.forEach(namesArray::add);
			final JsonObject mappingObject = new JsonObject();
			mappingObject.add("names", namesArray);
			mappingObject.addProperty("signature", signature);
			mappingsArray.add(mappingObject);

			final int methodsCount = methods.get(0).parametersNullable.length;
			final boolean[] parametersNullable = methods.stream().map(methodInfo -> methodInfo.parametersNullable).reduce(new boolean[methodsCount], (parametersNullable1, parametersNullable2) -> {
				final boolean[] result = new boolean[methodsCount];
				for (int i = 0; i < methodsCount; i++) {
					result[i] = parametersNullable1[i] || parametersNullable2[i];
				}
				return result;
			});

			final JsonObject nullableObject = new JsonObject();
			nullableObject.add("names", namesArray);
			nullableObject.addProperty("signature", signature);
			final JsonArray parametersNullableArray = new JsonArray();
			for (final boolean parameterNullable : parametersNullable) {
				parametersNullableArray.add(parameterNullable);
			}
			nullableObject.add("parameters", parametersNullableArray);
			boolean returnNullable = false;
			for (final MethodInfo method : methods) {
				if (method.returnNullable) {
					returnNullable = true;
					break;
				}
			}
			nullableObject.addProperty("return", returnNullable);
			nullableArray.add(nullableObject);
		}
	}

	private static <T> void writeToMap(Map<String, Map<String, List<T>>> additionalMap, String className, String newMethodName, String signature, BiConsumer<List<String>, List<T>> addToMap, String... methods) {
		for (final String classNameSplit : className.split("\\|")) {
			additionalMap.computeIfAbsent(classNameSplit, innerContent -> new HashMap<>());
			additionalMap.get(classNameSplit).computeIfAbsent(signature, additionalMethods -> new ArrayList<>());
			final List<String> methodsCombined = new ArrayList<>();
			methodsCombined.add(newMethodName);
			methodsCombined.addAll(Arrays.asList(methods));
			addToMap.accept(methodsCombined, additionalMap.get(classNameSplit).get(signature));
		}
	}

	private static List<List<MethodInfo>> getAdditionalMethodGroups(List<MethodInfo> methods, Map<String, Map<String, List<List<String>>>> map, String className, String signature) {
		final List<List<String>> additionalMethodNameGroups = map.getOrDefault(className, new HashMap<>()).getOrDefault(signature, new ArrayList<>());
		final List<List<MethodInfo>> additionalMethodGroups = new ArrayList<>();
		additionalMethodNameGroups.forEach(additionalMethodNameGroup -> additionalMethodGroups.add(methods.stream().filter(method -> additionalMethodNameGroup.contains(method.name)).collect(Collectors.toList())));
		return additionalMethodGroups;
	}

	private static String appendAfter(String string, String... replacements) {
		String newString = string;
		for (int i = 1; i < replacements.length; i += 2) {
			newString = newString.replace(replacements[i - 1], replacements[i - 1] + replacements[i]);
		}
		return newString;
	}

	@FunctionalInterface
	public interface AddMethodMap1 {
		void add(String className, String newMethodName, String... methods);
	}

	@FunctionalInterface
	public interface AddMethodMap2 {
		void add(String className, String newMethodName, String signature, String... methods);
	}

	@FunctionalInterface
	public interface Blacklist {
		void add(String className, String method, String signature);
	}

	private static final class MethodInfo {

		private final String name;
		private final String signature;
		private final boolean[] parametersNullable;
		private final boolean returnNullable;
		private final String version;

		private MethodInfo(JsonObject jsonObject, String version) {
			name = jsonObject.get("name").getAsString();
			signature = jsonObject.get("signature").getAsString();
			final JsonArray jsonArray = jsonObject.getAsJsonArray("parametersNullable");
			if (jsonArray == null) {
				parametersNullable = new boolean[0];
			} else {
				parametersNullable = new boolean[jsonArray.size()];
				for (int i = 0; i < parametersNullable.length; i++) {
					parametersNullable[i] = jsonArray.get(i).getAsBoolean();
				}
			}
			returnNullable = jsonObject.get("returnNullable").getAsBoolean();
			this.version = version;
		}
	}

	private enum GenerationStatus {NONE, DRY_RUN, GENERATE}
}
