DESCRIPTION = "MediaTek Dpframework Prot Prebuilt"

LICENSE = "CLOSED"

COMPATIBLE_MACHINE = "mt8391|mt8371"

BRANCH = "main"

SRC_URI = "${G01_AIOT_LSA_URI}/camera/isp6s/libcamera-drv-prebuilt;protocol=ssh;branch=${BRANCH};"
SRCREV = "846e71a6fdc3b98021baf3cf90aee4d907843dec"

S = "${WORKDIR}/git/libcam.halsensor.hwintegration/mt8189"

PATCHTOOL = "git"
EXTRA_PATCH_FLAGS += "--ignore-whitespace"

do_configure[noexec] = "1"
do_compile[noexec]   = "1"

do_install() {
    # Install library
   	install -D ${S}/arm64/* -t ${D}${libdir}

    # Install header
    install -d ${D}${includedir}/libhw_integration/mt8189
    install -m 0644 ${S}/include/libhw_integration/mt8189/*.h ${D}${includedir}/
}

INSANE_SKIP:${PN} += "already-stripped"
INSANE_SKIP:${PN} += "file-rdeps"

SOLIBS = ".so"
FILES_SOLIBSDEV = ""
ALLOW_EMPTY:${PN} = "1"
FILES:${PN} += "${libdir}/*.so"