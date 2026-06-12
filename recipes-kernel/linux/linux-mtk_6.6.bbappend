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

