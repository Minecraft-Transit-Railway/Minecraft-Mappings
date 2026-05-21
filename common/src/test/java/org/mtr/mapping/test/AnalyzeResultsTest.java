package org.mtr.mapping.test;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class AnalyzeResultsTest {

	private static final Pattern FILE_NAME_PATTERN = Pattern.compile("^(fabric|forge)-([0-9.]+)\\.txt$");

	@Test
	public void validateMappedMethods() throws IOException {
		final Map<String, Map<String, String>> mappedMethodsByVersion = new TreeMap<>();
		final List<Executable> assertions = new ArrayList<>();

		try (final DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Paths.get("../build/mappedMethods"))) {
			for (Path path : directoryStream) {
				final String fileName = path.getFileName().toString();
				final Matcher matcher = FILE_NAME_PATTERN.matcher(fileName);
				Assertions.assertTrue(matcher.matches(), "Unexpected mapped methods file: " + fileName);
				final String loader = matcher.group(1);
				final String version = matcher.group(2);
				mappedMethodsByVersion.computeIfAbsent(version, ignored -> new HashMap<>()).put(loader, FileUtils.readFileToString(path.toFile(), StandardCharsets.UTF_8));
			}
		}

		mappedMethodsByVersion.forEach((version, loaders) -> {
			final String fabricData = loaders.get("fabric");
			final String forgeData = loaders.get("forge");
			assertions.add(() -> {
				Assertions.assertNotNull(fabricData, version + " is missing fabric mapped methods");
				Assertions.assertNotNull(forgeData, version + " is missing forge mapped methods");
				Assertions.assertEquals(fabricData, forgeData, version + " fabric and forge mapped methods differ");
			});
		});

		Assertions.assertAll(assertions);
	}
}
