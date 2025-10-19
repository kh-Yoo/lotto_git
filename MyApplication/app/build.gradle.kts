plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.myapplication"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.example.myapplication"
        minSdk = 24
        targetSdk = 36
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation ("androidx.appcompat:appcompat:1.6.1")
    implementation ("com.google.android.material:material:1.11.0")
    implementation ("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation ("androidx.recyclerview:recyclerview:1.3.2")

    // Retrofit (네트워크 통신)
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
// Or a more recent version
    implementation("com.google.android.material:material:1.11.0")
// Or a more recent version
    implementation("androidx.recyclerview:recyclerview:1.3.2")
// Or a more recent version
    // Room (로컬 데이터베이스)
    implementation ("androidx.room:room-runtime:2.6.1")
    annotationProcessor ("androidx.room:room-compiler:2.6.1")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    // 🎯 Jsoup 라이브러리 추가
    implementation("org.jsoup:jsoup:1.17.2")

    // Java에서 비동기 처리를 위한 라이브러리 (필요 시 추가)
    // implementation 'androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2'
    // implementation 'androidx.lifecycle:lifecycle-runtime-ktx:2.6.2'
}