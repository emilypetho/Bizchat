plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.pethoemilia.client"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.pethoemilia.client"
        minSdk = 29
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
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.google.android.material:material:1.4.0")
    implementation(libs.circleimageview)
    implementation(libs.converter.gson)
    implementation(libs.kotlinx.coroutines.android)
    implementation (libs.circleimageview)
    implementation(libs.activity)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation(libs.jbcrypt)
    implementation("com.rabbitmq:amqp-client:5.21.0")

}