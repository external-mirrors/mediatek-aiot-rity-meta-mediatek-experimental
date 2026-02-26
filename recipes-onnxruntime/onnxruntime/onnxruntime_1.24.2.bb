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
           file://efficientnet-lite4/efficientnet-lite4.onnx \
           file://onnxruntime_example/onnxruntime_test.py \
           file://onnxruntime_example/labels_map.txt \
           file://onnxruntime_example/kitten.jfif \
           "
DEPENDS += " \
    cmake-native \
    zlib \
    nodejs-native \
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
    -DONNX_WERROR=OFF \
    -Donnxruntime_USE_WEBGPU=ON \
    -Donnxruntime_WGSL_TEMPLATE=dynamic \
    -Donnxruntime_BUILD_DAWN_SHARED_LIBRARY=ON \
    -Donnxruntime_BUILD_WEBGPU_EP_STATIC_LIB=ON \
    -Donnxruntime_ENABLE_DAWN_BACKEND_VULKAN=ON \
"

TARGET_CFLAGS += "-Wno-unused-variable -Wno-unused-value"

do_configure:append() {
    cd ${S}/onnxruntime/core/providers/webgpu/wgsl_templates
    npm install
}

do_configure[network] = "1"

do_install() {
    install -d ${D}${libdir}
    install -d ${D}${includedir}/onnxruntime/core/
    install -d ${D}${libdir} ${D}${PYTHON_SITEPACKAGES_DIR}
    install -d ${D}${ROOT_HOME}/onnxruntime_example

    for so in ${B}/libonnxruntime*.so* ${B}/libonnxruntime_providers*.so*; do
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