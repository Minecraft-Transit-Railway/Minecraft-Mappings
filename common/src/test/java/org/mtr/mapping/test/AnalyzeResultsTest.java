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
import java.util.List;

public final class AnalyzeResultsTest {

	@Test
	public void validateMappedMethods() throws IOException {
		String existingData = null;
		final List<Executable> assertions = new ArrayList<>();

		try (final DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Paths.get("../build/mappedMethods"))) {
			for (Path path : directoryStream) {
				final String newData = FileUtils.readFileToString(path.toFile(), StandardCharsets.UTF_8);
				if (existingData != null) {
					final String finalExistingData = existingData;
					assertions.add(() -> Assertions.assertEquals(finalExistingData, newData, path.getFileName().toString()));
				}
				existingData = newData;
			}
		}

		Assertions.assertAll(assertions);
	}
}
