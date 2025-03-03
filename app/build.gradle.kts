import com.android.build.api.dsl.Lint
import com.android.build.api.dsl.LintOptions

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.v2ray.ang"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.v2plus.app"
        minSdk = 21
        targetSdk = 34
        versionCode = 579
        versionName = "1.8.35"
        multiDexEnabled = true
        splits.abi {
            reset()
            include(
                "arm64-v8a",
                "armeabi-v7a",
                "x86_64",
                "x86"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildTypes {
        release {
            isMinifyEnabled = false

        }
        debug {
            isMinifyEnabled = false

        }
    }

    sourceSets {
        getByName("main") {
            jniLibs.srcDirs("libs")
        }
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
    }

    splits {
        abi {
            isEnable = true
            isUniversalApk = true
        }
    }

    applicationVariants.all {
        val variant = this
        val versionCodes =
            mapOf("armeabi-v7a" to 4, "arm64-v8a" to 4, "x86" to 4, "x86_64" to 4, "universal" to 4)

        variant.outputs
            .map { it as com.android.build.gradle.internal.api.ApkVariantOutputImpl }
            .forEach { output ->
                val abi = if (output.getFilter("ABI") != null)
                    output.getFilter("ABI")
                else
                    "universal"

                output.outputFileName = "v2Plus_${variant.versionName}_${abi}.apk"
                if(versionCodes.containsKey(abi))
                {
                    output.versionCodeOverride = (1000000 * versionCodes[abi]!!).plus(variant.versionCode)
                }
                else
                {
                    return@forEach
                }
            }
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
    }

    packagingOptions {
        jniLibs {
            useLegacyPackaging = true
        }
    }
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.aar","*.jar"))))
    implementation(files("E:\\v2ray1.8.35\\v2rayNG-1.8.35\\V2rayNG\\app\\library\\rxpermissions-0.9.4.aar"))
    implementation(files("E:\\v2ray1.8.35\\v2rayNG-1.8.35\\V2rayNG\\app\\library\\libv2ray.aar"))
    implementation("com.squareup.okhttp3:okhttp:4.9.3")

    testImplementation(libs.junit)

    implementation(libs.flexbox)
    // Androidx
    implementation(libs.constraintlayout)
    implementation(libs.legacy.support.v4)
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.cardview)
    implementation(libs.preference.ktx)
    implementation(libs.recyclerview)
    implementation(libs.fragment.ktx)
    implementation(libs.multidex)
    implementation(libs.viewpager2)

    // Androidx ktx
    implementation(libs.activity.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.runtime.ktx)

    //kotlin
    implementation(libs.kotlin.reflect)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)

    implementation(libs.mmkv.static)
    implementation(libs.gson)
    implementation(libs.rxjava)
    implementation(libs.rxandroid)
    implementation(libs.toastcompat)
    implementation(libs.editorkit)
    implementation(libs.language.base)
    implementation(libs.language.json)
    implementation(libs.quickie.bundled)
    implementation(libs.core)

    implementation(libs.work.runtime.ktx)
    implementation(libs.work.multiprocess)
    implementation("commons-codec:commons-codec:1.15")

}