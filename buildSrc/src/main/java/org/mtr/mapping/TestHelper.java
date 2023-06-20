package org.mtr.mapping;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public final class TestHelper {

	public static void copyTest(File file, String token) throws IOException {
		final String testFile = FileUtils.readFileToString(new File(String.format("%s/../../common/src/test/java/org/mtr/mapping/test/SearchForMappedMethodsTest.java", file)), StandardCharsets.UTF_8);
		FileUtils.write(new File(String.format("%s/src/test/java/org/mtr/mapping/test/SearchForMappedMethodsTest.java", file)), testFile.replace("@namespace@", token), StandardCharsets.UTF_8);
	}
}
