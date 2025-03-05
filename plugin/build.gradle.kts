import org.jetbrains.intellij.platform.gradle.IntelliJPlatformType

plugins {
  alias(libs.plugins.kotlin)
  id("org.jetbrains.intellij.platform")
}

dependencies {
  implementation(libs.kotlinStdLib)
  intellijPlatform {
    intellijIdeaUltimate("2024.3.2")
    pluginVerifier("1.381")
  }
}

val pluginName = "gradle-monorepo"
val sinceIdeVersion = "233.15619.7" // corresponds to 2023.3.x versions
val sinceBuildMajorVersion = sinceIdeVersion.substringBefore('.')
val untilIdeVersion = properties["IIC.release.version"] as String
val untilBuildMajorVersion = untilIdeVersion.substringBefore('.')

intellijPlatform {
  buildSearchableOptions = false
  projectName = project.name
  instrumentCode = false // We don't need to scan codebase for jetbrains annotations
  pluginConfiguration {
    id = "xyz.block.gradle-monorepo"
    name = pluginName
    version = project.version.toString()
    description = "Improves the developer experience working in a gradle monorepo project"
    vendor {
      name = "Block"
      url = "https://block.xyz/"
    }
    ideaVersion {
      sinceBuild = sinceBuildMajorVersion
      untilBuild = "$untilBuildMajorVersion.*"
    }
  }
  pluginVerification {
    ides {
      recommended()
      select {
        types = listOf(IntelliJPlatformType.IntellijIdeaCommunity, IntelliJPlatformType.IntellijIdeaUltimate)
        sinceBuild = sinceIdeVersion
        untilBuild = untilIdeVersion
      }
    }
  }
}

tasks {
  publishPlugin {
    enabled = false
  }

  buildPlugin {
    archiveBaseName = pluginName
  }

  check {
    dependsOn("verifyPlugin")
  }
}
