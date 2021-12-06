# Minecraft Mappings

This is a small library for developing Minecraft mods for different Minecraft versions. Currently, this library supports Fabric 1.16.5, Fabric 1.17.1, and Fabric 1.18.

## Usage

Make the following changes to `build.gradle` file of your project.

1. Add the library as a dependency:

   ```
   dependencies {
   	...
   	modImplementation "com.github.jonafanho:Minecraft-Mappings:<commit hash>"
   }
   ```

1. Add Jitpack to the list of repositories:

   ```
   allprojects {
   	repositories {
   		...
   		maven {
   			url = "https://jitpack.io/"
   		}
   	}
   }
   ```

Your mod may require the [Minecraft Transit Railway](https://github.com/jonafanho/Minecraft-Transit-Railway) mod. This will affect the configuration of `build.gradle`.

### Requiring the Minecraft Transit Railway Mod

1. Add the Minecraft Transit Railway as a dependency:

   ```
   dependencies {
   	...
   	modImplementation "com.github.jonafanho:Minecraft-Transit-Railway:<commit hash>"
   }
   ```

1. The Minecraft Transit Railway mod already includes this library, so version conflicts may arise. Add a resolution strategy upon version conflict:

   ```
   configurations.all {
   	resolutionStrategy {
   		force "com.github.jonafanho:Minecraft-Mappings:<commit hash>"
   	}
   }
   ```

### Not Requiring the Minecraft Transit Railway Mod

1. This library will have to be packaged into your final jar:

   ```
   dependencies {
   	...
   	include "com.github.jonafanho:Minecraft-Mappings:<commit hash>"
   }
   ```

## License

This project is licensed with the [MIT License](https://opensource.org/licenses/MIT).
