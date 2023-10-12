package org.mtr.mapping.test;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;
import org.mtr.mapping.tool.DummyClass;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Stream;

public final class GenerateMixinCreatorsTest {

	@Test
	public void generate() {
		final Map<String, Map<String, String>> files = new HashMap<>();
		final Path rootDirectory = Paths.get(System.getProperty("user.dir"));

		forEach(rootDirectory.getParent().resolve("build/mixins"), loaderPath -> forEach(loaderPath, minecraftVersionPath -> forEach(minecraftVersionPath, mixinPath -> {
			try {
				final String mixinName = mixinPath.getFileName().toString();
				files.computeIfAbsent(mixinName, mixinMap -> new HashMap<>());
				files.get(mixinName).put(String.format("%s-%s", minecraftVersionPath.getFileName().toString(), loaderPath.getFileName().toString()), FileUtils.readFileToString(mixinPath.toFile(), StandardCharsets.UTF_8));
			} catch (Exception e) {
				DummyClass.logException(e);
			}
		})));

		files.forEach((mixinName, contents) -> {
			if (!mixinName.equals("package-info")) {
				final StringBuilder stringBuilder = new StringBuilder("package org.mtr.mapping.mixin;import java.io.IOException;import java.nio.charset.StandardCharsets;import java.nio.file.Files;import java.nio.file.Path;import java.nio.file.StandardOpenOption;public final class Create");
				stringBuilder.append(mixinName).append("{public static void create(String minecraftVersion,String loader,Path path,String packageName)throws IOException{final String content;switch(String.format(\"%s-%s\",minecraftVersion,loader)){");
				contents.forEach((key, mixinContent) -> stringBuilder.append("case\"").append(key).append("\":content=\"").append(mixinContent.replace("\n", "\\n").replace("\r", "").replace("\"", "\\\"")).append("\";break;"));
				stringBuilder.append("default:content=null;}if(content!=null){Files.write(path.resolve(\"").append(mixinName);
				stringBuilder.append(".java\"),content.replace(\"@package@\",packageName).getBytes(StandardCharsets.UTF_8),StandardOpenOption.CREATE,StandardOpenOption.TRUNCATE_EXISTING);}}}");

				try {
					final Path directory = rootDirectory.resolve("src/main/java/org/mtr/mapping/mixin");
					Files.createDirectories(directory);
					Files.write(directory.resolve(String.format("Create%s.java", mixinName)), stringBuilder.toString().getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
				} catch (Exception e) {
					DummyClass.logException(e);
				}
			}
		});
	}

	private static void forEach(Path path, Consumer<Path> consumer) {
		try (Stream<Path> stream = Files.list(path)) {
			stream.forEach(consumer);
		} catch (Exception e) {
			DummyClass.logException(e);
		}
	}
}
