package org.mtr.mapping.test;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class ValidateMappedMethodsTest {

	@Test
	public void validateMappedMethods() throws IOException {
		String existingData = null;
		try (final DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Paths.get("../build/mappedMethods"))) {
			for (Path path : directoryStream) {
				final String newData = FileUtils.readFileToString(path.toFile(), StandardCharsets.UTF_8);
				if (!newData.isEmpty()) {
					if (existingData != null) {
						Assertions.assertEquals(existingData, newData, path.getFileName().toString());
					}
					existingData = newData;
				}
			}
		}
	}
}
