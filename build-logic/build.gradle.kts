import org.gradle.kotlin.dsl.`kotlin-dsl`

plugins {
    `kotlin-dsl`
}

dependencies {
    // 引入 Android Gradle 插件依赖，使 Kotlin 代码能识别 LibraryExtension
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
}

gradlePlugin {
    plugins {
        // 注册插件 ID，供其他模块使用
        register("androidLibrary") {
            id = "custom.android.library"
            implementationClass = "AndroidLibraryConventionPlugin"
        }

        register("androidCompose") {
            id = "custom.android.compose"
            implementationClass = "AndroidComposeConventionPlugin"
        }
    }
}