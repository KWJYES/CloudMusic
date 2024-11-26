pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
        maven(url = "https://dl.bintray.com/marcoferrer/kroto-plus/")
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven(url = "https://jitpack.io")
        maven(url = "https://dl.bintray.com/marcoferrer/kroto-plus/")
        maven(url = "https://maven.aliyun.com/repository/jcenter")
    }
}

rootProject.name = "CloudMusic"
include(":app")
