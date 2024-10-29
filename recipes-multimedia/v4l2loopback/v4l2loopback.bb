require v4l2loopback.inc

inherit module

S = "${WORKDIR}/git"

EXTRA_OEMAKE += "KERNEL_DIR=${STAGING_KERNEL_DIR}"
EXTRA_OEMAKE += "PREFIX=${D}${prefix}"

MAKE_TARGETS = "v4l2loopback"
MODULES_INSTALL_TARGET = "install"

RPROVIDES:${PN} += "kernel-module-v4l2loopback"

RDEPENDS:${PN} += " \
	v4l2loopback-ctl \
"
