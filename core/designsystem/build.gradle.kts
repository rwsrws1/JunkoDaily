plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.custom.android.environment)
    alias(libs.plugins.custom.android.compose)
}

android {
    namespace = "com.junko.junkodaily.core.designsystem"
}

dependencies {
    implementation(projects.core.common)
    implementation(libs.coil.compose)
    implementation(libs.coil.network)
}