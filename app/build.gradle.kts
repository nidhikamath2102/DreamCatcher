plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "edu.vt.cs5254.dreamcatcher"
    compileSdk = 34

    defaultConfig {
        applicationId = "edu.vt.cs5254.dreamcatcher"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
    }
    @Suppress("UnstableApiUsage")
    testOptions {
        animationsDisabled = true
    }

}

dependencies {

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    implementation("androidx.fragment:fragment-ktx:1.6.2") // BNRG Listing 9.1
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0") // BNRG Listing 10.1
    implementation("androidx.recyclerview:recyclerview:1.3.2") // BNRG Listing 10.5
    implementation("androidx.test.espresso:espresso-contrib:3.5.1") // for Project 2 testing
    debugImplementation("androidx.fragment:fragment-testing:1.6.2") // for Project 2 testing

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}