load("//:import_external.bzl", import_external = "safe_exodus_maven_import_external")

def dependencies():

  import_external(
      name = "com_github_danny02_AnnotationsProcessing",
      artifact = "com.github.danny02:AnnotationsProcessing:1.1",
      artifact_sha256 = "1db57446a5efabbc7ab5d010d3703c4992f7f262ac5081e99e0abdbf8d780ab6",
      deps = [
          "@com_google_code_findbugs_jsr305"
      ],
  )
