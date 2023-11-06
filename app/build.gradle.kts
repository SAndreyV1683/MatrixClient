

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")

}



android {
    namespace = "a.sboev.matrixclient"
    compileSdk = 34

    defaultConfig {
        applicationId = "a.sboev.matrixclient"
        minSdk = 29
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_19
        targetCompatibility = JavaVersion.VERSION_19
    }
    kotlinOptions {
        jvmTarget = "19"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.2"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }


}


val trixnityVersion = "3.10.4"
fun trixnity(module: String, version: String = trixnityVersion) =
    "net.folivo:trixnity-$module:$version"


dependencies {

    implementation ("io.ktor:ktor-client-logging:2.3.4")
    implementation("io.ktor:ktor-client-android:2.3.4")
    implementation("io.github.aakira:napier:2.6.1")
    implementation("com.russhwolf:multiplatform-settings:1.0.0")

    implementation("net.folivo:trixnity-client-repository-realm:3.10.4")
    implementation("net.folivo:trixnity-client-media-okio:3.10.4")
    //implementation("net.folivo:trixnity-client-media-indexeddb:3.10.4")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.1")

    implementation(trixnity("client"))
    implementation("androidx.compose.ui:ui-graphics-android:1.5.4")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
    implementation("androidx.activity:activity-compose:1.8.0")
    implementation(platform("androidx.compose:compose-bom:2023.03.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.03.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
    runtimeOnly("net.folivo:trixnity-client:3.10.4")
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

}