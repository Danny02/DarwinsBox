load("//:import_external.bzl", import_external = "safe_exodus_maven_import_external")

def dependencies():

  import_external(
      name = "junit_junit",
      artifact = "junit:junit:4.12",
      artifact_sha256 = "59721f0805e223d84b90677887d9ff567dc534d7c502ca903c0c2b17f05c116a",
      deps = [
          "@org_hamcrest_hamcrest_core"
      ],
  )
