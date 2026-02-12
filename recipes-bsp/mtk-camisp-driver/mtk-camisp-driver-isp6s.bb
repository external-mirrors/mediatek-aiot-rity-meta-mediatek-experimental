DESCRIPTION = "MediaTek camera ISP6s out-of-tree kernel driver"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=6760c75814a7f07fc569c7ca1fd5c35b"

COMPATIBLE_MACHINE = "mt8391|mt8371"

inherit module

SRCREV = "e1dc03758807332bf36cd6e0bc1ee09daac4bb53"

BRANCH = "dev/isp6s"

SRC_URI += "${AIOT_BSP_URI}/mtk-camisp-driver.git;protocol=https;branch=${BRANCH} \
"

S = "${WORKDIR}/git"

python() {
    plat = d.getVar('SOC_FAMILY', True)
    if plat == 'mt8370':
        plat = 'mt8188'
    d.setVar('PLATFORM', plat)
}

EXTRA_OEMAKE:append = " PLATFORM=${PLATFORM} "

# The inherit of module.bbclass will automatically name module packages with
# "kernel-module-" prefix as required by the oe-core build environment.

RPROVIDES:${PN} += "kernel-module-${BPN}"
