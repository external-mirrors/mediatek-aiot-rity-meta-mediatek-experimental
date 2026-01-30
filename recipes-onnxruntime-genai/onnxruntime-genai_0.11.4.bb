SUMMARY = "ONNX Runtime GenAI"
HOMEPAGE = "https://github.com/microsoft/onnxruntime-genai"
# Based on recipe from: https://github.com/NobuoTsukamoto/meta-onnxruntime/tree/main/recipes-framework/onnxruntime-genai
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d4a904ca135bb7bc912156fee12726f0"

SRCREV = "v${PV}"

SRC_URI = " \
    gitsm://github.com/microsoft/onnxruntime-genai;branch=rel-${PV};protocol=https \
    file://0001-Update_ort_include_dir.patch \
"

S = "${WORKDIR}/git"

DEPENDS = " \
    python3 \
    python3-pybind11 \
    python3-pip-native \
    python3-wheel-native \
    nlohmann-json \
    onnxruntime \
"

RDEPENDS:${PN} = " \
    python3 \
    python3-numpy \
    python3-pybind11 \
    onnxruntime \
"

inherit cmake python3-dir

python() {
    d.setVar("PYTHON_VERSION_ORT_GENAI", d.getVar("PYTHON_BASEVERSION").replace(".", ""))
}

EXTRA_OECMAKE:append = " \
    -DCMAKE_BUILD_TYPE=Release \
    -DUSE_CUDA=OFF \
    -DUSE_ROCM=OFF \
    -DUSE_DML=OFF \
    -DENABLE_JAVA=OFF \
    -DBUILD_WHEEL=ON \
    -DENABLE_PYTHON=ON \
    -DUSE_GUIDANCE=OFF \
    -DENABLE_TESTS=OFF \
    -DORT_HOME=${RECIPE_SYSROOT}/usr \
    -DPYTHON_EXECUTABLE=${STAGING_BINDIR_NATIVE}/${PYTHON_PN}-native/${PYTHON_PN} \
    -DFETCHCONTENT_FULLY_DISCONNECTED=OFF \
    -DPYTHON_MODULE_EXTENSION=.cpython-${PYTHON_VERSION_ORT_GENAI}-${TARGET_ARCH}-linux-gnu.so \
"

do_configure[network] = "1"

do_compile:append() {
    cd ${WORKDIR}/build/wheel
    ${STAGING_BINDIR_NATIVE}/${PYTHON_PN}-native/${PYTHON_PN} -m pip wheel --no-deps .
}

do_install:append() {
    install -d ${D}${PYTHON_SITEPACKAGES_DIR}
    ${STAGING_BINDIR_NATIVE}/${PYTHON_PN}-native/${PYTHON_PN} -m pip install --disable-pip-version-check -v \
       -t ${D}${PYTHON_SITEPACKAGES_DIR} --no-cache-dir --no-deps \
        ${WORKDIR}/build/wheel/onnxruntime_genai-${PV}-*.whl

    rm -rf ${D}${PYTHON_SITEPACKAGES_DIR}/onnxruntime_genai/libonnxruntime-genai.so
}

FILES:${PN} += " \
    ${PYTHON_SITEPACKAGES_DIR}/onnxruntime_genai-${DPV}.dist-info/* \
    ${PYTHON_SITEPACKAGES_DIR}/onnxruntime_genai/* \
    ${libdir} \
    /usr/README.md \
    /usr/ThirdPartyNotices.txt \
    /usr/SECURITY.md \
    /usr/LICENSE \
"

FILES:${PN}-dev = "${includedir}"
INSANE_SKIP:${PN} += "buildpaths already-stripped"