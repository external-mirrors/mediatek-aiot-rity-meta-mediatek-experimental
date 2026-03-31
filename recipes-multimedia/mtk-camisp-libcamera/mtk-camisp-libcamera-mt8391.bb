DESCRIPTION = "MediaTek Camera ISP Prebuilt libcamera"
LICENSE = "CLOSED"

#inherit module
COMPATIBLE_MACHINE = "mt8391|mt8371"

BRANCH = "main"

S = "${WORKDIR}/git"

SRC_URI:mt8391 = "${G01_AIOT_LSA_URI}/camera/isp6s/libcamera-mt8391.git;protocol=ssh;branch=${BRANCH}"
SRCREV:mt8391 = "fd285933b7f117ae745f2b35004d3a3ebc55cb63"

#inherit pkgconfig meson
do_install() {
	install -D "${S}/arm64/clang"/* -t "${D}${libdir}"

	install -d ${D}${includedir}/libcamalgo_3dnr
	install -m 644 ${S}/include/libcamalgo_3dnr/*.h ${D}${includedir}/libcamalgo_3dnr

	install -d ${D}${includedir}/libcamalgo_eis
	install -m 644 ${S}/include/libcamalgo_eis/*.h ${D}${includedir}/libcamalgo_eis

	install -d ${D}${includedir}/libcamalgo_gyro
	install -m 644 ${S}/include/libcamalgo_gyro/*.h ${D}${includedir}/libcamalgo_gyro

	install -d ${D}${includedir}/libcamalgo_lmv
	install -m 644 ${S}/include/libcamalgo_lmv/*.h ${D}${includedir}/libcamalgo_lmv

	install -d ${D}${includedir}/libcamalgo_lsc
	install -m 644 ${S}/include/libcamalgo_lsc/*.h ${D}${includedir}/libcamalgo_lsc

	install -d ${D}${includedir}/libcamalgo_mfnr
	install -m 644 ${S}/include/libcamalgo_mfnr/*.h ${D}${includedir}/libcamalgo_mfnr

	install -d ${D}${includedir}/libcamalgo_platform
	install -m 644 ${S}/include/libcamalgo_platform/*.h ${D}${includedir}/libcamalgo_platform

	install -d ${D}${includedir}/libfsc
	install -m 644 ${S}/include/libfsc/*.h ${D}${includedir}/libfsc

	install -d ${D}${includedir}/aaa_ae
	install -m 644 ${S}/include/aaa_ae/*.h ${D}${includedir}/aaa_ae

	install -d ${D}${includedir}/aaa_af
	install -m 644 ${S}/include/aaa_af/*.h ${D}${includedir}/aaa_af

	install -d ${D}${includedir}/aaa_awb
	install -m 644 ${S}/include/aaa_awb/*.h ${D}${includedir}/aaa_awb

	install -d ${D}${includedir}/aaa_ltm
	install -m 644 ${S}/include/aaa_ltm/*.h ${D}${includedir}/aaa_ltm

	install -d ${D}${includedir}/aaa_dce
	install -m 644 ${S}/include/aaa_dce/*.h ${D}${includedir}/aaa_dce

	install -d ${D}${includedir}/aaa_gma
	install -m 644 ${S}/include/aaa_gma/*.h ${D}${includedir}/aaa_gma

	install -d ${D}${includedir}/aaa_lce
	install -m 644 ${S}/include/aaa_lce/*.h ${D}${includedir}/aaa_lce

	install -d ${D}${includedir}/aaa_hlr
	install -m 644 ${S}/include/aaa_hlr/*.h ${D}${includedir}/aaa_hlr

	install -d ${D}${includedir}/aaa_flicker
	install -m 644 ${S}/include/aaa_flicker/*.h ${D}${includedir}/aaa_flicker

	install -d ${D}${includedir}/aaa_flickerutil
	install -m 644 ${S}/include/aaa_flickerutil/*.h ${D}${includedir}/aaa_flickerutil

	install -d ${D}${includedir}/libcamalgo_dngop
	install -m 644 ${S}/include/libcamalgo_dngop/*.h ${D}${includedir}/libcamalgo_dngop

	install -d ${D}${includedir}/aaa_ae_core
	install -m 644 ${S}/include/aaa_ae_core/*.h ${D}${includedir}/aaa_ae_core
}

INSANE_SKIP:${PN} += "already-stripped"
INSANE_SKIP:${PN} += "file-rdeps"

SOLIBS = ".so"
FILES_SOLIBSDEV = ""
ALLOW_EMPTY_${PN} = "1"
FILES_${PN} = "${libdir}/*.so ${bindir}"
