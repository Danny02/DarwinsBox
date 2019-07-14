load("//:import_external.bzl", import_external = "safe_exodus_maven_import_external")

def dependencies():

  import_external(
      name = "org_jogamp_gluegen_gluegen_rt",
      artifact = "org.jogamp.gluegen:gluegen-rt:2.1.5-01",
      artifact_sha256 = "57bd9f63ff301b3f95571e8a96add24966018eec331eb13f9786e382aef595d0",
  )


  import_external(
      name = "org_jogamp_gluegen_gluegen_rt_main",
      artifact = "org.jogamp.gluegen:gluegen-rt-main:2.1.5-01",
      artifact_sha256 = "cc114f4c0b29b2fe4d06a5c7931838814452a04cc9789e27b21ab05d3541c107",
      deps = [
          "@org_jogamp_gluegen_gluegen_rt",
          "@org_jogamp_gluegen_gluegen_rt_natives_android_armv6",
          "@org_jogamp_gluegen_gluegen_rt_natives_linux_amd64",
          "@org_jogamp_gluegen_gluegen_rt_natives_linux_armv6",
          "@org_jogamp_gluegen_gluegen_rt_natives_linux_armv6hf",
          "@org_jogamp_gluegen_gluegen_rt_natives_linux_i586",
          "@org_jogamp_gluegen_gluegen_rt_natives_macosx_universal",
          "@org_jogamp_gluegen_gluegen_rt_natives_solaris_amd64",
          "@org_jogamp_gluegen_gluegen_rt_natives_solaris_i586",
          "@org_jogamp_gluegen_gluegen_rt_natives_windows_amd64",
          "@org_jogamp_gluegen_gluegen_rt_natives_windows_i586"
      ],
  )


  import_external(
      name = "org_jogamp_gluegen_gluegen_rt_natives_android_armv6",
      artifact = "org.jogamp.gluegen:gluegen-rt:jar:natives-android-armv6:2.1.5-01",
      artifact_sha256 = "b51299c4597823ceaaf6d9540eb8c1584a8eccf69eea6a242ce2d1af26d9f8e4",
  )


  import_external(
      name = "org_jogamp_gluegen_gluegen_rt_natives_linux_amd64",
      artifact = "org.jogamp.gluegen:gluegen-rt:jar:natives-linux-amd64:2.1.5-01",
      artifact_sha256 = "1e5d0bf4ad658141cc67ba829790d2ed6b7f88aad567d3d0a284e33b026f2c22",
  )


  import_external(
      name = "org_jogamp_gluegen_gluegen_rt_natives_linux_armv6",
      artifact = "org.jogamp.gluegen:gluegen-rt:jar:natives-linux-armv6:2.1.5-01",
      artifact_sha256 = "1e08dfd8342c81c50ae587cbfa76e1b84fef0bcbae5b8f2ba5abcbffa0b49beb",
  )


  import_external(
      name = "org_jogamp_gluegen_gluegen_rt_natives_linux_armv6hf",
      artifact = "org.jogamp.gluegen:gluegen-rt:jar:natives-linux-armv6hf:2.1.5-01",
      artifact_sha256 = "c7cab3f41d0eccd35ae5332f33dbb0fad5c90b2db46f6b40d29440b5352f7d17",
  )


  import_external(
      name = "org_jogamp_gluegen_gluegen_rt_natives_linux_i586",
      artifact = "org.jogamp.gluegen:gluegen-rt:jar:natives-linux-i586:2.1.5-01",
      artifact_sha256 = "46b9cc9094f0d80732fd5e61d7284f52e0a1e0124102114e7face46d58e9bcea",
  )


  import_external(
      name = "org_jogamp_gluegen_gluegen_rt_natives_macosx_universal",
      artifact = "org.jogamp.gluegen:gluegen-rt:jar:natives-macosx-universal:2.1.5-01",
      artifact_sha256 = "04873a8d8eb1c5a937ee24c05b9a7d8ab702fc068b1bc0e56e927ac26f7f7780",
  )


  import_external(
      name = "org_jogamp_gluegen_gluegen_rt_natives_solaris_amd64",
      artifact = "org.jogamp.gluegen:gluegen-rt:jar:natives-solaris-amd64:2.1.5-01",
      artifact_sha256 = "318d28a0d5e86211cd6e062c9272513b7ea82ba670a5a94ba857b551185e1c09",
  )


  import_external(
      name = "org_jogamp_gluegen_gluegen_rt_natives_solaris_i586",
      artifact = "org.jogamp.gluegen:gluegen-rt:jar:natives-solaris-i586:2.1.5-01",
      artifact_sha256 = "7aaff78f4934d2ff439d663084134c8cb6ebb3a87ad503adab47b428f318d0cd",
  )


  import_external(
      name = "org_jogamp_gluegen_gluegen_rt_natives_windows_amd64",
      artifact = "org.jogamp.gluegen:gluegen-rt:jar:natives-windows-amd64:2.1.5-01",
      artifact_sha256 = "28aa6c69c9eca4f15786a30b217ebc497066680b016b75febd96f38c927ca406",
  )


  import_external(
      name = "org_jogamp_gluegen_gluegen_rt_natives_windows_i586",
      artifact = "org.jogamp.gluegen:gluegen-rt:jar:natives-windows-i586:2.1.5-01",
      artifact_sha256 = "6942607e741369ad84044be3ed7ffd784f5a87cad08d6892bcaafe867fe94486",
  )
