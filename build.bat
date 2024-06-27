@echo Scan Minecraft classes
call gradlew test -x common:test --continue -Pgenerate=dryRun
@echo Generate holders
call gradlew test -x common:test --continue -Pgenerate=normal
@echo Search for mapped methods
call gradlew test -x common:test
@echo Analyze test results
call gradlew common:test
@echo Build common
call gradlew common:build -x test
@echo Build Fabric 1.16.5
call gradlew fabric:1.16.5-mapping:build -x test
@echo Build Fabric 1.17.1
call gradlew fabric:1.17.1-mapping:build -x test
@echo Build Fabric 1.18.2
call gradlew fabric:1.18.2-mapping:build -x test
@echo Build Fabric 1.19.2
call gradlew fabric:1.19.2-mapping:build -x test
@echo Build Fabric 1.19.4
call gradlew fabric:1.19.4-mapping:build -x test
@echo Build Fabric 1.20.1
call gradlew fabric:1.20.1-mapping:build -x test
@echo Build Fabric 1.20.4
call gradlew fabric:1.20.4-mapping:build -x test
@echo Build Forge 1.16.5
call gradlew forge:1.16.5-mapping:build -x test
@echo Build Forge 1.17.1
call gradlew forge:1.17.1-mapping:build -x test
@echo Build Forge 1.18.2
call gradlew forge:1.18.2-mapping:build -x test
@echo Build Forge 1.19.2
call gradlew forge:1.19.2-mapping:build -x test
@echo Build Forge 1.19.4
call gradlew forge:1.19.4-mapping:build -x test
@echo Build Forge 1.20.1
call gradlew forge:1.20.1-mapping:build -x test
@echo Build Forge 1.20.4
call gradlew forge:1.20.4-mapping:build -x test
pause