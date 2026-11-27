plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
}

android {
    namespace = "com.moma.app"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.moma.app"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "0.1.0-gym-first"

        buildConfigField("boolean", "USE_MOCK_DATA", "true")
        buildConfigField("String", "SUPABASE_URL", "\"https://your-project.supabase.co\"")
        buildConfigField("String", "SUPABASE_ANON_KEY", "\"public-anon-key\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    testOptions {
        unitTests.isReturnDefaultValues = true
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.activity.compose)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons)
    debugImplementation(libs.androidx.compose.ui.tooling)

    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)

    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    implementation(libs.retrofit)
    implementation(libs.retrofit.gson)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging)
    implementation(libs.gson)

    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    implementation(libs.security.crypto)
    implementation(libs.coil.compose)
    implementation(libs.androidx.work.runtime.ktx)

    testImplementation(libs.junit)
    testImplementation(libs.kotlin.test)
    testImplementation(libs.kotlinx.coroutines.test)
}

// Helper for environments where the forked Gradle test worker cannot start
// (a malformed PATH entry containing spaces breaks the worker's command line).
// Dumps the unit-test runtime classpath so JUnit can be invoked directly:
//   java -cp "$(cat app/build/unit-test-classpath.txt)" \
//        org.junit.runner.JUnitCore com.moma.core.domain.CredentialGateTest
tasks.register("dumpUnitTestClasspath") {
    dependsOn("compileDebugUnitTestKotlin")
    doLast {
        val entries = mutableListOf<String>()
        val build = layout.buildDirectory.get().asFile
        listOf(
            "tmp/kotlin-classes/debugUnitTest",
            "intermediates/javac/debugUnitTest/classes",
            "tmp/kotlin-classes/debug",
            "intermediates/javac/debug/classes"
        ).forEach { rel ->
            val dir = File(build, rel)
            if (dir.exists()) entries.add(dir.absolutePath)
        }
        configurations.getByName("debugUnitTestRuntimeClasspath").files
            .mapTo(entries) { it.absolutePath }
        val out = File(build, "unit-test-classpath.txt")
        out.writeText(entries.joinToString(File.pathSeparator))
        println("WROTE=" + out.absolutePath + " entries=" + entries.size)
    }
}
