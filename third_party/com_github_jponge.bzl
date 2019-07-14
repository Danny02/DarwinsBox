load("//:import_external.bzl", import_external = "safe_exodus_maven_import_external")

def dependencies():

  import_external(
      name = "com_github_jponge_lzma_java",
      artifact = "com.github.jponge:lzma-java:1.3",
      artifact_sha256 = "75ccc0e24d37ed5b8916e1bcffad1e067c2b1cfa21b635287f968db5c3c1963a",
  )
