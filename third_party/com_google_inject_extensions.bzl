load("//:import_external.bzl", import_external = "safe_exodus_maven_import_external")

def dependencies():

  import_external(
      name = "com_google_inject_extensions_guice_assistedinject",
      artifact = "com.google.inject.extensions:guice-assistedinject:3.0",
      artifact_sha256 = "29a0e823babf10e28c6d3c71b2f9d56a3be2c9696d016fb16258e3fb1d184cf1",
      deps = [
          "@com_google_inject_guice"
      ],
  )
