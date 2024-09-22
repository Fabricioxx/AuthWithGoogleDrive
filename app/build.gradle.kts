plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.galactapp.authwithgoogledrive"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.galactapp.authwithgoogledrive"
        minSdk = 23
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
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
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes.add("META-INF/INDEX.LIST")
            // Se precisar excluir outros arquivos, você pode adicionar mais entradas:
             excludes.add("META-INF/DEPENDENCIES")
            // excludes.add("META-INF/NOTICE")
            // excludes.add("META-INF/LICENSE")
        }
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.firebase.auth)
    implementation(platform(libs.firebase.bom))
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

     // firebase auth ( com.google.android.gms:play-services-auth: )
    implementation(libs.play.services.auth)

    // google drive api
    implementation(libs.google.api.client)
    implementation(libs.google.oauth.client.jetty)
    implementation(libs.google.api.services.drive)

    //google drive api
   // implementation(libs.google.api.client) // Biblioteca base para fazer chamadas de API do Google.
    implementation(libs.google.api.client.android) // Integração específica para Android, facilitando o uso da API em dispositivos Android.
   // implementation(libs.google.api.services.drive) // Biblioteca específica para interagir com o Google Drive API.
    implementation(libs.google.auth.library.oauth2.http) // Biblioteca para autenticação OAuth2, necessária para acessar os serviços do Google com credenciais seguras.
    //implementation(libs.google.oauth.client.jetty) // Cliente OAuth2 com suporte ao servidor Jetty para facilitar o processo de autorização.

    // coil - imagem
    implementation(libs.coil.compose)


}