pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}
includeBuild("build-logic")

rootProject.name = "LearnCompose"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
include(":app")
include(":arithmetic_lib")
include(":core:designsystem")
include(":feature")
include(":feature:chart")
include(":feature:experiment")
include(":core:network")
include(":core:data")
include(":core:database")
include(":core:datastore")
include(":feature:auth")
include(":feature:Introduction")
include(":feature:routine")
include(":feature:spend")
include(":core:navigation")
include(":core:model")
include(":core:common")
