package xyz.block.tools.monorepo.idea

import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.guessProjectDir
import com.intellij.openapi.util.io.FileUtilRt
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.findFile
import java.io.InputStreamReader

private val logger by lazy { Logger.getInstance("xyz.block.tools.monorepo.idea.ProjectExtensions") }

fun Project.getFileContent(filePath: String): String? {
  val rootDir = this.guessProjectDir()
  if (rootDir == null) {
    logger.warn("The project root directory is null - skipping")
    return null
  }
  val file = rootDir.findFile(filePath)
  if (file == null) {
    logger.warn("The file at $filePath is missing")
    return null
  }
  return file.loadText()
}

private fun VirtualFile.loadText(): String =
  InputStreamReader(this.inputStream).use { reader ->
    return String(FileUtilRt.loadText(reader, this.length.toInt()))
  }
