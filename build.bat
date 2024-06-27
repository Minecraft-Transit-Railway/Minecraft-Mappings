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
@echo Build Fabric
call gradlew build -p fabric -x test
@echo Build Forge
call gradlew build -p forge -x test
pause
