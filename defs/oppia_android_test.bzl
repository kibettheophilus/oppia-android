"""
Central macros pertaining to setting up tests across the codebase.
"""

load("@io_bazel_rules_kotlin//kotlin:android.bzl", "kt_android_library")
load(":generate_android_manifest.bzl", "generate_android_manifest")

_TEST_MIN_SDK_VERSION = 19
_TEST_TARGET_SDK_VERSION = 30

# TODO: Update docs and document that org.oppia.android is main resources package, by default.
def oppia_android_test(
        name,
        srcs,
        deps,
        temp_test_class_migration = None,
        android_merge_deps = [],
        include_robolectric_support = True,
        runtime_deps = []):
    """
    Creates a local Oppia test target with Kotlin support.

    Note that this creates an additional, internal library.

    Args:
      name: str. The name of the Kotlin test file without the '.kt' suffix.
      srcs: list of str. The name of the Kotlin test files to be run.
      test_manifest: str. The path to the test manifest file.
      test_class: The package of the src file. For example, if the src is 'FakeEventLoggerTest.kt',
          then the test_class would be "org.oppia.testing.FakeEventLoggerTest".
      enable_data_binding: boolean. Indicates whether the test enables data-binding.
      deps: list of str. The list of dependencies needed to run the tests.
      additional_runtime_deps: list of str. The list of runtime-only dependencies needed to run the
          tests.
      assets: list of str. A list of assets needed to run the tests.
      assets_dir: str. The path to the assets directory.
    """

    # TODO: Double-check that all tests using resources are doing it through the new way.
    # TODO: Remove resources_package bit once off Gradle (since tests can then use local resources). Might be able to get rid of manifest gen, too.
    resources_package = "org.oppia.android"
    test_manifest = _generate_test_android_manifest(name, package = resources_package)
    include_support_for_android_resources = len(android_merge_deps) != 0

    extra_kt_lib_dep = []
    if temp_test_class_migration == None:
        kt_android_library(
            name = name + "_lib",
            custom_package = resources_package if include_support_for_android_resources else None,
            srcs = srcs,
            deps = deps,
            testonly = True,
            manifest = test_manifest if include_support_for_android_resources else None,
            android_merge_deps = android_merge_deps,
        )
        extra_kt_lib_dep = [":" + name + "_lib"]

    native.android_local_test(
        name = name,
        test_class = temp_test_class_migration,
        manifest = test_manifest,
        manifest_values = {
            "applicationId": resources_package,
            "minSdkVersion": "%s" % _TEST_MIN_SDK_VERSION,
            "targetSdkVersion": "%s" % _TEST_TARGET_SDK_VERSION,
            "versionCode": "0",
            "versionName": "0.1-test",
        },
        deps = extra_kt_lib_dep + deps + ([
            "//third_party:robolectric_android-all",
        ] if include_robolectric_support else []),
        runtime_deps = runtime_deps + ([
            "//third_party:org_robolectric_robolectric",
        ] if include_robolectric_support else []),
    )

def _generate_test_android_manifest(name, package):
    return generate_android_manifest(
        name,
        package,
        min_sdk_version = _TEST_MIN_SDK_VERSION,
        target_sdk_version = _TEST_TARGET_SDK_VERSION,
    )