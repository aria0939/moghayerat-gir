pluginManagement {
    repositories {
        maven { url = uri("https://repo.huaweicloud.com/repository/maven") }
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        maven { url = uri("https://repo.huaweicloud.com/repository/maven") }
        google()
        mavenCentral()
        maven("https://jitpack.io")
    }
}

rootProject.name = "MoghayeratGir"
include(":app")
