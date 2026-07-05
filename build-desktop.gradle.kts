// Build script para compilar apenas o módulo desktop
task("buildWindowsInstallers") {
  description = "Build Windows installers (.exe e .msi)"
  group = "build"
  dependsOn(":desktop:createDistributable")
  doLast {
    println("✓ Windows installers criados com sucesso!")
    println("  Localização: desktop/build/compose/binaries/main/msi")
    println("  Localização: desktop/build/compose/binaries/main/exe")
  }
}

task("runDesktopApp") {
  description = "Executar aplicativo desktop"
  group = "run"
  dependsOn(":desktop:run")
}
