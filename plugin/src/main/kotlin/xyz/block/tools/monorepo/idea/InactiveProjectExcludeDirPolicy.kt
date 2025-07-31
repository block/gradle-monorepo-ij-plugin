package xyz.block.tools.monorepo.idea

import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.impl.DirectoryIndexExcludePolicy
import java.nio.file.Paths

class InactiveProjectExcludeDirPolicy(private val project: Project?) : DirectoryIndexExcludePolicy {

  override fun getExcludeUrlsForProject(): Array<out String> {
    if (project == null) {
      logger.info("The project is null - skipping")
      return emptyArray()
    }

    if (!project.getService(ConfigurationService::class.java).pluginEnabled) {
      logger.info("The gradle-monorepo plugin is not enabled for the current project")
      return emptyArray()
    }

    val inactiveProjectDirs = project.getInactiveProjectDirs()
    val moduleDependencyTestDirs = project.getProjectDependencyTestDirs()
    val extraDirsToExclude = project.getService(ConfigurationService::class.java).extraDirsToExclude
    return if (
      inactiveProjectDirs.isNotEmpty() ||
      moduleDependencyTestDirs.isNotEmpty() ||
      extraDirsToExclude.isNotEmpty()
    ) {
      logger.info("Excluding inactive project dirs: $inactiveProjectDirs")
      logger.info("Excluding project dependency test dirs: $moduleDependencyTestDirs")
      logger.info("Excluding configured extra directories: $extraDirsToExclude")
      inactiveProjectDirs.plus(moduleDependencyTestDirs).plus(extraDirsToExclude)
        .map { Paths.get(project.basePath, it) }.map { "file://$it" }.toSet().toTypedArray()
    } else {
      emptyArray()
    }
  }

  companion object {
    private val logger = Logger.getInstance(InactiveProjectExcludeDirPolicy::class.java)
    private val testDirs = listOf(
      "src/test/java",
      "src/test/kotlin",
      "src/test/resources"
    )

    private fun Project.getInactiveProjectDirs(): List<String> {
      val requestedModulesContent = getRequestedModulesContent()
      if (shouldSkipDirExclusions(requestedModulesContent)) {
        logger.info("Skipping exclude of inactive project dirs as requested by user")
        return emptyList()
      }

      val inactiveProjectsContent = this.getFileContent(Files.INACTIVE_MODULES) ?: return emptyList()
      return inactiveProjectsContent
        .split("\n")
        .filter { it.trim().isNotEmpty() }
        .map { it.replace(':', '/').trim('/') }
    }

    private fun Project.getProjectDependencyTestDirs(): List<String> {
      if (!this.getService(ConfigurationService::class.java).excludeDepTestDirs) {
        logger.info("The gradle-monorepo plugin is configured to NOT exclude dep test dirs")
        return emptyList()
      }

      val requestedModulesContent = getRequestedModulesContent()
      if (shouldSkipDirExclusions(requestedModulesContent)) {
        logger.info("Skipping exclude of test dirs as requested by user")
        return emptyList()
      }

      if (requestedModulesContent.contains("__ALL")) {
        logger.info("Skipping exclude of test dirs as all Projects are requested.")
        return emptyList()
      }

      return this.getFileContent(Files.MODULE_DEPENDENCIES)
        ?.split("\n")
        ?.filter { it.trim().isNotEmpty() }
        ?.map { it.replace(':', '/').trim('/') }
        ?.flatMap { projectDir -> testDirs.map { testDir -> "$projectDir/$testDir" } }
        ?: emptyList()
    }

    private fun Project.getRequestedModulesContent(): List<String> =
      this.getFileContent(Files.REQUESTED_MODULES)?.split("\n") ?: emptyList()

    private fun shouldSkipDirExclusions(requestedModulesContent: List<String>): Boolean =
      requestedModulesContent.any { it.trim() == "__SKIP_DIR_EXCLUSION" }
  }
}
