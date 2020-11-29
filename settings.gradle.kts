pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}

rootProject.name = "qyoga"
include("backend:qyoga-exercises")
include("backend:qyoga-stdlib")
include("qyoga-app")
include("qyoga-compose-front")
include("qyoga-tfx-front")
include("qyoga-api")
