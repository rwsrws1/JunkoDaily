plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.custom.android.environment)
}

android {
    namespace = "com.junko.junkodaily.core.data"
}

dependencies {
    implementation(projects.core.model)
    implementation(projects.core.database)
    implementation(projects.core.datastore)
}