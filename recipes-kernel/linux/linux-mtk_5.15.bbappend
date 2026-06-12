FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

SRC_URI:append = " \
	${@bb.utils.contains('IMAGE_INSTALL', 'jailhouse', 'file://5.15-Added-Jailhouse-exports-and-prevent-switch-to-VHE.patch', '', d)} \
"

SRC_URI:append:i350-pumpkin = " \
    ${@bb.utils.contains('IMAGE_INSTALL', 'jailhouse', 'file://i350-pumpkin-Jailhouse-memory-reservation-and-UART-settings.patch', '', d)} \
"
