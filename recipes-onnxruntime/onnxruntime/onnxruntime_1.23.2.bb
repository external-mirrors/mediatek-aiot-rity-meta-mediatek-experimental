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
	   file://0001-fix-range-loop-copy.patch \
	   file://efficientnet-lite4/efficientnet-lite4.onnx \
	   file://onnxruntime_example/onnxruntime_test.py \
	   file://onnxruntime_example/labels_map.txt \
	   file://onnxruntime_example/kitten.jfif \
	   "

DEPENDS += " \
	cmake-native \
	protobuf \
	protobuf-native \
	zlib \
	python3 \
	python3-numpy \
	python3-numpy-native \
"

EXTRA_OECMAKE=" \
	-DCMAKE_BUILD_TYPE=MinSizeRel \
	-DONNX_CUSTOM_PROTOC_EXECUTABLE=${STAGING_BINDIR_NATIVE}/protoc \
	-DCMAKE_FIND_ROOT_PATH_MODE_PACKAGE=ONLY \
	-DCMAKE_FIND_ROOT_PATH_MODE_PROGRAM=NEVER \
	-DCMAKE_FIND_ROOT_PATH_MODE_LIBRARY=ONLY \
	-DCMAKE_FIND_ROOT_PATH_MODE_INCLUDE=ONLY \
	-DCMAKE_FIND_ROOT_PATH=${STAGING_DIR_TARGET} \
	-DCMAKE_SYSTEM_PROCESSOR=arm64 \
	-Dprotobuf_WITH_ZLIB=OFF \
	-Donnxruntime_GCC_STATIC_CPP_RUNTIME=OFF \
	-Donnxruntime_BUILD_SHARED_LIB=ON \
	-Donnxruntime_DEV_MODE=OFF \
	-Donnxruntime_ENABLE_PYTHON=ON \
	-DFETCHCONTENT_FULLY_DISCONNECTED=OFF \
	-Donnxruntime_BUILD_UNIT_TESTS=OFF \
	-Donnxruntime_USE_XNNPACK=ON \
	-DONNX_WERROR=OFF \
" 

# Workaround for network access issue during compile step
# this needs to be fixed in the recipes buildsystem to move
# this such that it can be accomplished during do_fetch task
do_configure[network] = "1"

do_install() {
	install -d ${D}${libdir}
	install -d ${D}${includedir}/core/providers
	install -d ${D}${libdir} ${D}${PYTHON_SITEPACKAGES_DIR}
	install -d ${D}${ROOT_HOME}/onnxruntime_example

	for so in ${B}/libonnxruntime*.so* ${B}/libonnxruntime_providers*.so*; do
	[ -f "$so" ] && install -m 0644 "$so" ${D}${libdir}/
	done 

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
FILES:${PN}-staticdev = " \
	${libdir}/*.a \
"

FILES:${PN}-examples = " \
	${ROOT_HOME}/onnxruntime_example \
"

FILES:python3-${PN} = " \
	{libdir}${PYTHON_DIR} \
	${libdir}${PYTHON_SITEPACKAGES_DIR} \
	${libdir}${PYTHON_SITEPACKAGES_DIR}/onnxruntime \
"
