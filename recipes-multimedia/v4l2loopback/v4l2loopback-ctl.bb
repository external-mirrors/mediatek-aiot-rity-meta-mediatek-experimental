require v4l2loopback.inc

LIC_FILES_CHKSUM = "file://../COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

S = "${WORKDIR}/git/utils"

DEPENDS += "glibc"

EXTRA_OEMAKE = "DESTDIR=${D}"

do_install() {
	oe_runmake install
}

FILES:${PN} = "${bindir}/v4l2loopback-ctl"

RDEPENDS:${PN} += " \
	gstreamer1.0 \
	gstreamer1.0-plugins-base \
	gstreamer1.0-plugins-good \
	v4l-utils \
"
