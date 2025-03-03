pluginManagement {
    repositories {
        maven {
            url = uri("https://maven.myket.ir")
        }

        gradlePluginPortal()
        google()
        mavenCentral()
    }
}
dependencyResolutionManagement {

    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        maven {
            url = uri("https://maven.myket.ir")
        }

        google()
        mavenCentral()
        jcenter()
    }
}
rootProject.name = "V2rayNG"
include(":app")
