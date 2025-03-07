### Idea Plugin

This repository contains the code for the Block Gradle monorepo IntelliJ idea plugin.

## Features

- **Automatic Exclusion of Inactive Projects**: The plugin automatically excludes directories for inactive projects in IntelliJ upon running `set-ide-modules`. This happens with no user interaction, as soon as IntelliJ has synced for the newly activated projects.

The plugin is disabled by default, and a `gradle-monorepo.properties` file located under the `.idea` directory with the following content, can be used to enable it for any project: 

```properties
enabled=true
```

Changes to these config require an IDE restart to take effect.

## Local Development

To test the plugin locally, the following task will start a new IntelliJ instance with the plugin installed:
```
gradle runIde
```
