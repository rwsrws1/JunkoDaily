import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

class AndroidComposeConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            apply(plugin = "com.android.application")
            apply(plugin = "org.jetbrains.kotlin.plugin.compose")
            extensions.configure<ApplicationExtension> {
                buildFeatures.apply {
                    compose = true
                }
            }

            dependencies {
                val bom = libsCatalog.findLibrary("androidx-compose-bom").get()
                "implementation"(platform(bom))
                "implementation"(libsCatalog.findBundle("android-compose").get())

                "androidTestImplementation"(platform(bom))
                "androidTestImplementation"(libsCatalog.findLibrary("androidx-compose-ui-test-junit4").get())
                "debugImplementation"(libsCatalog.findLibrary("androidx-compose-ui-tooling").get())
                "debugImplementation"(libsCatalog.findLibrary("androidx-compose-ui-test-manifest").get())
            }
        }
    }
}