### [中文](README_CN.md) English

# Minecraft Mappings

Mod loaders like Fabric and Forge provide mappings that translate Minecraft class, method, and field names to human-readable ones. Since the mappings are created independently, it is often the case where class, method, and field names are translated differently between mod loaders and even between Minecraft versions. Even the package of a certain class can change. The purpose of this project is to provide a stable map of classes, methods, and fields across different mod loaders and versions of Minecraft.

Currently, this project supports 1.16-1.20 Fabric and Forge. For simplicity and ease of maintenance, only the last version of Minecraft for each major version is supported. For example, 1.16.5 is supported but 1.16.4 is not.

## How it Works

### Directory Structure

The `<mod loader>/<Minecraft version>-generator` and `<mod loader>/<Minecraft version>-mapping` subprojects are created for each supported version. The `-mapping` subproject contains all the mapping code that will be built into a library JAR and the `-generator` subproject is responsible for generating classes for the `-mapping` subproject.

### The `@MappedMethod` Annotation

The `@MappedMethod` annotation, applicable to constructors and methods, is used to show that a constructor or method is guaranteed to exist between versions. Constructors and methods not guaranteed to exist between versions should be marked as `@Deprecated` and final or non-public.

Several JUnit tests in the build pipeline will enforce these rules.

### Class Generation

This project relies heavily on automatic code generation to reduce repetitive manual work and potential human error. There are five stages to the build pipeline, all of which can be run using gradle commands.

Each `-generator` subproject should contain a test file called `ClassScannerTest`. This defines a list of classes that should be equivalent between versions. For example, in Fabric there is an `Identifier` class while in Forge there is a `ResourceLocation` class. Both of these are actually the same thing.

The `scan()` method is used to register this list of classes. Each entry is added by `scanner.put()`, `scanner.putAbstract()`, or `scanner.putInterface()`, which takes in a desired mapped name and the Minecraft class itself. Generally, the Fabric name is used for the mapped name. This mapped name must be consistent between all versions.

```java
scanner.put("MappedClassName", MinecraftClass.class);
scanner.putAbstract("MappedClassName", MinecraftClass.class);
scanner.putInterface("MappedClassName", MinecraftClass.class);
```

As an example:

`ClassScannerTest` in `fabric/1.16.5-generator`

```java
scanner.put("Identifier", Identifier.class);
```

`ClassScannerTest` in `forge/1.16.5-generator`

```java
scanner.put("Identifier", ResourceLocation.class);
```

Classes generated using `scanner.put()` will look like the following. These classes hold the actual Minecraft object inside a public final field. Enum constants will also be copied over. Note that the constructor to convert the Minecraft object to the holder class, the `cast` method, and the `isInstance` method is included as some helpful tools.

```java
package org.mtr.mapping.holder;

import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.tool.HolderBase;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@SuppressWarnings({"deprecation", "unchecked", "unused"})
public final class Identifier extends HolderBase<net.minecraft.util.ResourceLocation> {

	public Identifier(net.minecraft.util.ResourceLocation data) {
		super(data);
	}

	@MappedMethod
	public static Identifier cast(HolderBase<?> data) {
		return new Identifier((net.minecraft.util.ResourceLocation) data.data);
	}

	@MappedMethod
	public static boolean isInstance(HolderBase<?> data) {
		return data.data instanceof net.minecraft.util.ResourceLocation;
	}

	// Additional class constructors, methods, and enum constants go here
}
```

Classes generated using `scanner.putAbstract()` will look like the following. The mapped class name will always be appended by `AbstractMapping`. A holder class without the `AbstractMapping` suffix (see above) will also be generated.

```java
package org.mtr.mapping.holder;

import org.mtr.mapping.annotation.MappedMethod;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@SuppressWarnings({"deprecation", "unchecked", "unused"})
public abstract class BlockAbstractMapping extends net.minecraft.block.Block {

	// Additional class constructors and methods go here
}
```

Classes generated using `scanner.putInterface()` will look like the following. No holder classes will be generated.

```java
package org.mtr.mapping.holder;

import org.mtr.mapping.annotation.MappedMethod;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@SuppressWarnings({"deprecation", "unchecked", "unused"})
public interface StringIdentifiable extends net.minecraft.util.StringIdentifiable {

	// Additional interface methods go here
}
```

### The Build Pipeline

#### Step 1: Scan Minecraft classes in each version to see what constructors, methods, and fields exist.

```gradle
gradle test -x common:test --rerun-tasks --continue -Pgenerate=dryRun
```

The first stage of the build pipeline scans each Minecraft class defined in `ClassScannerTest` and saves the constructors, methods, and fields into temporary JSON files. This is done for each version. The temporary JSON files are stored in `build/existingMethods`.

#### Step 2: From scanned classes, collect equivalent constructors, methods, and fields and generate holder classes.

```gradle
gradle test -x common:test --rerun-tasks --continue -Pgenerate=normal
```

The second stage of the build pipeline consists of a few parts. Equivalent class, method, and field names are mapped based on manually and automatically defined mappings.

1. Read manually defined method mappings in `MethodMaps` of the `buildSrc` module. They are all defined in the `setMethodMaps()` method.
    1. `addMethodMap1.add()` maps method names in one or more classes. If defining models for one or more classes, separate them by the `|` character.
       ```java
       addMethodMap1.add("ClassNames", "desiredMappedName", "additionalNamesToBeMapped");
       ```
       For example, the `isFood` method Fabric in the `BlockItem` and `Item` classes of Fabric is also called `isEdible` in Forge.
       ```java
       addMethodMap1.add("BlockItem|Item", "isFood", "isEdible");
       ```
    1. `addMethodMap2.add()` is similar as the above except that the signature is also specified. Methods will not be mapped unless the signature matches exactly.
       ```java
       addMethodMap2.add("ClassNames", "desiredMappedName", "signature", "additionalNamesToBeMapped");
       ```
       For example, the `isChunkLoaded` method Fabric in the `ServerWorld` and `World` classes of Fabric is also called `hasChunk` in Forge.
       ```java
       addMethodMap2.add("ServerWorld|World", "isChunkLoaded", "public boolean (int,int)", "hasChunk");
       ```
    1. `blacklist.add()` is used to prevent certain methods from being mapped, for example if they cause unexpected compile errors.
       ```java
       blacklist.add("ClassNames", "mappedName", "signature");
       ```
1. Apply manually defined mappings with specified signatures.
1. Attempt to automatically figure out what constructors, methods, and fields of a class are equivalent. Iterate through the remaining constructors, methods, and fields (excluding the methods mapped manually) and compare signatures. This includes modifiers, return types (methods), and object types (fields).
    1. If the signature and method or field name matches exactly between all versions, map it.
    1. If the signature matches exactly between all versions:
        1. If there is only one instance of the signature between all versions, assume they are the same and map it.
        1. If there is more than once instance of the signature between all versions, do not map anything.
1. Apply manually defined mappings without signatures.
1. Now that all manually defined mappings are read, repeat the automatic mapping step again.
1. Collect results to `build/existingMethods/combined.json`.
1. Using collected results, generate class files.
    1. Depending on whether classes are registered with `scanner.put()`, `scanner.putAbstract()` or `scanner.putInterface()`, different classes will be generated. See the section above for more details about the types of generated classes.
    1. All constructors and methods from the Minecraft class will be added to the generated classes. If they match any of the mappings, they will be renamed to the mapped name. The `@MappedMethod` annotation will also be added. Otherwise, they will be automatically marked as `@Deprecated`.
    1. All generated classes are placed in the `holder` directory within each `-mapping` subproject.

#### Step 3: Read all constructors and methods annotated with `@MappedMethod`.

```gradle
gradle test -x common:test --rerun-tasks --continue
```

This simple test finds all constructors and methods annotated with `@MappedMethod` and stores the constructor or method class, modifiers, name, and signature into a text file located in the `build/mappedMethods` directory.

For constructors and methods not annotated with `@MappedMethod`, this test also checks that they are marked as `@Deprecated` and final or non-public.

These tests are run for all versions.

#### Step 4: Verify that all constructors and methods annotated with `@MappedMethod` exists across all versions.

```gradle
gradle common:test --rerun-tasks
```

This test takes the collected results from the previous step and checks that they are consistent across versions. Essentially, this test checks that something marked with `@MappedMethod` also exists in all other versions.

#### Step 5: Build the JAR files.

```gradle
gradle common:build jar remapJar -x test
```

The gradle `jar` and `remapJar` commands builds deobfuscated JAR files for Fabric and Forge respectively. Several common classes in the `common` subproject, including the `@MappedMethod` annotation, get built as well.

After building, JAR files in each version (including `common`) are copied over to the `build/release` directory. They are also available for download in the `Mappings` artifact of [GitHub action](https://github.com/jonafanho/Minecraft-Mappings/actions) runs.

## Using the Mappings in Another Project

More to come soon!

## Helpful Tools

Comma separated values (CSV) files are generated as part of the build pipeline. They contain a list of method names that are unmapped by manually or automatically created mappings for each registered Minecraft class.

These CSV files are located in the `build/libraryMethods` directory. They are also available for download in the `Minecraft Methods` artifact of [GitHub action](https://github.com/jonafanho/Minecraft-Mappings/actions) runs.

## Contributing

### Adding a New Minecraft Version

When creating a new version, simply create the `<mod loader>/<Minecraft version>-generator` and `<mod loader>/<Minecraft version>-mapping` directories and register the subprojects in `settings.gradle` using the `include()` method.

### Adding Mappings

As mentioned above, add entries to `ClassScannerTest` for each `-generator` subproject. Make sure that each entry exists in all versions and the mapped name is the same between all versions.

To manually add method name mappings, edit `MethodMaps` in `buildSrc`.

## License

This project is licensed with the [MIT License](https://opensource.org/licenses/MIT).
