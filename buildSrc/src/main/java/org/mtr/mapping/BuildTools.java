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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;

public class BuildTools {

	public final String minecraftVersion;
	public final String loader;
	public final int javaLanguageVersion;

	private final Path path;
	private final Path rootPath;
	private final String version;
	private final boolean isGenerator;

	public BuildTools(Project project, boolean generateHolders) throws IOException {
		path = project.getProjectDir().toPath();
		version = project.getVersion().toString();
		final String[] projectNameSplit = path.getFileName().toString().split("-");
		minecraftVersion = projectNameSplit[0];
		final int majorVersion = Integer.parseInt(minecraftVersion.split("\\.")[1]);
		javaLanguageVersion = majorVersion <= 16 ? 8 : majorVersion == 17 ? 16 : 17;
		isGenerator = projectNameSplit.length > 1 && projectNameSplit[1].equals("generator");
		final Path parentPath = path.getParent();
		loader = parentPath.getFileName().toString();
		rootPath = parentPath.getParent();

		if (!isGenerator) {
			final Path testFolder = path.resolve("src/test/java/org/mtr/mapping/test");
			Files.createDirectories(testFolder);
			final String namespace = String.format("%s-%s", loader, minecraftVersion);

			final String testFile = FileUtils.readFileToString(rootPath.resolve("common/src/test/java/org/mtr/mapping/test/SearchForMappedMethodsTest.java").toFile(), StandardCharsets.UTF_8);
			final String newTestFile = generateHolders ? testFile : testFile.replace("@namespace@", namespace);
			Files.write(testFolder.resolve("SearchForMappedMethodsTest.java"), newTestFile.getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

			final String generateFile = FileUtils.readFileToString(rootPath.resolve("common/src/test/java/org/mtr/mapping/test/GenerateHolders.java").toFile(), StandardCharsets.UTF_8);
			final String newGenerateFile = generateHolders ? generateFile.replace("@generate@", "").replace("@path@", path.toString().replace("\\", "/")).replace("@namespace@", namespace) : generateFile;
			Files.write(parentPath.resolve(String.format("%s-generator/src/test/java/org/mtr/mapping/test/GenerateHolders.java", minecraftVersion)), newGenerateFile.getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
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
			Files.copy(path.resolve(String.format(loader.equals("fabric") ? "build/devlibs/%s-mapping-%s-dev.jar" : "build/libs/%s-mapping-%s.jar", minecraftVersion, version)), directory.resolve(String.format("Minecraft-Mappings-%s-%s-%s.jar", loader, minecraftVersion, version)), StandardCopyOption.REPLACE_EXISTING);
		}
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
}
