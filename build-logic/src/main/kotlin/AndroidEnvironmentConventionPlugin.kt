import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.CommonExtension
import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

class AndroidEnvironmentConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            apply(plugin = "org.jetbrains.kotlin.plugin.serialization")
            apply(plugin = "custom.android.hilt")

            pluginManager.withPlugin("com.android.application") {
                extensions.configure<ApplicationExtension> {
                    configureCommonAndroid(this)
                }
            }
            pluginManager.withPlugin("com.android.library") {
                extensions.configure<LibraryExtension> {
                    configureCommonAndroid(this)
                }
            }

            dependencies {
                "implementation"(libsCatalog.findLibrary("androidx-core-ktx").get())
                "testImplementation"(libsCatalog.findLibrary("junit").get())
                "androidTestImplementation"(libsCatalog.findLibrary("androidx-espresso-core").get())
                "androidTestImplementation"(libsCatalog.findLibrary("androidx-junit").get())
            }
        }
    }

    private fun configureCommonAndroid(
        commonExtension: CommonExtension
    ) {
        commonExtension.apply {
            compileSdk {
                version = release(37) {
                    minorApiLevel = 1
                }
            }

            defaultConfig.apply {
                minSdk = 29
                testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
            }

            compileOptions.apply {
                sourceCompatibility = JavaVersion.VERSION_17
                targetCompatibility = JavaVersion.VERSION_17
            }
        }
    }
}

