import com.vanniktech.maven.publish.SonatypeHost

plugins {
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.compose)
    alias(libs.plugins.compose.compiler)
    id("com.vanniktech.maven.publish") version "0.29.0"
}

group = "io.github.thegbguy"
version = "1.0"

kotlin {
    androidTarget {
        publishLibraryVariants("release", "debug")
        compilations.all {
            kotlinOptions {
                jvmTarget = "17"
            }
        }
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "library"
            isStatic = true
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(libs.kotlinx.datetime)
            implementation(libs.kermit)
        }

        androidMain {
            dependencies {
                implementation(libs.compose.ui.tooling.preview)
                implementation(libs.androidx.activity.compose)
            }
        }

    }

    //https://kotlinlang.org/docs/native-objc-interop.html#export-of-kdoc-comments-to-generated-objective-c-headers
    targets.withType<org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget> {
        compilations["main"].compilerOptions.options.freeCompilerArgs.add("-Xexport-kdoc")
    }

}

android {
    namespace = "io.github.thegbguy"
    compileSdk = 34

    defaultConfig {
        minSdk = 24
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.plugin.get()
    }
}

mavenPublishing {
    coordinates(
        groupId = "io.github.thegbguy",
        artifactId = "nepali-date-picker",
        version = "1.0.2"
    )

    pom {
        name.set("Kompose Nepali Date Picker")
        description.set("A KMP library for data picker and date conversion.")
        inceptionYear.set("2024")
        url.set("https://github.com/theGBguy/KomposeNepaliDatePicker")
        licenses {
            license {
                name.set("MIT")
                url.set("https://opensource.org/licenses/MIT")
            }
        }
        developers {
            developer {
                id.set("theGBguy")
                name.set("Chiranjeevi Pandey")
                url.set("https://github.com/theGBguy/")
            }
        }
        scm {
            url.set("https://github.com/theGBguy/KomposeNepaliDatePicker")
            connection.set("scm:git:git://github.com/theGBguy/KomposeNepaliDatePicker.git")
            developerConnection.set("scm:git:ssh://git@github.com/theGBguy/KomposeNepaliDatePicker.git")
        }
    }

    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)

    signAllPublications()
}