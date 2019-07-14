load("//:import_external.bzl", import_external = "safe_exodus_maven_import_external")

def dependencies():

  import_external(
      name = "com_google_inject_guice",
      artifact = "com.google.inject:guice:3.0",
      artifact_sha256 = "1a59d0421ffd355cc0b70b42df1c2e9af744c8a2d0c92da379f5fca2f07f1d22",
      deps = [
          "@aopalliance_aopalliance",
          "@javax_inject_javax_inject"
      ],
  )
