import org.jetbrains.intellij.platform.gradle.IntelliJPlatformType

plugins {
  alias(libs.plugins.kotlin)
  id("org.jetbrains.intellij.platform")
}

dependencies {
  implementation(libs.kotlinStdLib)
  intellijPlatform {
    intellijIdeaUltimate("2024.3.2")
    pluginVerifier("1.388")
    bundledPlugin("com.intellij.gradle")
  }
}

version = System.getenv("IJ_PLUGIN_VERSION") ?: "0.1.0" // IJ_PLUGIN_VERSION env var available in CI

val pluginName = "gradle-monorepo"
val sinceBuildMajorVersion = "241" // corresponds to 2024.1.x versions
val sinceIdeVersionForVerification = "243.28141.18" // corresponds to the 2024.3 version
val untilIdeVersion = properties["IIC.release.version"] as String
val untilBuildMajorVersion = untilIdeVersion.substringBefore('.')

intellijPlatform {
  version = version
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
        sinceBuild = sinceIdeVersionForVerification
        untilBuild = untilIdeVersion
      }
    }
  }
}

tasks {
  buildPlugin {
    archiveBaseName = pluginName
  }

  check {
    dependsOn("verifyPlugin")
  }

  patchPluginXml {
    version = version
  }

  publishPlugin {
    token.set(System.getenv("JETBRAINS_TOKEN")) // JETBRAINS_TOKEN env var available in CI
  }
}
