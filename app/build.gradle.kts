plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp") // BNRG Listing 12.8
    id("androidx.navigation.safeargs.kotlin") // BNRNG 13.10
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
        testInstrumentationRunnerArguments["clearPackageData"] = "true"

        ksp { // BNRG Chapter 12 Challenge
            arg("room.schemaLocation", "$projectDir/schemas")
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
        execution = "ANDROIDX_TEST_ORCHESTRATOR" // for P2B Testing
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

    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0") // BNRG Listing 12.1
    implementation("androidx.room:room-runtime:2.6.1") // BNRG Listing 12.8
    implementation("androidx.room:room-ktx:2.6.1") // BNRG Listing 12.8
    ksp("androidx.room:room-compiler:2.6.1") // BNRG Listing 12.8
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.7") // BNRG Listing 13.1
    implementation("androidx.navigation:navigation-ui-ktx:2.7.7") // BNRG Listing 13.1
    androidTestImplementation("androidx.test:runner:1.5.2") // for P2B testing
    androidTestUtil("androidx.test:orchestrator:1.4.2") // for P2B testing

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.test.espresso:espresso-intents:3.5.1") // Project 2C testing

    androidTestImplementation("androidx.navigation:navigation-testing:2.7.7") // Project 2 testing
}