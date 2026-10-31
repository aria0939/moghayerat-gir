pluginManagement {
    repositories {
        maven { url = uri("https://mirrors.cloud.tencent.com/repository/maven/google") }
        maven { url = uri("https://mirrors.cloud.tencent.com/repository/maven/central") }
        maven { url = uri("https://mirrors.cloud.tencent.com/repository/maven/gradle-plugin") }
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        maven { url = uri("https://mirrors.cloud.tencent.com/repository/maven/google") }
        maven { url = uri("https://mirrors.cloud.tencent.com/repository/maven/central") }
        google()
        mavenCentral()
        maven("https://jitpack.io")
    }
}

rootProject.name = "MoghayeratGir"
include(":app")
