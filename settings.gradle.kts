pluginManagement {
    includeBuild("buildSrc")
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "e-paper-launcher"

include(":app")
include(":core:core-ui")
include(":core:core-data")
include(":core:core-common")
include(":feature:launcher-home")
include(":feature:launcher-drawer")
include(":feature:filter-engine")
include(":feature:icon-engine")
include(":feature:widgets")
include(":feature:settings")
include(":feature:billing")
