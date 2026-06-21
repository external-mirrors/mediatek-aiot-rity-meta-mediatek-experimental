DESCRIPTION = "MediaTek camera hal for ISP6s"
LICENSE = "CLOSED"

COMPATIBLE_MACHINE = "mt8391|mt8371"

BRANCH = "main"

SRC_URI = " \
    ${G01_AIOT_LSA_URI}/camera/isp6s/mtk-camisp-mw-isp6s;protocol=https;branch=${BRANCH};destsuffix=git/mtk-camisp-mw;name=mtk-camisp-mw \
    ${G01_AIOT_LSA_URI}/camera/isp6s/mtkcam;protocol=https;branch=${BRANCH};destsuffix=git/mtkcam;name=mtkcam \
    ${G01_AIOT_LSA_URI}/camera/isp6s/mtkcam3;protocol=https;branch=${BRANCH};destsuffix=git/mtkcam3;name=mtkcam3 \
    ${G01_AIOT_LSA_URI}/camera/isp6s/dpframework;protocol=https;branch=${BRANCH};destsuffix=git/dpframework;name=dpframework \
    ${G01_AIOT_LSA_URI}/camera/isp6s/custom-isp6s;protocol=https;branch=${BRANCH};destsuffix=git/custom;name=custom \
"
SRCREV_mtk-camisp-mw    = "dc28d9d5c1ade0d3140fc842cf9408225af37ece"
SRCREV_mtkcam           = "7f0b92cf76c950f3a930dedf99cc75b827575265"
SRCREV_mtkcam3          = "bdd22a73b96a3c196240f7d67528e9b6c1ee1eeb"
SRCREV_dpframework      = "d442dac77af2286b5bc62480eec5b0c2efc70b21"
SRCREV_custom           = "7735052cf2a34d103b343b3ab2d25ea1600f7931"

SRCREV_FORMAT = "mtk-camisp-mw_mtkcam_mtkcam3_dpframework_custom"

S = "${WORKDIR}/git/mtk-camisp-mw"

inherit pkgconfig meson systemd

DEPENDS += " \
    ninja-native \
    glog \
    gflags \
    gtest \
    libsync \
    libutils \
    libcutils \
    virtual/kernel \
    libion \
    jpeg \
    mtk-camisp-libcamera-mt8391 \
    dpframework-prot-prebuilt \
    libcamera-drv-prebuilt \
"

RDEPENDS:${PN} = " \
    gflags \
    glog \
    libsync \
    libutils \
    libcutils \
    jpeg \
"

TARGET_CFLAGS:append = " -I${STAGING_KERNEL_BUILDDIR}/${includedir} "

do_configure:prepend() {
    ln -sf "${S}/../mtkcam" "${S}/mtkcam"
    ln -sf "${S}/../mtkcam3" "${S}/mtkcam3"
    ln -sf "${S}/../dpframework" "${S}/dpframework"
    ln -sf "${S}/../custom" "${S}/custom"
}

do_install:prepend() {
    if [ "${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'systemd', '', d)}" = "systemd" ]; then
        if [ -f "${S}/camd-ng/lib/systemd/system/camd-ng.service" ]; then
            install -d ${D}${systemd_unitdir}/system
            install -m 644 ${S}/camd-ng/lib/systemd/system/camd-ng.service ${D}${systemd_unitdir}/system/
        else
            bbwarn "camd-ng.service not found at ${S}/camd-ng/lib/systemd/system/camd-ng.service, skipping install"
        fi
    fi
}

# Only register the systemd service when the unit file actually ships in
# the source tree; do_install:prepend above already skips installation
# (with a warning) when it is missing, so without this guard do_package
# would fail with "Didn't find service unit 'camd-ng.service'".
SYSTEMD_PACKAGES = "${@bb.utils.contains('DISTRO_FEATURES', 'systemd', '${PN}', '', d) if os.path.isfile(d.expand('${S}/camd-ng/lib/systemd/system/camd-ng.service')) else ''}"
SYSTEMD_SERVICE:${PN} = "${@'camd-ng.service' if os.path.isfile(d.expand('${S}/camd-ng/lib/systemd/system/camd-ng.service')) else ''}"

TOOLCHAIN = "clang"
RUNTIME = "llvm"

SOLIBS = ".so"
FILES_SOLIBSDEV = ""
ALLOW_EMPTY:${PN} = "1"

FILES:${PN} += " \
    ${libdir}/ \
    ${bindir} \
    ${systemd_unitdir}/system/camd-ng.service \
    /usr/share \
"
FILES:${PN}-dev += "${includedir}"
FILES:${PN}-staticdev += "${libdir}"

INSANE_SKIP:${PN} += "already-stripped file-rdeps ldflags"
INSANE_SKIP:${PN}-dev += "file-rdeps dev-elf ldflags"
INSANE_SKIP:${PN}-staticdev += "file-rdeps"
