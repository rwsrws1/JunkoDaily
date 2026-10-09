plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.custom.android.environment)
    alias(libs.plugins.custom.android.compose)
    alias(libs.plugins.custom.android.room)
}

android {
    namespace = "com.junko.junkodaily"

    defaultConfig {
        applicationId = "com.junko.junkodaily"
        targetSdk = 37
        versionCode = 1
        versionName = "1.2"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
}

dependencies {
    implementation(projects.core.navigation)
    implementation(projects.core.data)
    implementation(projects.core.model)
    implementation(projects.core.database)
    implementation(projects.core.datastore)
    implementation(projects.core.designsystem)
    implementation(projects.core.network)
    implementation(projects.feature.introduction)
    implementation(projects.feature.auth)
    implementation(projects.feature.experiment)
    implementation(projects.feature.chart)
    implementation(projects.feature.routine)
    implementation(projects.feature.spend)

    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.navigation3.runtime)
    implementation(libs.androidx.navigation3.ui)
    implementation(libs.androidx.lifecycle.viewmodel.navigation3)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.lifecycle.process)
    implementation(libs.coil.compose)
    implementation(libs.coil.network)
    implementation(libs.androidx.work.runtime)
}