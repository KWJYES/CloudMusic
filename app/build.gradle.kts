plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-kapt")
}

android {
    compileSdk = 34
    namespace = "com.example.cloudmusic"
    defaultConfig {
        applicationId = "com.example.cloudmusic"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
//        ndk {
//            abiFilters("arm64-v8a", "armeabi-v7a")
//        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    defaultConfig {
        vectorDrawables.useSupportLibrary = true
    }

    dataBinding {
        isEnabled = true
    }
}

dependencies {
    implementation("com.github.bumptech.glide:glide:4.11.0")
    implementation("androidx.legacy:legacy-support-v4:1.0.0")
    kapt("com.github.bumptech.glide:compiler:4.11.0")
    implementation("androidx.cardview:cardview:1.0.0")
    implementation("de.hdodenhof:circleimageview:2.1.0")
    implementation("org.litepal.guolindev:core:3.2.3")
    implementation("io.github.youth5201314:banner:2.2.2")
    implementation("com.github.Justson.AgentWeb:agentweb-core:v5.0.0-alpha.1-androidx")
    implementation("com.github.Justson.AgentWeb:agentweb-filechooser:v5.0.0-alpha.1-androidx")
    implementation("com.github.Justson:Downloader:v5.0.0-androidx")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.google.code.gson:gson:2.8.9")
    implementation("com.wang.avi:library:2.1.3")
    implementation("org.greenrobot:eventbus:3.3.1")
    implementation("io.github.scwang90:refresh-layout-kernel:2.0.5")
    implementation("io.github.scwang90:refresh-header-material:2.0.5")
    implementation("io.github.scwang90:refresh-header-classics:2.0.5")
    implementation("io.github.scwang90:refresh-footer-classics:2.0.5")
    implementation("io.github.scwang90:refresh-footer-ball:2.0.5")
    implementation("com.github.franmontiel:PersistentCookieJar:v1.0.1")
    implementation("jp.wasabeef:glide-transformations:4.1.0")
    implementation("androidx.appcompat:appcompat:1.2.0")
    implementation("com.google.android.material:material:1.3.0")
    implementation("androidx.constraintlayout:constraintlayout:2.0.4")
    testImplementation("junit:junit:4.+")
    androidTestImplementation("androidx.test.ext:junit:1.1.2")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.3.0")
    implementation("androidx.core:core-ktx:1.3.2")
//    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7:$kotlin_version")
}
