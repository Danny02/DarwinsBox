load("//:import_external.bzl", import_external = "safe_exodus_maven_import_external")

def dependencies():

  import_external(
      name = "com_google_guava_guava",
      artifact = "com.google.guava:guava:17.0",
      artifact_sha256 = "8c36a80ea613d0b6b8040a17cf837c5bbe3677bc1b06a058a6c174fdb787ebbc",
  )
