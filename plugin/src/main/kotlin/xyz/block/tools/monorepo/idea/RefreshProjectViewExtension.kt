package xyz.block.tools.monorepo.idea

import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.externalSystem.model.DataNode
import com.intellij.openapi.externalSystem.model.project.ProjectData
import com.intellij.openapi.vcs.FileStatusManager
import org.jetbrains.plugins.gradle.service.project.AbstractProjectResolverExtension

class RefreshProjectViewExtension : AbstractProjectResolverExtension() {

  override fun resolveFinished(projectDataNode: DataNode<ProjectData?>) {
    super.resolveFinished(projectDataNode)
    val project = resolverCtx.externalSystemTaskId.findProject()
    if (project == null) {
      logger.warn("Project is null, cannot refresh the project view")
    } else if (!project.getService(ConfigurationService::class.java).pluginEnabled) {
      logger.info("The gradle-monorepo plugin is not enabled for the current project")
    } else {
      logger.info("Forcing a refresh of the project view")
      FileStatusManager.getInstance(project).fileStatusesChanged()
    }
  }

  companion object {
    private val logger = Logger.getInstance(RefreshProjectViewExtension::class.java)
  }
}