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
val sinceIdeVersionForVerification = "251.23774.435" // corresponds to the 2025.1 version
val untilIdeCommunityVersion = properties["IIC.release.version"] as String
val untilIdeUltimateVersion = properties["IIU.release.version"] as String
val untilCommunityBuildMajorVersion = untilIdeCommunityVersion.substringBefore('.')
val untilUltimateBuildMajorVersion = untilIdeUltimateVersion.substringBefore('.')

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
      untilBuild = "$untilUltimateBuildMajorVersion.*"
    }
  }
  pluginVerification {
    ides {
      select {
        types = listOf(IntelliJPlatformType.IntellijIdeaCommunity)
        sinceBuild = sinceIdeVersionForVerification
        untilBuild = untilIdeCommunityVersion
      }
      select {
        types = listOf(IntelliJPlatformType.IntellijIdeaUltimate)
        sinceBuild = sinceIdeVersionForVerification
        untilBuild = untilIdeUltimateVersion
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
