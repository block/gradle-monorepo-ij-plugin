package xyz.block.tools.monorepo.idea

import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project
import java.io.InputStreamReader
import java.util.Properties

@Service(Service.Level.PROJECT)
class ConfigurationService(private val project: Project) {

  // This is calculated once and on demand.
  // Changes to the config file will require an IJ restart.
  private val configProperties: Properties by lazy {
    val configFile = project.getFile(CONFIG_FILE_PATH)
    val properties = Properties()
    configFile?.let { file ->
      InputStreamReader(file.inputStream).use {
        properties.load(it)
      }
    }
    properties
  }

  val pluginEnabled: Boolean by lazy {
    configProperties.getProperty(ENABLED_PROPERTY_NAME)?.toBoolean() == true
  }

  val excludeDepTestDirs: Boolean by lazy {
    configProperties.getProperty(EXCLUDE_DEP_TEST_DIRS_PROPERTY_NAME)?.toBoolean() == true
  }

  val extraDirsToExclude: Set<String> by lazy {
    configProperties.getProperty(EXTRA_DIRS_TO_EXCLUDE_PROPERTY_NAME)?.split(",")
      ?.map { it.trim() }
      ?.filter { it.isNotEmpty() }
      ?.toSet() ?: emptySet()
  }

  private companion object {
    private const val CONFIG_FILE_PATH: String = ".idea/gradle-monorepo.properties"
    private const val ENABLED_PROPERTY_NAME: String = "enabled"
    private const val EXCLUDE_DEP_TEST_DIRS_PROPERTY_NAME: String = "exclude-dep-test-dirs"
    private const val EXTRA_DIRS_TO_EXCLUDE_PROPERTY_NAME: String = "extra-dirs-to-exclude"
  }
}