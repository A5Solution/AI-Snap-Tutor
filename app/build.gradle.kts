plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
    id("kotlin-kapt")
}

android {
    namespace = "com.example.aisnaptutor"
    compileSdk = 34

    defaultConfig {
        applicationId = "sprxtech.photosolve.homework.questionsolver.picanswer.scan.ai.tutorapp"
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
            resValue("string", "admob_app_id", "ca-app-pub-7927705861954420~9063655505")
            resValue("string", "admob_rewarded_id", "ca-app-pub-3940256099942544/5224354917")
            resValue("string", "admob_inter_id", "ca-app-pub-7927705861954420/6903098852")
            resValue("string", "admob_native_id", "ca-app-pub-7927705861954420/9720833883")
            resValue("string", "admob_open_id", "ca-app-pub-7927705861954420/2419507487")
        }
        debug {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            resValue("string", "admob_app_id", "ca-app-pub-4219822921938965~6037175836")
            resValue("string", "admob_rewarded_id", "ca-app-pub-7927705861954420/8954547128")
            resValue("string", "admob_inter_id", "ca-app-pub-3940256099942544/1033173712")
            resValue("string", "admob_native_id", "ca-app-pub-3940256099942544/2247696110")
            resValue("string", "admob_open_id", "ca-app-pub-3940256099942544/9257395921")
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
}

dependencies {

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.7")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.7")
    implementation("androidx.lifecycle:lifecycle-process:2.7.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    implementation("com.squareup.okhttp3:okhttp:4.11.0")
    implementation ("androidx.camera:camera-core:1.3.2")
    implementation ("androidx.camera:camera-camera2:1.3.2")
    implementation ("androidx.camera:camera-lifecycle:1.3.2")
    implementation ("androidx.camera:camera-view:1.3.2")
    implementation ("androidx.camera:camera-extensions:1.3.2")
    implementation ("com.google.android.gms:play-services-mlkit-text-recognition:19.0.0")
    implementation ("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.2")

    //Image cropping
    api ("com.theartofdev.edmodo:android-image-cropper:2.8.0")
    implementation ("com.squareup.picasso:picasso:2.5.2")

    //Firebase
    implementation("com.google.firebase:firebase-config-ktx:21.6.2")
    implementation("com.google.firebase:firebase-analytics:21.5.1")
    implementation("com.google.firebase:firebase-crashlytics:18.6.2")

    //Admob ads
    implementation ("com.google.android.gms:play-services-ads:22.6.0")

    //Room database
    val room_version = "2.6.1"

    implementation("androidx.room:room-runtime:$room_version")
    annotationProcessor("androidx.room:room-compiler:$room_version")
    implementation ("androidx.room:room-ktx:$room_version")
    kapt ("androidx.room:room-compiler:$room_version")

    //Localization
    implementation ("com.akexorcist:localization:1.2.11")
}