plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.custom.android.environment)
    alias(libs.plugins.custom.android.compose)
}

android {
    namespace = "com.junko.junkodaily.core.navigation"
}

dependencies {
    implementation(libs.androidx.navigation3.runtime)
    implementation(libs.androidx.lifecycle.viewmodel.navigation3)
}