@file:Suppress("UNUSED_EXPRESSION")

plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")

}

android {
    namespace = "com.example.governorsindhfaculty"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.governorsindhfaculty"
        minSdk = 23
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildFeatures{
        viewBinding = true;
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.firebase.crashlytics.buildtools)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation(platform("com.google.firebase:firebase-bom:33.1.2"))
    implementation("com.google.firebase:firebase-analytics")
    implementation ("com.google.firebase:firebase-auth:23.0.0")
    implementation ("com.google.firebase:firebase-firestore:23.0.3")
    implementation ("com.airbnb.android:lottie:6.4.1")
    implementation ("com.google.firebase:firebase-storage:19.2.1")
    implementation("com.google.firebase:firebase-database")

    implementation ("com.google.android.material:material:1.5.0")
    implementation ("com.hbb20:ccp:2.5.0")
    //responsive size UI
    implementation ("com.intuit.sdp:sdp-android:1.1.1")
    //responsive size texts
    implementation ("com.intuit.ssp:ssp-android:1.1.1")
    implementation ("commons-io:commons-io:2.6")
    //pdf Viewer dependency
//    implementation ("com.github.barteksc:android-pdf-viewer:3.2.0-beta.1")
//    implementation ("com.github.barteksc:android-pdf-viewer:2.8.2")
}