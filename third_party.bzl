load("//:third_party/org_slf4j.bzl", org_slf4j_deps = "dependencies")

load("//:third_party/org_jogamp_jogl.bzl", org_jogamp_jogl_deps = "dependencies")

load("//:third_party/org_jogamp_gluegen.bzl", org_jogamp_gluegen_deps = "dependencies")

load("//:third_party/org_hamcrest.bzl", org_hamcrest_deps = "dependencies")

load("//:third_party/junit.bzl", junit_deps = "dependencies")

load("//:third_party/javax_inject.bzl", javax_inject_deps = "dependencies")

load("//:third_party/com_google_inject.bzl", com_google_inject_deps = "dependencies")

load("//:third_party/com_google_inject_extensions.bzl", com_google_inject_extensions_deps = "dependencies")

load("//:third_party/com_google_guava.bzl", com_google_guava_deps = "dependencies")

load("//:third_party/com_google_code_findbugs.bzl", com_google_code_findbugs_deps = "dependencies")

load("//:third_party/com_github_jponge.bzl", com_github_jponge_deps = "dependencies")

load("//:third_party/com_github_danny02.bzl", com_github_danny02_deps = "dependencies")

load("//:third_party/com_fasterxml_jackson_core.bzl", com_fasterxml_jackson_core_deps = "dependencies")

load("//:third_party/aopalliance.bzl", aopalliance_deps = "dependencies")


load("//:macros.bzl", "maven_archive", "maven_proto")

def third_party_dependencies():
      

  aopalliance_deps()


  com_fasterxml_jackson_core_deps()


  com_github_danny02_deps()


  com_github_jponge_deps()


  com_google_code_findbugs_deps()


  com_google_guava_deps()


  com_google_inject_extensions_deps()


  com_google_inject_deps()


  javax_inject_deps()


  junit_deps()


  org_hamcrest_deps()


  org_jogamp_gluegen_deps()


  org_jogamp_jogl_deps()


  org_slf4j_deps()
