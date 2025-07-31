package xyz.block.tools.monorepo.idea

import com.intellij.ide.projectView.ProjectView
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.newvfs.BulkFileListener
import com.intellij.openapi.vfs.newvfs.events.VFileContentChangeEvent
import com.intellij.openapi.vfs.newvfs.events.VFileEvent
import com.intellij.openapi.vfs.readText

class FilesUpdateListener(val project: Project) : BulkFileListener {

  override fun after(events: List<VFileEvent>) {
    events.forEach {
      //println("Trigger file event, name: ${it.file?.name}, type: ${it.javaClass.simpleName}")
      if (it is VFileContentChangeEvent && it.file.name == Files.INACTIVE_MODULES) {
        println("Trigger project view refresh .........")
        val lines = (it as VFileContentChangeEvent).file.readText().split("\n")

        println("Has usher: ${lines.contains(":usher")}")
        println("Has btc-network: ${lines.contains(":btc-network")}")
        println("Has activity: ${lines.contains(":activity")}")
        ApplicationManager.getApplication().invokeLater {
          //VfsUtil.markDirtyAndRefresh(false, true, false, it.file)
          ProjectView.getInstance(project).refresh()
        }
      }
    }
  }
}