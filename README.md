# Minecraft Mappings

This is a small library for developing Minecraft mods for different Minecraft versions. Currently, this library supports Architectury 1.16.5, 1.17.1, and 1.18.

## Usage

Make the following changes to `build.gradle` file of your project.

1. Add a gradle task to download and extract this repository, making sure to replace `<version>` with the appropriate Minecraft version and `<path>` with the corresponding directory of your project:

   ```
   task setupFiles() {
       download {
           src "https://github.com/jonafanho/Minecraft-Mappings/archive/refs/heads/<version>.zip"
           dest "common/src/main/java/<path>/mappings/files.zip"
           overwrite true
       }
   
       copy {
           outputs.upToDateWhen { false }
           from(zipTree("common/src/main/java/<path>/mappings/files.zip")) { eachFile { file -> file.relativePath = new RelativePath(true, file.relativePath.segments.drop(1) as String[]) } }
           into "common/src/main/java/<path>/mappings"
           filter(ReplaceTokens, tokens: ["package": "<path>.mappings"])
       }

       ant.path { ant.fileset(dir: "common/src/main/java/<path>/mappings", includes: "Fabric*.java") }.list().each {
           ant.move(file: it, todir: "fabric/src/main/java/<path>/mappings")
       }

       ant.path { ant.fileset(dir: "common/src/main/java/<path>/mappings", includes: "Forge*.java") }.list().each {
           ant.move(file: it, todir: "forge/src/main/java/<path>/mappings")
       }
   }
   ```

1. Add the above task to run automatically whenever a gradle task is run:

   ```
   allprojects {
       afterEvaluate {
           for (def task in it.tasks) {
               if (task != rootProject.tasks.setupFiles) {
                   task.dependsOn rootProject.tasks.setupFiles
               }
           }
       }
   }
   ```

## License

This project is licensed with the [MIT License](https://opensource.org/licenses/MIT).
