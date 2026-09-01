import org.gradle.kotlin.dsl.`kotlin-dsl`

plugins {
    `kotlin-dsl`
}

dependencies {
    // 引入 Android Gradle 插件依赖，使 Kotlin 代码能识别 LibraryExtension
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
    compileOnly(libs.ksp.gradlePlugin)
    compileOnly(libs.room.gradlePlugin)
}

gradlePlugin {
    plugins {
        // 注册插件 ID，供其他模块使用
        register("androidEnvironment") {
            id = "custom.android.environment"
            implementationClass = "AndroidEnvironmentConventionPlugin"
        }

        register("androidCompose") {
            id = "custom.android.compose"
            implementationClass = "AndroidComposeConventionPlugin"
        }

        register("androidRoom") {
            id = "custom.android.room"
            implementationClass = "AndroidRoomConventionPlugin"
        }
    }
}