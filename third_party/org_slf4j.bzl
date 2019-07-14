load("//:import_external.bzl", import_external = "safe_exodus_maven_import_external")

def dependencies():

  import_external(
      name = "org_slf4j_slf4j_api",
      artifact = "org.slf4j:slf4j-api:1.7.7",
      artifact_sha256 = "69980c038ca1b131926561591617d9c25fabfc7b29828af91597ca8570cf35fe",
  )
