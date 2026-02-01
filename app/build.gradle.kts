plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
}

// --- LÓGICA DE VARIABLES (Fuera del bloque android para mayor estabilidad) ---
val supabaseUrl: String = System.getenv("TAXIFIT_SUPABASE_URL")
    ?: project.findProperty("TAXIFIT_SUPABASE_URL")?.toString() ?: ""

val supabaseAnonKey: String = System.getenv("TAXIFIT_SUPABASE_ANON_KEY")
    ?: project.findProperty("TAXIFIT_SUPABASE_ANON_KEY")?.toString() ?: ""

val googleWebClientId: String = System.getenv("TAXIFIT_GOOGLE_WEB_CLIENT_ID")
    ?: project.findProperty("TAXIFIT_GOOGLE_WEB_CLIENT_ID")?.toString() ?: ""

// Imprime en la consola de Gradle para que verifiques que se están leyendo
println("Taxifit Config Check -> URL: ${supabaseUrl.isNotEmpty()}, Key: ${supabaseAnonKey.isNotEmpty()}, GoogleID: ${googleWebClientId.isNotEmpty()}")

android {
    namespace = "com.mm.taxifit"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.mm.taxifit"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Inyección de variables en el código Java/Kotlin
        buildConfigField("String", "TAXIFIT_SUPABASE_URL", "\"$supabaseUrl\"")
        buildConfigField("String", "TAXIFIT_SUPABASE_ANON_KEY", "\"$supabaseAnonKey\"")
        buildConfigField("String", "TAXIFIT_GOOGLE_WEB_CLIENT_ID", "\"$googleWebClientId\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            // Aseguramos que el BuildConfig esté disponible en debug
            buildConfigField("String", "TAXIFIT_SUPABASE_URL", "\"$supabaseUrl\"")
            buildConfigField("String", "TAXIFIT_SUPABASE_ANON_KEY", "\"$supabaseAnonKey\"")
            buildConfigField("String", "TAXIFIT_GOOGLE_WEB_CLIENT_ID", "\"$googleWebClientId\"")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material3.adaptive.navigation.suite)
    implementation(libs.androidx.compose.material.icons.core)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services.auth)
    implementation(libs.googleid)

    // Supabase & Ktor
    implementation(platform(libs.supabase.bom))
    implementation(libs.supabase.auth)
    implementation(libs.ktor.client.okhttp)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}