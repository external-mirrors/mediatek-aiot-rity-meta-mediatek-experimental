FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

SRC_URI:append = " \
	${@bb.utils.contains('IMAGE_INSTALL', 'jailhouse', 'file://6.6-Added-Jailhouse-exports-and-prevent-switch-to-VHE.patch', '', d)} \
	${@bb.utils.contains('IMAGE_INSTALL', 'jailhouse', 'file://0001-uio-Enable-read-only-mappings.patch', '', d)} \
	${@bb.utils.contains('IMAGE_INSTALL', 'jailhouse', 'file://0002-uio-Enable-read-only-mappings-compile-fix.patch', '', d)} \
	${@bb.utils.contains('IMAGE_INSTALL', 'jailhouse', 'file://0003-ivshmem-Add-header-file.patch', '', d)} \
	${@bb.utils.contains('IMAGE_INSTALL', 'jailhouse', 'file://0004-uio-Add-driver-for-inter-VM-shared-memory-device.patch', '', d)} \
	${@bb.utils.contains('IMAGE_INSTALL', 'jailhouse', 'file://0005-WIP-virtio-Add-virtio-over-ivshmem-transport-driver.patch', '', d)} \
	${@bb.utils.contains('IMAGE_INSTALL', 'jailhouse', 'file://0006-virtio-ivshmem-check-peer_state-early.patch', '', d)} \
	${@bb.utils.contains('IMAGE_INSTALL', 'jailhouse', 'file://0007-WIP-tools-Add-virtio-ivshmem-console-demo.patch', '', d)} \
	${@bb.utils.contains('IMAGE_INSTALL', 'jailhouse', 'file://0008-WIP-tools-Add-virtio-ivshmem-block-demo.patch', '', d)} \
"

SRC_URI:append:genio-510-evk = " \
    ${@bb.utils.contains('IMAGE_INSTALL', 'jailhouse', 'file://6.6-Genio-510-evk-Jailhouse-memory-reservation-and-UART-settings.patch', '', d)} \
"

SRC_URI:append:genio-700-evk = " \
    ${@bb.utils.contains('IMAGE_INSTALL', 'jailhouse', 'file://6.6-Genio-700-evk-Jailhouse-memory-reservation-and-UART-settings.patch', '', d)} \
"

SRC_URI:append:genio-720-evk = " \
    ${@bb.utils.contains('DISTRO_VERSION', '25.1.1-release', 'file://0001-BACKPORT-UPSTREAM-dma-buf-dma-heap-export-declared-f.patch', '', d)} \
    ${@bb.utils.contains('DISTRO_VERSION', '25.1.1-release', 'file://0001-GENIO-arm64-dts-mediatek-mt8189-Add-support-for-came.patch', '', d)} \
    ${@bb.utils.contains('DISTRO_VERSION', '25.1.1-release', 'file://0002-GENIO-include-soc-mediatek-Add-colorimetry-info-to-h.patch', '', d)} \
    ${@bb.utils.contains('DISTRO_VERSION', '25.1.1-release', 'file://0003-GENIO-include-soc-mediatek-mtk-hdmirx-intf-Add-quant.patch', '', d)} \
    ${@bb.utils.contains('DISTRO_VERSION', '25.1.1-release', 'file://0016-GENIO-media-platform-mtk-mdp3-add-colorimetry-info-t.patch', '', d)} \
    ${@bb.utils.contains('DISTRO_VERSION', '25.1.1-release', 'file://0017-GENIO-media-platform-mtk-mdp3-add-component-config-f.patch', '', d)} \
    ${@bb.utils.contains('DISTRO_VERSION', '25.1.1-release', 'file://0018-GENIO-media-platform-mtk-mdp3-add-MT8195-MDP-compone.patch', '', d)} \
    ${@bb.utils.contains('DISTRO_VERSION', '25.1.1-release', 'file://0019-GENIO-media-platform-mtk-mdp3-add-MT8195-topology-fo.patch', '', d)} \
    ${@bb.utils.contains('DISTRO_VERSION', '25.1.1-release', 'file://0020-GENIO-media-platform-mtk-mdp3-enable-MT8195-kernel-a.patch', '', d)} \
    ${@bb.utils.contains('DISTRO_VERSION', '25.1.1-release', 'file://0021-GENIO-media-platform-mtk-mdp3-add-kernel-algorithm-f.patch', '', d)} \
    ${@bb.utils.contains('DISTRO_VERSION', '25.1.1-release', 'file://0022-GENIO-media-platform-mtk-mdp3-avoid-extra-SCP-contro.patch', '', d)} \
    ${@bb.utils.contains('DISTRO_VERSION', '25.1.1-release', 'file://0023-GENIO-media-platform-mtk-mdp3-extend-colorspace-and-.patch', '', d)} \
    ${@bb.utils.contains('DISTRO_VERSION', '25.1.1-release', 'file://0024-GENIO-media-platform-mtk-mdp3-add-support-HDMI-RX-HD.patch', '', d)} \
    ${@bb.utils.contains('DISTRO_VERSION', '25.1.1-release', 'file://0025-GENIO-media-platform-mtk-mdp3-force-BT.709-xfer_func.patch', '', d)} \
    ${@bb.utils.contains('DISTRO_VERSION', '25.1.1-release', 'file://0026-GENIO-media-platform-mtk-mdp3-add-support-for-MT8171.patch', '', d)} \
    ${@bb.utils.contains('DISTRO_VERSION', '25.1.1-release', 'file://0027-GENIO-media-platform-mtk-mdp3-fix-NULL-pointer-deref.patch', '', d)} \
    ${@bb.utils.contains('DISTRO_VERSION', '25.1.1-release', 'file://0028-GENIO-media-platform-mtk-mdp3-fix-dead-code-in-mdp_a.patch', '', d)} \
    ${@bb.utils.contains('DISTRO_VERSION', '25.1.1-release', 'file://0029-GENIO-media-platform-mtk-mdp3-fix-enum-used-as-boole.patch', '', d)} \
    ${@bb.utils.contains('DISTRO_VERSION', '25.1.1-release', 'file://0030-GENIO-media-platform-mtk-mdp3-fix-array-overrun-unde.patch', '', d)} \
    ${@bb.utils.contains('DISTRO_VERSION', '25.1.1-release', 'file://0031-GENIO-media-platform-mtk-mdp3-fix-use-after-free-in-.patch', '', d)} \
    ${@bb.utils.contains('DISTRO_VERSION', '25.1.1-release', 'file://0032-GENIO-media-platform-mtk-mdp3-add-MT8171-WROT-data-c.patch', '', d)} \
    ${@bb.utils.contains('DISTRO_VERSION', '25.1.1-release', 'file://0033-GENIO-media-platform-mtk-mdp3-initialize-CMDQ-client.patch', '', d)} \
    ${@bb.utils.contains('DISTRO_VERSION', '25.1.1-release', 'file://0034-GENIO-media-platform-mtk-mdp3-support-alpha-series-R.patch', '', d)} \
    ${@bb.utils.contains('DISTRO_VERSION', '25.1.1-release', 'file://0035-GENIO-media-platform-mtk-mdp3-fix-memory-leak-in-til.patch', '', d)} \
"
