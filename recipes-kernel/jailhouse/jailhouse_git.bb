SUMMARY = "Linux-based partitioning hypervisor"
DESCRIPTION = "Jailhouse is a partitioning Hypervisor based on Linux. It is able to run bare-metal applications or (adapted) \
operating systems besides Linux. For this purpose, it configures CPU and device virtualization features of the hardware \
platform in a way that none of these domains, called 'cells' here, can interfere with each other in an unacceptable way."
HOMEPAGE = "https://github.com/siemens/jailhouse"
SECTION = "jailhouse"
LICENSE = "GPL-2.0-only"

LIC_FILES_CHKSUM = "file://COPYING;md5=9fa7f895f96bde2d47fd5b7d95b6ba4d \
                 file://tools/root-cell-config.c.tmpl;beginline=6;endline=33;md5=2825581c1666c44a17955dc574cfbfb3 \
                 file://include/jailhouse/hypercall.h;beginline=9;endline=36;md5=2825581c1666c44a17955dc574cfbfb3 \
                 file://include/jailhouse/cell-config.h;beginline=9;endline=36;md5=2825581c1666c44a17955dc574cfbfb3 \
                 file://include/arch/arm/asm/jailhouse_hypercall.h;beginline=9;endline=36;md5=2825581c1666c44a17955dc574cfbfb3 \
                 file://include/arch/arm64/asm/jailhouse_hypercall.h;beginline=9;endline=36;md5=2825581c1666c44a17955dc574cfbfb3 \
                 file://include/arch/x86/asm/jailhouse_hypercall.h;beginline=9;endline=36;md5=2825581c1666c44a17955dc574cfbfb3 \
                 file://driver/jailhouse.h;beginline=9;endline=36;md5=2825581c1666c44a17955dc574cfbfb3 \
"

PV = "0.12+git${SRCPV}"
SRCREV = "e57d1eff6d55aeed5f977fe4e2acfb6ccbdd7560"
SRCREV = "${AUTOREV}"
BRANCH = "MTK-Genio"

SRC_URI = " \
    git://github.com/ffreimann-mtk/jailhouse.git;protocol=https;branch=${BRANCH} \
    file://0001-LF-10135-driver-update-the-use-of-of_overlay_fdt_app.patch \
    file://0001-Create-patchable-CONFIG_INMATE_BASE-define.patch \
    file://config.h \
"

DEPENDS = " \
    make-native \
    python3-mako-native \
    python3-mako \
    dtc-native \
"

inherit module python3native bash-completion deploy setuptools3

S = "${WORKDIR}/git"
B = "${S}"

JH_ARCH = "arm64"
JH_DATADIR ?= "${datadir}/jailhouse"
CELL_DIR ?= "${JH_DATADIR}/cells"
CELLCONF_DIR ?= "${JH_DATADIR}/configs"
INMATES_DIR ?= "${JH_DATADIR}/inmates"
INMATES_DTB_DIR ?= "${JH_DATADIR}/inmates/dtb"

# Inmate dtbs are manually installed (empty == all)
JH_INMATE_DTB ?= ""

JH_CONFIG_VENDOR = "mediatek"
JH_CONFIG_INMATE_BASE = "0x00008000"
JH_CONFIG_SOC:mt8365-pumpkin = "mt8365"
JH_CONFIG_SOC:mt8370-pumpkin = "mt8370"
JH_CONFIG_SOC:mt8390-pumpkin = "mt8390"
JH_CONFIG_SOC:mt8370-evk     = "mt8370"
JH_CONFIG_SOC:mt8390-evk     = "mt8390"

do_configure() {
    if [ -d ${STAGING_DIR_HOST}/${CELLCONF_DIR} ]; then
        cp "${STAGING_DIR_HOST}/${CELLCONF_DIR}/"*.c ${S}/configs/${ARCH}/
    fi
}

do_compile:prepend() {
	local olddir=`pwd`

    cd ${S}   

    # Create the jailhouse config.h
    cp ${WORKDIR}/config.h ${S}/include/jailhouse/config.h
	
	# Create 'CONFIG_VENDOR_<vendor name>' where '<vendor name>'
	# is JH_CONFIG_VENDOR converted to upper case.
    upper=$(echo ${JH_CONFIG_VENDOR} | awk '{print toupper($0)}')
    sed -i -e 's#_CONFIG_VENDOR_#CONFIG_VENDOR_'"$upper"'#' ${S}/include/jailhouse/config.h
   
	# Create 'CONFIG_SOC_<soc name>' where '<soc name>'
	# is JH_CONFIG_SOC converted to upper case.
    upper=$(echo ${JH_CONFIG_SOC} | awk '{print toupper($0)}')
    sed -i -e 's#_CONFIG_SOC_#CONFIG_SOC_'"$upper"'#' ${S}/include/jailhouse/config.h

    # Set the inmate base address.
    sed -i -e 's#_CONFIG_INMATE_BASE_#${JH_CONFIG_INMATE_BASE}#' ${S}/include/jailhouse/config.h
    sed -i -e 's#_CONFIG_INMATE_BASE_#${JH_CONFIG_INMATE_BASE}#' ${S}/inmates/lib/include/inmate_common.h

	cd "$olddir"

    unset LDFLAGS
    oe_runmake V=1 CC="${CC}" \
        ARCH=${JH_ARCH} \
        CROSS_COMPILE=${TARGET_PREFIX} \
        CONFIG_VENDOR=${JH_CONFIG_VENDOR} \
        CONFIG_SOC=${JH_CONFIG_SOC} \
        KDIR=${STAGING_KERNEL_BUILDDIR}
}

do_install:prepend() {
    oe_runmake \
        PYTHON=python3 \
        V=1 \
        LDFLAGS="" \
        CC="${CC}" \
        ARCH=${JH_ARCH} \
        CROSS_COMPILE=${TARGET_PREFIX} \
        CONFIG_VENDOR=${JH_CONFIG_VENDOR} \
        CONFIG_SOC=${JH_CONFIG_SOC} \
        KDIR=${STAGING_KERNEL_BUILDDIR} \
        INSTALL_MOD_PATH=${D}${root_prefix} \
        firmwaredir=${nonarch_base_libdir}/firmware \
        DESTDIR=${D} install

    install -d ${D}${CELL_DIR}
    install ${B}/configs/${JH_ARCH}/*.cell ${D}${CELL_DIR}/

    install -d ${D}${INMATES_DIR}/tools/${JH_ARCH}
    install ${B}/inmates/demos/${JH_ARCH}/*.bin ${D}${INMATES_DIR}

    install -d ${D}${JH_DATADIR}/tools
    install ${B}/tools/jailhouse-cell-linux ${D}${JH_DATADIR}/tools
    install ${B}/tools/jailhouse-cell-stats ${D}${JH_DATADIR}/tools
    install ${B}/tools/jailhouse-config-collect ${D}${JH_DATADIR}/tools
    install ${B}/tools/jailhouse-config-create ${D}${JH_DATADIR}/tools
    install ${B}/tools/jailhouse-gcov-extract ${D}${JH_DATADIR}/tools
    install ${B}/tools/jailhouse-hardware-check ${D}${JH_DATADIR}/tools
    install ${B}/inmates/tools/${JH_ARCH}/linux-loader.bin ${D}${INMATES_DIR}/tools/${JH_ARCH}

    # inmate dtbs
    if [ -d ${B}/configs/${JH_ARCH}/dts ]; then
        install -d ${D}${INMATES_DTB_DIR}
        if [ -z "${JH_INMATE_DTB}" ]; then
            install ${B}/configs/${JH_ARCH}/dts/*.dtb ${D}${INMATES_DTB_DIR}
        else
            install ${B}/configs/${JH_ARCH}/dts/${JH_INMATE_DTB} ${D}${INMATES_DTB_DIR}
        fi
    fi
}

PACKAGE_BEFORE_PN = "pyjailhouse"

FILES:${PN} += "${nonarch_base_libdir}/firmware ${libexecdir} ${sbindir} ${JH_DATADIR}"
# Remove libdir/* appended by setuptools3-base.bbclass for module split to work correctly
FILES:${PN}:remove = "${libdir}/*"
FILES:pyjailhouse = "${PYTHON_SITEPACKAGES_DIR}"

RDEPENDS:${PN} += " \
    python3-curses \
    python3-datetime \
    python3-mmap \
"

RDEPENDS:pyjailhouse = " \
    python3-core \
    python3-ctypes \
    python3-fcntl \
    python3-shell \
"

INSANE_SKIP:${PN}  = " ldflags "
INSANE_SKIP:${PN} += " installed-vs-shipped "

COMPATIBLE_MACHINE = "i350-pumpkin|i510-pumpkin|i700-pumpkin|genio-510-evk|genio-700-evk"

