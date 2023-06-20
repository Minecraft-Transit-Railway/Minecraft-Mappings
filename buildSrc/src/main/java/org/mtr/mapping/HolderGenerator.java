package org.mtr.mapping;

import org.apache.commons.io.FileUtils;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public final class HolderGenerator {

	public static void cleanFolder(File file) throws IOException {
		FileUtils.deleteDirectory(new File(String.format("%s/src/main/java/org/mtr/mapping/holder", file)));
	}

	public static void generate(File file, String minecraftClassName, String newClassName, @Nullable String parameterized) throws IOException {
		FileUtils.write(new File(String.format("%s/src/main/java/org/mtr/mapping/holder/%s.java", file, newClassName)), String.format(
				"package org.mtr.mapping.holder;public final class %2$s%3$s{public final %1$s%4$s data;public %2$s(%1$s%4$s data){this.data=data;}}",
				minecraftClassName,
				newClassName,
				parameterized == null ? "" : String.format("<T %s>", parameterized),
				parameterized == null ? "" : "<T>"
		), StandardCharsets.UTF_8);
	}
}
