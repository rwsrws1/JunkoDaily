plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.custom.android.environment)
    alias(libs.plugins.custom.android.compose)
}

android {
    namespace = "com.junko.junkodaily.feature.chart"
}

dependencies {
    implementation(projects.core.designsystem)
    implementation(projects.core.navigation)
    implementation(projects.core.data)
    implementation(projects.core.model)
    implementation(projects.core.common)

    implementation(libs.androidx.navigation3.runtime)
    implementation(libs.androidx.lifecycle.viewmodel.navigation3)
}