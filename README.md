# Minecraft Mappings

This is a small library for developing Minecraft mods for different Minecraft versions. Currently, this library supports Architectury 1.16.5, 1.17.1, and 1.18.

## Usage

Make the following changes to `build.gradle` file of your project.

1. Add gradle tasks to download and extract this repository:

   ```
   task downloadMappings(type: Download) {
       src "https://github.com/jonafanho/Minecraft-Mappings/archive/refs/heads/<version>.zip"
       dest "common/src/main/java/minecraftmappings/files.zip"
       overwrite true
   }

   task extractMappings(dependsOn: downloadMappings, type: Copy) {
       outputs.upToDateWhen { false }
       from(zipTree("common/src/main/java/minecraftmappings/files.zip")) { eachFile { file -> file.relativePath = new RelativePath(true, file.relativePath.segments.drop(1) as String[]) } }
       into "common/src/main/java/minecraftmappings"
   }
   ```

1. Add the above tasks to run automatically whenever a gradle task is run:

   ```
   allprojects {
       afterEvaluate {
           for (def task in it.tasks) {
               if (task != rootProject.tasks.downloadMappings && task != rootProject.tasks.extractMappings) {
                   task.dependsOn rootProject.tasks.injectKey
               }
           }
       }
   }
   ```

## License

This project is licensed with the [MIT License](https://opensource.org/licenses/MIT).
