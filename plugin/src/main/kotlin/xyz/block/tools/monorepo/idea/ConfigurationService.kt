package xyz.block.tools.monorepo.idea

import com.intellij.openapi.components.Service
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import java.io.InputStreamReader
import java.util.Properties

@Service(Service.Level.PROJECT)
class ConfigurationService(private val project: Project) {

  // This is calculated once and on demand.
  // Changes to the config file will require an IJ restart.
  val pluginEnabled by lazy {
    val configFile = project.getFile(CONFIG_FILE_PATH)
    val enabled = configFile?.let { file ->
      val properties = Properties()
      InputStreamReader(file.inputStream).use {
        properties.load(it)
        properties.getProperty(ENABLED_PROPERTY_NAME)?.toBoolean() == true
      }
    } ?: false

    logger.info("""The gradle-monorepo plugin is ${if (enabled) "enabled" else "disabled"}""")
    enabled
  }

  private companion object {
    private const val CONFIG_FILE_PATH: String = ".idea/gradle-monorepo.properties"
    private const val ENABLED_PROPERTY_NAME: String = "enabled"
    private val logger = Logger.getInstance(ConfigurationService::class.java.name)
  }
}