load("//:import_external.bzl", import_external = "safe_exodus_maven_import_external")

def dependencies():

  import_external(
      name = "com_google_code_findbugs_jsr305",
      artifact = "com.google.code.findbugs:jsr305:2.0.3",
      artifact_sha256 = "bec0b24dcb23f9670172724826584802b80ae6cbdaba03bdebdef9327b962f6a",
  )
