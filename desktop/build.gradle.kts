import org.jetbrains.compose.desktop.application.nativeinstallers.AbstractInstallerProperties
import org.jetbrains.compose.desktop.application.nativeInstallers.windows.WixToolset

plugins {
  kotlin("multiplatform")
  id("org.jetbrains.compose")
  id("kotlin-serialization")
}

kotlin {
  jvm()
  sourceSets {
    val jvmMain by getting {
      dependencies {
        implementation(compose.desktop.currentOs)
        implementation(compose.material3)
        implementation(libs.kotlin.stdlib)
        implementation(libs.kotlinx.coroutines.core)
        implementation(libs.kotlinx.coroutines.swing)
        implementation(libs.kotlinx.serialization.json)
        implementation(libs.retrofit)
        implementation(libs.okhttp)
        implementation(libs.logging.interceptor)
        implementation(libs.retrofit.converter.kotlinx.serialization)
        implementation(libs.androidx.room.runtime)
        implementation(libs.androidx.room.ktx)
      }
    }
    val jvmTest by getting
  }
}

compose.desktop {
  application {
    mainClass = "com.example.desktop.MainKt"
    nativeDistributions {
      targetFormats(TargetFormat.Exe, TargetFormat.Msi)
      packageName = "Holy Financial"
      packageVersion = "1.0.0"
      description = "Aplicativo de Finanças para Igreja"
      copyright = "© 2026 Holy Financial. All rights reserved."
      vendor = "Holy Financial"
      licenseFile.set(project.file("LICENSE.txt"))
      windows {
        iconFile.set(project.file("src/main/resources/icon.ico"))
        menuGroup = "Holy Financial"
        perUserInstall = true
        dirChooser = true
        upgradeUuid = "12345678-1234-1234-1234-123456789012"
        msi {
          packageVersion = "1.0.0"
          upgradeUuid = "12345678-1234-1234-1234-123456789012"
        }
      }
    }
  }
}
