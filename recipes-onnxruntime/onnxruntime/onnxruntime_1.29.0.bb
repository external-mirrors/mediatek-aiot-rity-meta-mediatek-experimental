DESCRIPTION = "ONNX Runtime is an open-source scoring engine for Open Neural \
Network Exchange (ONNX) models. ONNX Runtime has an open architecture that \
is continually evolving to address the newest developments and challenges \
in AI and Deep Learning."
SUMMARY = "ONNX Runtime"
HOMEPAGE = "https://github.com/microsoft/onnxruntime"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=0f7e3b1308cb5c00b372a6e78835732d"
PACKAGES += "${PN}-examples ${PN}-examples-dbg"
SRCREV_FORMAT = "onnxruntime"
SRCREV_onnxruntime = "v${PV}"
S = "${WORKDIR}/git"
OECMAKE_SOURCEPATH = "${S}/cmake"
inherit python3native python3-dir cmake
SRC_URI = "gitsm://github.com/microsoft/onnxruntime.git;protocol=https;branch=rel-${PV};name=onnxruntime \
           file://0001-linux-device-discovery-accept-non-PCI-DRM-cards.patch \
           file://0002-python-do-not-call-built-in-WebGPU-factory-under-EP-API-adapters.patch \
           file://efficientnet-lite4/efficientnet-lite4.onnx \
           file://onnxruntime_example/onnxruntime_test.py \
           file://onnxruntime_example/labels_map.txt \
           file://onnxruntime_example/kitten.jfif \
           "
DEPENDS += " \
    cmake-native \
    zlib \
    python3 \
    python3-numpy \
    python3-numpy-native \
"
EXTRA_OECMAKE=" \
    -DCMAKE_BUILD_TYPE=MinSizeRel \
    -DCMAKE_SYSTEM_PROCESSOR=arm64 \
    -Donnxruntime_GCC_STATIC_CPP_RUNTIME=OFF \
    -Donnxruntime_BUILD_SHARED_LIB=ON \
    -Donnxruntime_DEV_MODE=OFF \
    -Donnxruntime_ENABLE_PYTHON=ON \
    -DFETCHCONTENT_FULLY_DISCONNECTED=OFF \
    -Donnxruntime_BUILD_UNIT_TESTS=OFF \
    -Donnxruntime_USE_FULL_PROTOBUF=ON \
    -Donnxruntime_USE_XNNPACK=ON \
    -Donnxruntime_USE_KLEIDIAI=ON \
    -DONNX_WERROR=OFF \
    -Donnxruntime_USE_WEBGPU=ON \
    -Donnxruntime_ENABLE_DAWN_BACKEND_VULKAN=ON \
    -Donnxruntime_USE_EP_API_ADAPTERS=ON \
    -Donnxruntime_BUILD_DAWN_SHARED_LIBRARY=OFF \
"

# ORT sets COMPILE_WARNING_AS_ERROR on all its targets and only suppresses GCC's
# -Wmaybe-uninitialized false positives for GCC 12.x; we build with GCC 13.4, so
# they become fatal (e.g. webgpu/math/softmax.h:22). This cmake option ignores the
# property globally without patching sources. Needs CMake >= 3.24.
EXTRA_OECMAKE += "--compile-no-warning-as-error"

TARGET_CFLAGS += "-Wno-unused-variable -Wno-unused-value"

# WGSL templates are generated at do_compile by tools/python/wgsl_gen.py and
# compiled into libonnxruntime_providers_webgpu, so nothing to do here.
# Network is still needed: FETCHCONTENT_FULLY_DISCONNECTED=OFF makes cmake
# download abseil/re2/etc. while configuring.
do_configure[network] = "1"

do_install() {
    install -d ${D}${libdir}
    install -d ${D}${includedir}/onnxruntime/core/
    install -d ${D}${libdir} ${D}${PYTHON_SITEPACKAGES_DIR}
    install -d ${D}${ROOT_HOME}/onnxruntime_example

    # libonnxruntime_providers_webgpu.so is the plugin EP (USE_EP_API_ADAPTERS=ON).
    # libwebgpu_dawn.so only exists when Dawn is built as a shared library; with the
    # plugin EP build Dawn is static, so that entry is skipped by the -f test.
    for so in ${B}/libonnxruntime*.so* ${B}/libonnxruntime_providers*.so* ${B}/libwebgpu_dawn.so; do
        [ -f "$so" ] && install -m 0644 "$so" ${D}${libdir}/
    done

    cp -r ${S}/include/onnxruntime/core/* ${D}${includedir}/onnxruntime/core/
    cp -r ${S}/include/onnxruntime/core/session/* ${D}${includedir}/onnxruntime/

    cp -r ${B}/onnxruntime ${D}${PYTHON_SITEPACKAGES_DIR}/

    install -m 644 ${WORKDIR}/efficientnet-lite4/efficientnet-lite4.onnx ${D}${ROOT_HOME}/onnxruntime_example
    install -m 644 ${WORKDIR}/onnxruntime_example/labels_map.txt ${D}${ROOT_HOME}/onnxruntime_example
    install -m 644 ${WORKDIR}/onnxruntime_example/kitten.jfif ${D}${ROOT_HOME}/onnxruntime_example
    install -m 644 ${WORKDIR}/onnxruntime_example/onnxruntime_test.py ${D}${ROOT_HOME}/onnxruntime_example
}

FILES_SOLIBSDEV = ""
FILES:${PN} += " \
    ${libdir}/* \
    ${libdir}/libonnxruntime*.so* \
    ${libdir}/libonnxruntime_providers*.so* \
"
FILES:${PN}-dev += " \
    ${includedir}/onnxruntime/* \
"
FILES:${PN}-staticdev = " \
    ${libdir}/*.a \
"
FILES:${PN}-examples = " \
    ${ROOT_HOME}/onnxruntime_example \
"
FILES:python3-${PN} = " \
    ${libdir}${PYTHON_DIR} \
    ${libdir}${PYTHON_SITEPACKAGES_DIR} \
    ${PYTHON_SITEPACKAGES_DIR}/onnxruntime \
"
