DESCRIPTION = "MediaTek Dpframework Prot Prebuilt"

LICENSE = "CLOSED"
LIC_FILES_CHKSUM = "file://PROPRIETARY_LICENSE;md5=b67afae1a9957c1092fa01d76f5fe6d3"

COMPATIBLE_MACHINE = "mt8391|mt8371"

BRANCH = "main"

SRC_URI = "${G01_AIOT_LSA_URI}/camera/isp6s/dpframework-prot-prebuilt;protocol=https;branch=${BRANCH};"
SRCREV = "f1c11a9c465ebcb3c951b7602651217be73ab748"

S = "${WORKDIR}/git/libdpframework_prot.v5/common"

do_configure[noexec] = "1"
do_compile[noexec]   = "1"

do_install() {
    # Install library
    install -d ${D}${libdir}
    install -m 0644 ${S}/arm64_armv8-2a_cortex-a55/libdpframework_prot.v5.a ${D}${libdir}/libdpframework_prot.a

    # Install header
    install -d ${D}${includedir}
    install -m 0644 ${S}/include/common/*.h ${D}${includedir}/
    install -m 0644 ${S}/include/v5/*.h ${D}${includedir}/
}

INSANE_SKIP:${PN} += "already-stripped"
INSANE_SKIP:${PN} += "file-rdeps"
INSANE_SKIP:${PN} += "dev-so"

ALLOW_EMPTY:${PN} = "1"
FILES:${PN}-staticdev += "${libdir}/libdpframework_prot.a"
FILES:${PN}-dev += " \
    ${includedir}/*.h \
"