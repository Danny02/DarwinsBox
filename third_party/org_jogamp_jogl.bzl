load("//:import_external.bzl", import_external = "safe_exodus_maven_import_external")

def dependencies():

  import_external(
      name = "org_jogamp_jogl_jogl_all",
      artifact = "org.jogamp.jogl:jogl-all:2.1.5-01",
      artifact_sha256 = "f031877805b078ec80e765e2e362fd0cdc4da8d709fe424b3a593003fe104fb6",
  )


  import_external(
      name = "org_jogamp_jogl_jogl_all_main",
      artifact = "org.jogamp.jogl:jogl-all-main:2.1.5-01",
      artifact_sha256 = "cc114f4c0b29b2fe4d06a5c7931838814452a04cc9789e27b21ab05d3541c107",
      deps = [
          "@org_jogamp_jogl_jogl_all",
          "@org_jogamp_jogl_jogl_all_natives_android_armv6",
          "@org_jogamp_jogl_jogl_all_natives_linux_amd64",
          "@org_jogamp_jogl_jogl_all_natives_linux_armv6",
          "@org_jogamp_jogl_jogl_all_natives_linux_armv6hf",
          "@org_jogamp_jogl_jogl_all_natives_linux_i586",
          "@org_jogamp_jogl_jogl_all_natives_macosx_universal",
          "@org_jogamp_jogl_jogl_all_natives_solaris_amd64",
          "@org_jogamp_jogl_jogl_all_natives_solaris_i586",
          "@org_jogamp_jogl_jogl_all_natives_windows_amd64",
          "@org_jogamp_jogl_jogl_all_natives_windows_i586"
      ],
  )


  import_external(
      name = "org_jogamp_jogl_jogl_all_natives_android_armv6",
      artifact = "org.jogamp.jogl:jogl-all:jar:natives-android-armv6:2.1.5-01",
      artifact_sha256 = "f30b95590f971e3b959a1f8e162ceb74e61d2d3abb98d6c0df19dee328346d0f",
  )


  import_external(
      name = "org_jogamp_jogl_jogl_all_natives_linux_amd64",
      artifact = "org.jogamp.jogl:jogl-all:jar:natives-linux-amd64:2.1.5-01",
      artifact_sha256 = "1b095a64713776c314e2b0157e59a2d1bebefb3d6228d49e478abdba57c72187",
  )


  import_external(
      name = "org_jogamp_jogl_jogl_all_natives_linux_armv6",
      artifact = "org.jogamp.jogl:jogl-all:jar:natives-linux-armv6:2.1.5-01",
      artifact_sha256 = "5b3dd004e1fa33009236da897a24ad3bcc388349b34e80a3b73fb63ec08a6b57",
  )


  import_external(
      name = "org_jogamp_jogl_jogl_all_natives_linux_armv6hf",
      artifact = "org.jogamp.jogl:jogl-all:jar:natives-linux-armv6hf:2.1.5-01",
      artifact_sha256 = "867aff1536617e618ff00d6d071c78babf21d4357cbcda93ca50b92aca60441a",
  )


  import_external(
      name = "org_jogamp_jogl_jogl_all_natives_linux_i586",
      artifact = "org.jogamp.jogl:jogl-all:jar:natives-linux-i586:2.1.5-01",
      artifact_sha256 = "3f6f3c460f0c5bab1c9043f4068c80c4960678e29f074ea87b76e42e9137b26e",
  )


  import_external(
      name = "org_jogamp_jogl_jogl_all_natives_macosx_universal",
      artifact = "org.jogamp.jogl:jogl-all:jar:natives-macosx-universal:2.1.5-01",
      artifact_sha256 = "91b45ce150a40998b7ff183cf6a9fe628f5f780b630f07f37622814bcfee5386",
  )


  import_external(
      name = "org_jogamp_jogl_jogl_all_natives_solaris_amd64",
      artifact = "org.jogamp.jogl:jogl-all:jar:natives-solaris-amd64:2.1.5-01",
      artifact_sha256 = "d23ffebca3301689b2fd22f77de61010fbf698bf3bc0ab8e003e2d337e5eef7d",
  )


  import_external(
      name = "org_jogamp_jogl_jogl_all_natives_solaris_i586",
      artifact = "org.jogamp.jogl:jogl-all:jar:natives-solaris-i586:2.1.5-01",
      artifact_sha256 = "bb212365f9393e3e68e869fcde100f642a52b5ae879b9a250a164958bbfd886a",
  )


  import_external(
      name = "org_jogamp_jogl_jogl_all_natives_windows_amd64",
      artifact = "org.jogamp.jogl:jogl-all:jar:natives-windows-amd64:2.1.5-01",
      artifact_sha256 = "bf24a8fcd0db87a656f715515700ea9a9389050ea52998fba5fccfc88b37a300",
  )


  import_external(
      name = "org_jogamp_jogl_jogl_all_natives_windows_i586",
      artifact = "org.jogamp.jogl:jogl-all:jar:natives-windows-i586:2.1.5-01",
      artifact_sha256 = "3f231f1b069ef3dced9013f9401be271e699ba6c930f70b1ba776e9b34b585bb",
  )
