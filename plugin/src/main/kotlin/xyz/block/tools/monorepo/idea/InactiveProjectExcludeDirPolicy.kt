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
    return if (inactiveProjectDirs.isNotEmpty()) {
      logger.info("Excluding inactive project dirs: $inactiveProjectDirs")
      inactiveProjectDirs.map { Paths.get(project.basePath, it) }.map { "file://$it" }.toSet().toTypedArray()
    } else {
      emptyArray()
    }
  }

  companion object {
    private val logger = Logger.getInstance(InactiveProjectExcludeDirPolicy::class.java)

    private fun Project.getInactiveProjectDirs(): List<String> {
      val requestedModulesContent = this.getFileContent("requested-modules.txt") ?: return emptyList()
      val skipDirExclusions = requestedModulesContent.split("\n").any { it.trim() == "__SKIP_DIR_EXCLUSION" }
      if (skipDirExclusions) {
        logger.info("Skipping exclude of inactive project dirs as requested by user")
        return emptyList()
      }

      val inactiveProjectsContent = this.getFileContent("settings_inactive_modules.txt") ?: return emptyList()
      return inactiveProjectsContent
        .split("\n")
        .filter { it.trim().isNotEmpty() }
        .map { it.replace(':', '/').trim('/') }
    }
  }
}
