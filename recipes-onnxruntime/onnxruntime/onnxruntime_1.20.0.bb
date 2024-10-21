DESCRIPTION = "ONNX Runtime is an open-source scoring engine for Open Neural \
Network Exchange (ONNX) models. ONNX Runtime has an open architecture that \
is continually evolving to address the newest developments and challenges \
in AI and Deep Learning."
SUMMARY = "ONNX Runtime"
HOMEPAGE = "https://github.com/microsoft/onnxruntime"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://../LICENSE;md5=0f7e3b1308cb5c00b372a6e78835732d"

PACKAGES += "${PN}-examples ${PN}-examples-dbg"

SRCREV_FORMAT = "onnxruntime"

SRCREV_onnxruntime = "c4fb724e810bb496165b9015c77f402727392933"

S = "${WORKDIR}/git/cmake"
T = "${WORKDIR}/git"

inherit python3native cmake

SRC_URI = "gitsm://github.com/microsoft/onnxruntime.git;protocol=https;branch=rel-${PV};name=onnxruntime \
           file://efficientnet-lite4/efficientnet-lite4.onnx \
           file://onnxruntime_example/onnxruntime_test.py \
           file://onnxruntime_example/labels_map.txt \
           file://onnxruntime_example/kitten.jfif \
           "

DEPENDS += " \
	cmake-native \
	protobuf-native \
	zlib \
	python3 \
	python3-numpy \
	python3-numpy-native \
"

PREFERRED_VERSION_CMAKE="3.26.4"

EXTRA_OECMAKE=" \
	-DCMAKE_BUILD_TYPE=Release \
	-DCMAKE_FIND_ROOT_PATH_MODE_PACKAGE=ONLY \
	-DCMAKE_FIND_ROOT_PATH_MODE_PROGRAM=NEVER \
	-DCMAKE_FIND_ROOT_PATH_MODE_LIBRARY=ONLY \
	-DCMAKE_FIND_ROOT_PATH_MODE_INCLUDE=ONLY \
	-DCMAKE_SYSTEM_PROCESSOR=arm64 \
	-Dprotobuf_WITH_ZLIB=OFF \
	-Donnxruntime_GCC_STATIC_CPP_RUNTIME=ON \
	-DCMAKE_FIND_ROOT_PATH=${STAGING_DIR_TARGET} \
	-Donnxruntime_BUILD_SHARED_LIB=ON \
	-Donnxruntime_DEV_MODE=OFF \
	-Donnxruntime_ENABLE_PYTHON=ON \
    -DFETCHCONTENT_FULLY_DISCONNECTED=OFF \
    -Donnxruntime_BUILD_UNIT_TESTS=OFF \
	-Donnxruntime_USE_XNNPACK=ON \
" 

# Workaround for network access issue during compile step
# this needs to be fixed in the recipes buildsystem to move
# this such that it can be accomplished during do_fetch task
do_compile[network] = "1"

do_install() {
	install -d ${D}${libdir}
    install -d ${D}${includedir}/core/providers

	cp --parents \
		$(find . -name "*.so") \
		${D}/${libdir}

	install -d ${D}/home/root/onnxruntime_example

    install -m 644 ${WORKDIR}/efficientnet-lite4/efficientnet-lite4.onnx ${D}/home/root/onnxruntime_example

	install -m 644 ${WORKDIR}/onnxruntime_example/labels_map.txt ${D}/home/root/onnxruntime_example

	install -m 644 ${WORKDIR}/onnxruntime_example/kitten.jfif ${D}/home/root/onnxruntime_example

	install -m 644 ${WORKDIR}/onnxruntime_example/onnxruntime_test.py ${D}/home/root/onnxruntime_example


	install -d ${D}${libdir}/${PYTHON_DIR}/site-packages

	cp -r \
		${WORKDIR}/build/onnxruntime/ \
		${D}${libdir}/${PYTHON_DIR}/site-packages

}

ALLOW_EMPTY:${PN} = "1"

FILES:${PN} = ""

FILES:${PN}-staticdev = " \
	${libdir} \
"

FILES:${PN}-examples = " \
	${bindir}/${PN} \
	/home/root/onnxruntime_example \
"

FILES:python3-${PN} = " \
	${libdir}/${PYTHON_DIR} \
	${libdir}/${PYTHON_DIR}/site-packages \
	${libdir}/${PYTHON_DIR}/site-packages/onnxruntime \
"



