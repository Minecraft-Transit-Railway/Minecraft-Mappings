### 中文 [English](README.md)

# Minecraft Mappings

像Fabric和Forge这样的mod加载器提供了将Minecraft类、方法和字段名称翻译成可读的人类名称的映射。由于这些映射是独立创建的，经常出现不同的mod加载器甚至不同的Minecraft版本之间翻译类、方法和字段名称的差异。甚至某个类的包也可能会更改。该项目的目的是为不同的mod加载器和Minecraft版本提供一个稳定的类、方法和字段映射。

目前，该项目支持1.16-1.20的Fabric和Forge。为了简化和维护的便捷性，只支持每个主要版本的最新Minecraft版本。例如，支持1.16.5，但不支持1.16.4。

## 工作原理

### 目录结构

为每个支持的版本创建了`<mod loader>/<Minecraft version>-generator`和`<mod loader>/<Minecraft version>-mapping`
子项目。`-mapping`子项目包含所有映射代码，将构建为库JAR文件，而`-generator`子项目负责生成`-mapping`子项目的类。

### `@MappedMethod`注解

`@MappedMethod`
注解适用于构造函数和方法，用于表示某个构造函数或方法在各个版本之间是保证存在的。不保证在各个版本之间存在的构造函数和方法应标记为`@Deprecated`
并且是final或非公共的。

在构建流水线中，有几个JUnit测试将强制执行这些规则。

### 类生成

该项目大量依赖自动代码生成，以减少重复的手动工作和潜在的人为错误。构建流水线分为五个阶段，所有这些阶段都可以使用gradle命令运行。

每个`-generator`子项目应包含一个名为`ClassScannerTest`
的测试文件。它定义了应该在各个版本之间等效的类列表。例如，在Fabric中有一个`Identifier`
类，而在Forge中有一个`ResourceLocation`类。实际上，这两者是相同的。

`scan()`方法用于注册这个类列表。每个条目都是由`scanner.put()`、`scanner.putAbstract()`或`scanner.putInterface()`
添加的，它们接受所需的映射名称和Minecraft类本身。通常情况下，映射名称使用Fabric的名称。

```java
scanner.put("MappedClassName",MinecraftClass.class);
scanner.putAbstract("MappedClassName",MinecraftClass.class);
scanner.putInterface("MappedClassName",MinecraftClass.class);
```

举个例子：

`fabric/1.16.5-generator`中的`ClassScannerTest`：

```java
scanner.put("Identifier",Identifier.class);
```

`forge/1.16.5-generator`中的`ClassScannerTest`：

```java
scanner.put("Identifier",ResourceLocation.class);
```

使用`scanner.put()`
生成的类将如下所示。这些类在一个公共的final字段内保存了实际的Minecraft对象。枚举常量也会被复制过来。请注意，用于将Minecraft对象转换为持有类的构造函数、`cast`
方法和`isInstance`方法也包括在内，这些都是一些有用的工具。

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

    // 其他类构造函数、方法和枚举常量在这里添加
}
```

使用`scanner.putAbstract()`生成的类将如下所示。映射类名将始终附加`AbstractMapping`后缀。还将生成没有`AbstractMapping`
后缀的持有类。

```java
package org.mtr.mapping.holder;

import org.mtr.mapping.annotation.MappedMethod;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@SuppressWarnings({"deprecation", "unchecked", "unused"})
public abstract class BlockAbstractMapping extends net.minecraft.block.Block {

    // 其他类构造函数和方法在这里添加
}
```

使用`scanner.putInterface()`生成的类将如下所示。不会生成持有类。

```java
package org.mtr.mapping.holder;

import org.mtr.mapping.annotation.MappedMethod;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@SuppressWarnings({"deprecation", "unchecked", "unused"})
public interface StringIdentifiable extends net.minecraft.util.StringIdentifiable {

    // 其他接口方法在这里添加
}
```

### 构建流水线

#### 步骤1：扫描每个版本中的Minecraft类，查看存在哪些构造函数、方法和字段。

```gradle
gradle test -x common:test --rerun-tasks --continue -Pgenerate=dryRun
```

构建流水线的第一个阶段会扫描`ClassScannerTest`
中定义的每个Minecraft类，并将构造函数、方法和字段保存到临时的JSON文件中。这对每个版本都会执行。临时JSON文件存储在`build/existingMethods`
中。

#### 步骤2：从扫描的类中收集等效的构造函数、方法和字段，并生成持有类。

```gradle
gradle test -x common:test --rerun-tasks --continue -Pgenerate=normal
```

构建流水线的第二阶段包括几个部分。根据手动和自动定义的映射，将等效的类、方法和字段名称进行映射。
1. 读取`buildSrc`模块中`MethodMaps`中手动定义的方法映射。它们都在`setMethodMaps()`方法中定义。
    1. `addMethodMap1.add()`将一个或多个类中的方法名称映射到另一个类中。如果为一个或多个类定义模型，请使用`|` 字符分隔它们。
       ```java
       addMethodMap1.add("ClassNames", "desiredMappedName", "additionalNamesToBeMapped");
       ```
       例如，Fabric中`BlockItem`和`Item`类的`isFood`方法也在Forge中称为isEdible。
       ```java
       addMethodMap1.add("BlockItem|Item", "isFood", "isEdible");
       ```
    1. `addMethodMap2.add()`与上述相似，只是还指定了签名。方法只有在签名完全匹配时才会进行映射。
       ```java
       addMethodMap2.add("ClassNames", "desiredMappedName", "signature", "additionalNamesToBeMapped");
       ```
       例如，Fabric中`ServerWorld`和`World`类的`isChunkLoaded`方法也在Forge中称为`hasChunk`。
       ```java
       addMethodMap2.add("ServerWorld|World", "isChunkLoaded", "public boolean (int,int)", "hasChunk");
       ```
    1. `blacklist.add()`用于防止某些方法被映射，例如如果它们导致意外的编译错误。
       ```java
       blacklist.add("ClassNames", "mappedName", "signature");
       ```
1. 应用手动定义的带有指定签名的映射。
1. 尝试自动找出构造函数、方法和字段在类中的等效项。迭代剩余的构造函数、方法和字段（手动映射的方法除外），并比较签名。这包括修饰符、返回类型（方法）和对象类型（字段）。
    1. 如果签名和方法或字段名称在所有版本之间完全匹配，进行映射。
    1. 如果签名在所有版本之间完全匹配：
        1. 如果在所有版本之间只有一个签名实例，假定它们是相同的并进行映射。
        1. 如果在所有版本之间有多个签名实例，则不进行映射。
    1. 应用手动定义的不带签名的映射。
    1. 现在，所有手动定义的映射都已读取，再次重复自动映射步骤
    1. 收集结果到`build/existingMethods/combined.json`。
    1. 使用收集的结果生成类文件。
        1. 根据类是否注册到`scanner.put()`、`scanner.putAbstract()`或`scanner.putInterface()`中，将生成不同类型的类。有关生成的类类型的更多详细信息，请参见上面的章节。
        1. 将Minecraft类中的所有构造函数和方法添加到生成的类中。如果它们与任何映射匹配，它们将被重命名为映射名称。还将添加`@MappedMethod`注解。否则，它们将自动标记为`@Deprecated`。
        1. 所有生成的类都放置在每个`-mapping`子项目的`holder`目录中。
#### 步骤3：读取所有带有`@MappedMethod`注解的构造函数和方法。

```gradle
gradle test -x common:test --rerun-tasks --continue
```

这个简单的测试会查找所有带有`@MappedMethod`注解的构造函数和方法，并将构造函数或方法类、修饰符、名称和签名存储到位于`build/mappedMethods`目录中的文本文件中。

对于未带有`@MappedMethod`注解的构造函数和方法，此测试还会检查它们是否标记为`@Deprecated`并且是final或非公共的。

这些测试对所有版本都进行了运行。

#### 步骤4：验证所有带有`@MappedMethod`注解的构造函数和方法在所有版本中都存在。

```gradle
gradle common:test --rerun-tasks
```

这个测试采用前面步骤中收集的结果，并检查它们在各个版本之间是否一致。实际上，这个测试检查带有`@MappedMethod`注解的内容是否在所有其他版本中也存在。

#### 步骤5：构建JAR文件。

```gradle
gradle common:build jar remapJar -x test
```

gradle的`jar`和`remapJar`命令分别构建了Fabric和Forge的反混淆JAR文件。`common`子项目中的几个公共类，包括`@MappedMethod`注解，也会被构建。

构建后，每个版本中的JAR文件（包括`common`）都会复制到`build/release`目录中。它们也可以在[GitHub action](https://github.com/jonafanho/Minecraft-Mappings/actions)运行的`Mappings`工件中下载。

## 在另一个项目中使用映射

更多信息即将推出！

## 有用的工具

构建流水线会生成逗号分隔值（CSV）文件。这些文件包含了由手动或自动创建的映射未映射的方法名称列表，对于每个已注册的Minecraft类都是如此。

这些CSV文件位于`build/libraryMethods`目录中。它们也可以在[GitHub action](https://github.com/jonafanho/Minecraft-Mappings/actions)运行的`Minecraft Methods`工件中下载。

## 贡献

### 添加新的Minecraft版本

创建新版本时，只需创建`<mod loader>/<Minecraft version>-generator`和`<mod loader>/<Minecraft version>-mapping`目录，并在`settings.gradle`中使用`include()`方法注册子项目。

### 添加映射

如上所述，为每个`-generator`子项目的`ClassScannerTest`添加条目。确保每个条目在所有版本中都存在，并且映射名称在所有版本之间相同。

要手动添加方法名称映射，请编辑`buildSrc`中的`MethodMaps`。

## 许可证
该项目使用[MIT许可证](https://opensource.org/licenses/MIT)。