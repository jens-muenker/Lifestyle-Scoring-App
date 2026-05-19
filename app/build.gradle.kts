import com.github.jk1.license.render.JsonReportRenderer
import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import java.nio.file.Files
import org.gradle.api.GradleException
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.jvm.toolchain.JavaToolchainService
import org.gradle.api.tasks.testing.Test
import org.gradle.testing.jacoco.plugins.JacocoTaskExtension
import org.gradle.testing.jacoco.tasks.JacocoReport

plugins {
    id("com.android.application")
    id("maven-publish")
    id("com.google.dagger.hilt.android")
    id("com.github.jk1.dependency-license-report") version "3.1.2"
    id("com.google.devtools.ksp")
    id("jacoco")
}

android {
    namespace = "com.frosch2010.lifestyle_scoring_app"
    compileSdk = 37

    defaultConfig {
        applicationId = "com.frosch2010.lifestyle_scoring_app"
        minSdk = 24
        targetSdk = 37
        versionCode = 2
        versionName = "1.1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        testInstrumentationRunnerArguments["disableAnimations"] = "true"

        ndk {
            debugSymbolLevel = "FULL"
        }
    }

    buildFeatures {
        viewBinding = true
        dataBinding = true
    }

    buildTypes {
        debug {
            // Enables coverage data for instrumentation tests. (Unit test coverage is handled by JaCoCo report task below.)
            enableAndroidTestCoverage = true
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }
}

// Mockito inline mocks: load mockito-core as a JVM agent (JDK 21+ warns on dynamic self-attach).
val mockitoAgent: Configuration = configurations.create("mockitoAgent") {
    isCanBeConsumed = false
    isCanBeResolved = true
}

// Required for Robolectric coverage: Robolectric loads classes without line numbers by default.
tasks.withType(Test::class.java).configureEach {
    jvmArgs(
        "-javaagent:${mockitoAgent.asPath}",
        "-XX:+EnableDynamicAgentLoading"
    )
    extensions.configure(JacocoTaskExtension::class.java) {
        isIncludeNoLocationClasses = true
        excludes = listOf("jdk.internal.*")
    }
}

// Unit test coverage report: ./gradlew :app:jacocoTestReport
jacoco {
    toolVersion = "0.8.13"
}

tasks.register<JacocoReport>("jacocoTestReport") {
    group = "verification"
    description = "Generates JaCoCo coverage report for debug unit tests."

    dependsOn("testDebugUnitTest")

    reports {
        xml.required.set(true)
        html.required.set(true)
        csv.required.set(false)
    }

    // Must match bytecode loaded by unit tests (Hilt/ASM transform). Using kotlin-classes/javac-only
    // causes "execution data does not match" and near-zero coverage for @AndroidEntryPoint activities.
    val debugAppClasses = fileTree(
        "${layout.buildDirectory.get()}/intermediates/classes/debug/transformDebugClassesWithAsm/dirs"
    ) {
        exclude(
            "**/R.class",
            "**/R$*.class",
            "**/BuildConfig.*",
            "**/Manifest*.*",
            "**/*Test*.*",
            $$"**/*$ViewBinder*.*",
            $$"**/*$ViewInjector*.*",
            "**/*_MembersInjector.class",
            "**/Dagger*.*",
            "**/*Hilt*.*",
            "**/*_Factory*.*",
            "**/*_Provide*Factory*.*",
            "**/*_Impl*.*",
            "**/*MapperImpl*.*",
            $$"**/*$Lambda$*.*",
            $$"**/*$inlined$*.*",
            "**/*Companion*.*",
            "**/*Module*.*",
            "**/*Module_Companion*.*",
            "**/*Binding*.*",
            "**/*Directions*.*"
        )
    }

    classDirectories.setFrom(files(debugAppClasses))
    sourceDirectories.setFrom(files("src/main/java", "src/main/kotlin"))
    executionData.setFrom(
        fileTree(layout.buildDirectory.get().asFile) {
            include(
                "jacoco/testDebugUnitTest.exec",
                "outputs/unit_test_code_coverage/debugUnitTest/testDebugUnitTest.exec"
            )
        }
    )
}

kotlin {
    jvmToolchain(21)
}

val javaToolchains: JavaToolchainService = extensions.getByType(JavaToolchainService::class.java)
tasks.withType(JavaCompile::class.java).configureEach {
    javaCompiler.set(
        javaToolchains.compilerFor {
            languageVersion.set(JavaLanguageVersion.of(21))
        }
    )
}

dependencies {
    mockitoAgent("org.mockito:mockito-core:5.23.0") {
        isTransitive = false
    }

    implementation("androidx.core:core-ktx:1.18.0")
    implementation("androidx.appcompat:appcompat:1.7.1")
    implementation("com.google.android.material:material:1.14.0")
    implementation("androidx.constraintlayout:constraintlayout:2.2.1")
    implementation("androidx.datastore:datastore-preferences:1.2.1")

    implementation("androidx.camera:camera-camera2:1.6.1")
    implementation("androidx.camera:camera-lifecycle:1.6.1")
    implementation("androidx.camera:camera-view:1.6.1")

    implementation("com.google.android.gms:play-services-mlkit-text-recognition-common:19.1.0")
    implementation("com.google.android.gms:play-services-mlkit-text-recognition:19.0.1")

    testImplementation("junit:junit:4.13.2")
    testImplementation("androidx.test:core:1.7.0")
    androidTestImplementation("androidx.test:core:1.7.0")
    androidTestImplementation("androidx.test.ext:junit:1.3.0")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.7.0")
    androidTestImplementation("androidx.test.espresso:espresso-contrib:3.7.0")
    androidTestImplementation("androidx.test.espresso:espresso-intents:3.7.0")
    implementation("com.github.frosch2010:fuzzywuzzy-kotlin:1.0.1")

    implementation("com.google.dagger:hilt-android:2.59.2")
    ksp("com.google.dagger:hilt-android-compiler:2.59.2")
    kspTest("com.google.dagger:hilt-android-compiler:2.59.2")

    testImplementation("org.mockito.kotlin:mockito-kotlin:6.3.0")
    testImplementation("androidx.arch.core:core-testing:2.2.0")
    testImplementation("org.robolectric:robolectric:4.16.1")
    testImplementation("com.google.dagger:hilt-android-testing:2.59.2")

    implementation("io.noties.markwon:core:4.6.2")

}

licenseReport {
    outputDir = "${project.layout.buildDirectory.get()}/reports/licenses"
    renderers = arrayOf(JsonReportRenderer("licenses.json"))
    configurations = arrayOf("debugRuntimeClasspath", "releaseRuntimeClasspath")
}

tasks.register("transformLicensesJson") {
    dependsOn("generateLicenseReport")
    doLast {
        val inputFile = file("${project.layout.buildDirectory.get()}/reports/licenses/licenses.json")
        val outputFile = file("${project.layout.buildDirectory.get()}/reports/licenses/licenses_transformed.json")

        if (!inputFile.exists()) {
            outputFile.parentFile.mkdirs()
            outputFile.writeText(
                JsonOutput.prettyPrint(
                    JsonOutput.toJson(mapOf("dependencies" to emptyList<Map<String, Any?>>()))
                )
            )
            return@doLast
        }

        val parsed = JsonSlurper().parseText(Files.readString(inputFile.toPath()))
        val raw = when (parsed) {
            is Map<*, *> -> (parsed["dependencies"] as? List<*>)?.filterIsInstance<Map<*, *>>() ?: emptyList()
            is List<*> -> parsed.mapNotNull { it as? Map<*, *> }
            else -> emptyList()
        }
        val transformedDependencies = raw.map { dep ->
            mapOf(
                "projectName" to (dep["moduleName"] as? String ?: ""),
                "moduleVersion" to (dep["moduleVersion"] as? String ?: ""),
                "projectUrl" to (dep["moduleUrl"] as? String ?: ""),
                "licenses" to listOf(
                    mapOf(
                        "name" to (dep["moduleLicense"] as? String ?: "Unknown"),
                        "url" to (dep["moduleLicenseUrl"] as? String ?: "")
                    )
                )
            )
        }

        val result = mapOf("dependencies" to transformedDependencies)
        outputFile.writeText(JsonOutput.prettyPrint(JsonOutput.toJson(result)))
    }
}

tasks.register("copyLicensesToAssets") {
    dependsOn("transformLicensesJson")
    doLast {
        val assetsDir = file("$projectDir/src/main/assets")
        assetsDir.mkdirs()
        val outputFile = file("$assetsDir/licenses.json")
        val sourceFile = file("${project.layout.buildDirectory.get()}/reports/licenses/licenses_transformed.json")
        if (sourceFile.exists()) {
            sourceFile.copyTo(outputFile, overwrite = true)
        } else {
            outputFile.writeText(
                JsonOutput.prettyPrint(
                    JsonOutput.toJson(mapOf("dependencies" to emptyList<Map<String, Any?>>()))
                )
            )
        }
    }
}

tasks.register("verifyLicensesJsonAsset") {
    dependsOn("copyLicensesToAssets")
    doLast {
        val assetFile = file("$projectDir/src/main/assets/licenses.json")
        if (!assetFile.exists()) {
            throw GradleException("licenses.json is missing in app/src/main/assets")
        }
        val parsed = JsonSlurper().parseText(Files.readString(assetFile.toPath()))
        val deps = when (parsed) {
            is Map<*, *> -> parsed["dependencies"]
            else -> null
        }
        if (deps !is List<*>) {
            throw GradleException("licenses.json is invalid: missing array field 'dependencies'")
        }
    }
}

tasks.named("preBuild").configure {
    dependsOn("copyLicensesToAssets")
}

tasks.named("check").configure {
    dependsOn("verifyLicensesJsonAsset")
}