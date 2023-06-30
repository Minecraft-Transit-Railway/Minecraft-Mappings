package org.mtr.mapping.test;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

public final class AnalyzeResultsTest {

	@Test
	public void validateMappedMethods() throws IOException {
		String existingData = null;

		try (final DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Paths.get("../build/mappedMethods"))) {
			for (Path path : directoryStream) {
				final String newData = FileUtils.readFileToString(path.toFile(), StandardCharsets.UTF_8);
				if (existingData != null) {
					Assertions.assertEquals(existingData, newData, path.getFileName().toString());
				}
				existingData = newData;
			}
		}
	}

	@Test
	public void consolidateExistingMethods() throws IOException {
		final Map<String, Map<String, List<MethodInfo>>> allContent = new HashMap<>();
		final Path outputPath = Paths.get("../build/libraryMethods");
		Files.createDirectories(outputPath);
		final List<String> versions = new ArrayList<>();

		try (final DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Paths.get("../build/existingMethods"))) {
			for (Path path : directoryStream) {
				final JsonObject classesObject = JsonParser.parseString(FileUtils.readFileToString(path.toFile(), StandardCharsets.UTF_8)).getAsJsonObject();
				final String version = path.getFileName().toString();
				versions.add(version);

				classesObject.keySet().forEach(key -> {
					allContent.computeIfAbsent(key, innerContent -> new HashMap<>());
					classesObject.getAsJsonArray(key).forEach(method -> {
						final MethodInfo methodInfo = new MethodInfo(method.getAsJsonObject(), version);
						allContent.get(key).computeIfAbsent(methodInfo.signature, methods -> new ArrayList<>());
						allContent.get(key).get(methodInfo.signature).add(methodInfo);
					});
				});
			}
		}

		for (final Map.Entry<String, Map<String, List<MethodInfo>>> innerContent : allContent.entrySet()) {
			final List<String> fileContent = new ArrayList<>();
			fileContent.add(String.join(",", versions));
			final List<String> signatures = new ArrayList<>(innerContent.getValue().keySet());
			Collections.sort(signatures);

			signatures.forEach(signature -> {
				fileContent.add("");
				final List<MethodInfo> methods = innerContent.getValue().get(signature);
				methods.sort(Comparator.comparing(methodInfo -> methodInfo.name));
				while (!methods.isEmpty()) {
					final String[] row = new String[versions.size()];
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
					fileContent.add(String.join(",", row));
				}
			});

			Files.write(outputPath.resolve(String.format("%s.csv", innerContent.getKey())), fileContent, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
		}
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
