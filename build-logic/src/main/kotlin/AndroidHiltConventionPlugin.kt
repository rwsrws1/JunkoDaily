import com.android.build.api.dsl.CommonExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

class AndroidHiltConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            apply(plugin = "com.google.devtools.ksp")
            apply(plugin = "com.google.dagger.hilt.android")
            dependencies {
                "ksp"(libsCatalog.findLibrary("hilt-compiler").get())
                "implementation"(libsCatalog.findLibrary("hilt-android").get())
            }
        }
    }
}