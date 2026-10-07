import java.io.FileInputStream
import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
}

fun bacaAlamat(kunci: String): String {
    val teks = file("../alamat.json").readText()
    val cocok = Regex("\"$kunci\"\\s*:\\s*\"?([^\",\\s}]+)\"?").find(teks)
    return cocok?.groupValues?.get(1)
        ?: error("alamat.json: kolom \"$kunci\" tidak ditemukan")
}

val sifatKeystore = Properties().apply {
    val f = file("keystore.properties")
    if (f.exists()) FileInputStream(f).use { load(it) }
}

android {
    namespace = "com.zxparenting.ortu"
    compileSdk = 35

    defaultConfig {
        applicationId = bacaAlamat("idPaket")
        minSdk = 24
        targetSdk = 35
        versionCode = bacaAlamat("versiKode").toInt()
        versionName = bacaAlamat("versiNama")
        buildConfigField("String", "VERSI_NAMA", "\"${bacaAlamat("versiNama")}\"")
        buildConfigField("int", "VERSI_KODE", bacaAlamat("versiKode"))
    }

    signingConfigs {
        if (sifatKeystore.isNotEmpty()) {
            create("rilis") {
                storeFile = file(sifatKeystore.getProperty("storeFile"))
                storePassword = sifatKeystore.getProperty("storePassword")
                keyAlias = sifatKeystore.getProperty("keyAlias")
                keyPassword = sifatKeystore.getProperty("keyPassword")
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            if (sifatKeystore.isNotEmpty()) {
                signingConfig = signingConfigs.getByName("rilis")
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlin { compilerOptions { jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17) } }
    buildFeatures { compose = true; buildConfig = true }
}

dependencies {
    val composeBom = platform("androidx.compose:compose-bom:2024.12.01")
    implementation(composeBom)
    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.activity:activity-compose:1.9.3")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.7")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")
    implementation("androidx.datastore:datastore-preferences:1.1.1")
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
    // Google Sign-In via Credential Manager (minSdk 24 = pakai credentials-play-services-auth)
    implementation("androidx.credentials:credentials:1.3.0")
    implementation("androidx.credentials:credentials-play-services-auth:1.3.0")
    implementation("com.google.android.libraries.identity.googleid:googleid:1.1.1")
    // In-App Update (Play Core) — cek update Play Store saat app dibuka
    implementation("com.google.android.play:app-update:2.1.0")
    implementation("com.google.android.play:app-update-ktx:2.1.0")
}
