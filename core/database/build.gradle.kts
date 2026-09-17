plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.custom.android.environment)
    alias(libs.plugins.custom.android.room)
    alias(libs.plugins.custom.android.compose)
}

android {
    namespace = "com.example.learncompose.core.database"
}

dependencies {
    implementation(projects.core.model)
}