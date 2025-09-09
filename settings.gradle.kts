pluginManagement {
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
        maven { url = uri("https://jitpack.io") }
        // إضافة مستودعات إضافية لمكتبة WireGuard
        maven { url = uri("https://repo1.maven.org/maven2/") }
        maven { url = uri("https://dl.google.com/dl/android/maven2/") }
    }
}

rootProject.name = "VizoVPN"
include(":app")