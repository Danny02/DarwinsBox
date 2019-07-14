load("//:import_external.bzl", import_external = "safe_exodus_maven_import_external")

def dependencies():

  import_external(
      name = "com_fasterxml_jackson_core_jackson_annotations",
      artifact = "com.fasterxml.jackson.core:jackson-annotations:2.3.0",
      artifact_sha256 = "0c8c3811322cc84c09a93f34436fe784a1259dd5376a90aec5a73493456f757d",
  )


  import_external(
      name = "com_fasterxml_jackson_core_jackson_core",
      artifact = "com.fasterxml.jackson.core:jackson-core:2.3.3",
      artifact_sha256 = "11c9651fd29f0bd87b0014fc3ae18d1157c1d69f1a3b90c97ecb7a179d5d7450",
  )


  import_external(
      name = "com_fasterxml_jackson_core_jackson_databind",
      artifact = "com.fasterxml.jackson.core:jackson-databind:2.3.3",
      artifact_sha256 = "46b5cdab6f8d6aad830ead3acafc9cd4970bd0054d12a4d9b5f4eb6efa70cf4a",
      deps = [
          "@com_fasterxml_jackson_core_jackson_annotations",
          "@com_fasterxml_jackson_core_jackson_core"
      ],
  )
