plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.soroban"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.soroban"
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

    //Enable viewBinding
    buildFeatures {
        viewBinding = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    implementation(platform("com.google.firebase:firebase-bom:33.4.0"))
    implementation("com.google.firebase:firebase-firestore")
    implementation("com.github.bumptech.glide:glide:4.12.0")
    implementation(libs.preference)
    annotationProcessor("com.github.bumptech.glide:compiler:4.12.0")

    // Geolocation
    implementation("org.osmdroid:osmdroid-android:6.1.14")
    
    // Uncomment when Generating JavaDoc and then comment after
    // implementation(files("/Users/edwin/Library/Android/sdk/platforms/android-35/android.jar"))
    //implementation(files("C:\\Users\\matra\\AppData\\Local\\Android\\Sdk\\platforms\\android-34\\android.jar"))

    // Zebra dependencies
    implementation("com.journeyapps:zxing-android-embedded:4.3.0")
    implementation("com.google.zxing:core:3.4.1")

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.firebase.storage)
    implementation(libs.firebase.database)

    // Unit testing dependencies
    testImplementation(libs.junit)

    // UI testing dependencies
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)


    // Add missing Espresso dependencies explicitly
    androidTestImplementation (libs.espresso.core.v351)
    androidTestImplementation(libs.test.core)
    androidTestImplementation (libs.espresso.intents)
    androidTestImplementation (libs.rules)
    androidTestImplementation (libs.runner)
    androidTestImplementation (libs.junit.v115)
    androidTestImplementation(libs.mockito.core)
    androidTestImplementation (libs.test.rules)
    androidTestImplementation ("androidx.test.espresso:espresso-contrib:3.5.1"){
        exclude(group = "com.google.protobuf", module = "protobuf-lite")
    }
    androidTestImplementation ("androidx.test:runner:1.5.2")
}


