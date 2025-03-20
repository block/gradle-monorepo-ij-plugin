import org.jetbrains.intellij.platform.gradle.extensions.intellijPlatform

rootProject.name = "gradle-monorepo"

pluginManagement {
  repositories {
    mavenCentral()
    gradlePluginPortal()
  }
}

plugins {
  id("org.jetbrains.intellij.platform.settings") version "2.4.0"
}

dependencyResolutionManagement {
  repositories {
    mavenCentral()
    intellijPlatform {
      defaultRepositories()
    }
  }
}

include(":plugin")
