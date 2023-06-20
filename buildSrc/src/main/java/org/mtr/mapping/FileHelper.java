package org.mtr.mapping;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public final class FileHelper {

	public static void copyTest(File file, String token) throws IOException {
		final String testFile = FileUtils.readFileToString(new File(String.format("%s/../../common/src/test/java/org/mtr/mapping/test/SearchForMappedMethodsTest.java", file)), StandardCharsets.UTF_8);
		FileUtils.write(new File(String.format("%s/src/test/java/org/mtr/mapping/test/SearchForMappedMethodsTest.java", file)), testFile.replace("@namespace@", token), StandardCharsets.UTF_8);
	}

	public static void copyBuildFile(File file, String loader, String minecraftVersion, String codeVersion) throws IOException {
		final Path directory = Paths.get(String.format("%s/../../build/release", file));
		Files.createDirectories(directory);
		Files.copy(Paths.get(String.format("%s/build/libs/%s-%s.jar", file, minecraftVersion, codeVersion)), directory.resolve(String.format("Minecraft-Mappings-%s-%s-%s.jar", loader, minecraftVersion, codeVersion)), StandardCopyOption.REPLACE_EXISTING);
	}
}
