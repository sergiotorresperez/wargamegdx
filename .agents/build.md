# Build — Comandos y configuración

## Comandos principales

```bash
# Ejecutar en escritorio (desarrollo)
./gradlew lwjgl3:run

# Compilar módulo core
./gradlew core:build

# APK Android debug
./gradlew android:assembleDebug

# APK Android release
./gradlew android:assembleRelease

# Regenerar assets.txt (tras añadir/quitar assets)
./gradlew generateAssetList
```

## Distribución desktop (Construo)

```bash
./gradlew lwjgl3:jarLinux    # JAR Linux x64 (OpenJDK 21 bundled)
./gradlew lwjgl3:jarMacM1    # JAR macOS aarch64
./gradlew lwjgl3:jarMacX64   # JAR macOS x64
./gradlew lwjgl3:jarWin      # JAR Windows x64 (con icono)
```

## Módulos Gradle

| Módulo | Propósito |
|--------|-----------|
| `:core` | Lógica del juego (plataforma-independiente) |
| `:lwjgl3` | Launcher desktop, entrada: `Lwjgl3Launcher.kt` |
| `:android` | Launcher Android, entrada: `AndroidLauncher.kt` |

## Versiones JVM
- Java target: 21 (`java.sourceCompatibility = 21`)
- JVM args: `-Xms512M -Xmx1G`
- Android SDK: compileSdk 35, minSdk 21, targetSdk 35

## Config en `gradle.properties`
Todas las versiones de dependencias declaradas ahí.
Ver `.agents/stack.md` para tabla de versiones.
