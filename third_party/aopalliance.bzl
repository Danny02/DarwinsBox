load("//:import_external.bzl", import_external = "safe_exodus_maven_import_external")

def dependencies():

  import_external(
      name = "aopalliance_aopalliance",
      artifact = "aopalliance:aopalliance:1.0",
      artifact_sha256 = "0addec670fedcd3f113c5c8091d783280d23f75e3acb841b61a9cdb079376a08",
  )
