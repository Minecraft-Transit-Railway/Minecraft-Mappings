package org.mtr.mapping;

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

public final class BuildTools {

	public final String minecraftVersion;
	public final String loader;
	public final int javaLanguageVersion;

	private final Path path;
	private final Path rootPath;
	private final String version;
	private final boolean isGenerator;
	private final boolean isCommon;

	private static boolean needsSetup = true;
	private static final List<String> GLOBAL_METHOD_MAP = new ArrayList<>();
	private static final int MAPPINGS_TO_USE = 4; // Fabric 1.20.x

	public BuildTools(Project project, String generateHolders) throws IOException {
		path = project.getProjectDir().toPath();
		version = project.getVersion().toString();
		final String[] projectNameSplit = path.getFileName().toString().split("-");
		minecraftVersion = projectNameSplit[0];
		isCommon = minecraftVersion.equals("common");
		final int majorVersion = isCommon ? 0 : Integer.parseInt(minecraftVersion.split("\\.")[1]);
		javaLanguageVersion = majorVersion <= 16 ? 8 : majorVersion == 17 ? 16 : 17;
		isGenerator = projectNameSplit.length > 1 && projectNameSplit[1].equals("generator");
		final Path parentPath = path.getParent();
		loader = parentPath.getFileName().toString();
		rootPath = isCommon ? parentPath : parentPath.getParent();
		final boolean skipGenerate = generateHolders.isEmpty();
		final boolean shouldSetup = generateHolders.equals("normal");

		if (shouldSetup && needsSetup) {
			setup(rootPath);
		}

		if (!isGenerator && !isCommon) {
			final Path testFolder = path.resolve("src/test/java/org/mtr/mapping/test");
			Files.createDirectories(testFolder);
			final String namespace = String.format("%s-%s", loader, minecraftVersion);

			final String testFile = FileUtils.readFileToString(rootPath.resolve("common/src/test/java/org/mtr/mapping/test/SearchForMappedMethodsTest.java").toFile(), StandardCharsets.UTF_8);
			final String newTestFile = skipGenerate ? testFile.replace("@namespace@", namespace) : testFile;
			Files.write(testFolder.resolve("SearchForMappedMethodsTest.java"), newTestFile.getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

			final String generateFile = FileUtils.readFileToString(rootPath.resolve("common/src/test/java/org/mtr/mapping/test/GenerateHolders.java").toFile(), StandardCharsets.UTF_8);
			final String newGenerateFile = skipGenerate ? generateFile : generateFile.replace("@path@", path.toString().replace("\\", "/")).replace("@namespace@", namespace).replace("@writeFiles@", shouldSetup ? "true" : "false");
			Files.write(parentPath.resolve(String.format("%s-generator/src/test/java/org/mtr/mapping/test/GenerateHolders.java", minecraftVersion)), newGenerateFile.getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

			if (shouldSetup) {
				final StringBuilder mainStringBuilder = new StringBuilder("package org.mtr.mapping.test;public interface MethodMaps {static void setMethodMaps() {");
				final List<String> methods = new ArrayList<>();
				int i = 0;
				while (i < GLOBAL_METHOD_MAP.size()) {
					mainStringBuilder.append("setMethodMaps").append(i).append("();");
					final StringBuilder stringBuilder = new StringBuilder("static void setMethodMaps").append(i).append("(){");
					int j = 0;
					while (j < 100 && i < GLOBAL_METHOD_MAP.size()) {
						stringBuilder.append(GLOBAL_METHOD_MAP.get(i));
						i++;
						j++;
					}
					stringBuilder.append("}");
					methods.add(stringBuilder.toString());
				}
				mainStringBuilder.append("}");
				methods.forEach(mainStringBuilder::append);
				mainStringBuilder.append("}");
				Files.write(parentPath.resolve(String.format("%s-generator/src/test/java/org/mtr/mapping/test/MethodMaps.java", minecraftVersion)), mainStringBuilder.toString().getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
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
		return getJson("https://files.minecraftforge.net/net/minecraftforge/forge/promotions_slim.json").getAsJsonObject().getAsJsonObject("promos").get(minecraftVersion + "-latest").getAsString();
	}

	public void copyBuildFile() throws IOException {
		if (!isGenerator) {
			final Path directory = rootPath.resolve("build/release");
			Files.createDirectories(directory);
			Files.copy(
					path.resolve(isCommon ? String.format("build/libs/common-%s.jar", version) : String.format(loader.equals("fabric") ? "build/devlibs/%s-mapping-%s-dev.jar" : "build/libs/%s-mapping-%s.jar", minecraftVersion, version)),
					directory.resolve(isCommon ? String.format("Minecraft-Mappings-common-%s.jar", version) : String.format("Minecraft-Mappings-%s-%s-%s.jar", loader, minecraftVersion, version)),
					StandardCopyOption.REPLACE_EXISTING
			);
		}
	}

	static void addMethodMap1(String className, String newMethodName, String... methods) {
		addMethodMap(String.format("1(\"%s\",\"%s\"", className, newMethodName), methods);
	}

	static void addMethodMap2(String className, String newMethodName, String signature, String... methods) {
		addMethodMap(String.format("2(\"%s\",\"%s\",\"%s\"", className, newMethodName, signature), methods);
	}

	static void blacklist(String className, String method, String signature) {
		GLOBAL_METHOD_MAP.removeIf(map -> map.startsWith(String.format("GenerateHolders.HolderInfo.addMethodMap2(\"%s\",\"%s\",\"%s\"", className, method, signature)));
	}

	private static void addMethodMap(String prefix, String... methods) {
		final StringBuilder stringBuilder = new StringBuilder();
		if (methods.length > 0) {
			stringBuilder.append(",\"");
		}
		stringBuilder.append(String.join("\",\"", methods));
		if (methods.length > 0) {
			stringBuilder.append("\"");
		}
		GLOBAL_METHOD_MAP.add(String.format("GenerateHolders.HolderInfo.addMethodMap%s%s);", prefix, stringBuilder));
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
		final Path outputPath = rootPath.resolve("build/libraryMethods");
		Files.createDirectories(outputPath);
		final List<String> versions = new ArrayList<>();

		try (final DirectoryStream<Path> directoryStream = Files.newDirectoryStream(rootPath.resolve("build/existingMethods"))) {
			for (Path path : directoryStream) {
				final JsonObject classesObject = JsonParser.parseString(FileUtils.readFileToString(path.toFile(), StandardCharsets.UTF_8)).getAsJsonObject();
				final String version = path.getFileName().toString();
				versions.add(version);

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

		final int versionCount = versions.size();
		Collections.sort(versions);

		allContent.forEach((className, signatures) -> signatures.forEach((signature, methods) -> {
			while (true) {
				final Map<String, Set<MethodInfo>> nameToVersionMap = new HashMap<>();
				final String[] names = new String[versionCount];

				methods.forEach(methodInfo -> {
					nameToVersionMap.computeIfAbsent(methodInfo.name, versionSet -> new HashSet<>());
					nameToVersionMap.get(methodInfo.name).add(methodInfo);
					names[versions.indexOf(methodInfo.version)] = methodInfo.name;
				});

				boolean end = true;

				for (final Map.Entry<String, Set<MethodInfo>> nameMethods : nameToVersionMap.entrySet()) {
					final Set<MethodInfo> methodsForName = nameMethods.getValue();
					if (methodsForName.size() == versionCount) {
						addMethodMap2(className, nameMethods.getKey(), signature);
						methodsForName.forEach(methods::remove);
						end = false;
					}
				}

				if (end) {
					if (methods.size() == versionCount && Arrays.stream(names).noneMatch(Objects::isNull)) {
						final List<String> namesList = new ArrayList<>();
						for (final String name : names) {
							if (!namesList.contains(name)) {
								namesList.add(name);
							}
						}
						namesList.remove(names[MAPPINGS_TO_USE]);
						Collections.sort(namesList);
						addMethodMap2(className, names[MAPPINGS_TO_USE], signature, namesList.toArray(new String[0]));
						methods.clear();
					}
					break;
				}
			}
		}));

		for (final Map.Entry<String, Map<String, List<MethodInfo>>> innerContent : allContent.entrySet()) {
			final List<String> fileContent = new ArrayList<>();
			fileContent.add(String.join(",", versions));
			final List<String> signatures = new ArrayList<>(innerContent.getValue().keySet());
			Collections.sort(signatures);

			signatures.forEach(signature -> {
				final List<MethodInfo> methods = innerContent.getValue().get(signature);
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

			Files.write(outputPath.resolve(String.format("%s.csv", innerContent.getKey())), fileContent, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
		}

		MethodMaps.setMethodMaps();
		needsSetup = false;
	}

	private static final class MethodInfo {

		private final String name;
		private final String signature;
		private final String version;

		private MethodInfo(JsonObject jsonObject, String version) {
			name = jsonObject.get("name").getAsString();
			signature = jsonObject.get("signature").getAsString();
			this.version = version;
		}
	}
}
