### Idea Plugin

This repository contains the code for the Block Gradle monorepo IntelliJ idea plugin.

## Features

- **Automatic Exclusion of Inactive Projects**: The plugin automatically excludes directories for inactive projects in IntelliJ upon running `set-ide-modules`. This happens with no user interaction, as soon as IntelliJ has synced for the newly activated projects.

## Local Development

To test the plugin locally, the following task will start a new IntelliJ instance with the plugin installed:
```
gradle runIde
```
