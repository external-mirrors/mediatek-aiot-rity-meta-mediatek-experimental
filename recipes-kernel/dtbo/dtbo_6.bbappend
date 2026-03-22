# Copyright (C) 2024 Julien Stephan <jstephan@baylibre.com>
# Released under the MIT license (see COPYING.MIT for the terms)
FILESEXTRAPATHS:prepend:mt8365-evk := "${THISDIR}/v6/mt8365-evk:"
FILESEXTRAPATHS:prepend:mt8365:= "${THISDIR}/v6/mt8365:"
FILESEXTRAPATHS:prepend:genio-510-evk:= "${THISDIR}/v6/genio-510-evk:"
FILESEXTRAPATHS:prepend:mt8370:= "${THISDIR}/v6/mt8370:"
FILESEXTRAPATHS:prepend:genio-700-evk:= "${THISDIR}/v6/genio-700-evk:"
FILESEXTRAPATHS:prepend:mt8390:= "${THISDIR}/v6/mt8390:"
FILESEXTRAPATHS:prepend:genio-1200-evk:= "${THISDIR}/v6/genio-1200-evk:"
FILESEXTRAPATHS:prepend:mt8395:= "${THISDIR}/v6/mt8395:"
FILESEXTRAPATHS:prepend:genio-720-evk := "${THISDIR}/v6/genio-720-evk:"
FILESEXTRAPATHS:prepend:mt8391 := "${THISDIR}/v6/mt8391:"
FILESEXTRAPATHS:prepend:genio-360-evk := "${THISDIR}/v6/genio-360-evk:"
FILESEXTRAPATHS:prepend:mt8367 := "${THISDIR}/v6/mt8367:"

SRC_URI:append:genio-720-evk = " \
	file://isp6s.dtsi \
	file://camera-common.dtsi \
	file://camera-max96724a-csi0.dts \
	file://camera-max96724b-csi1.dts \
	file://camera-max96724a-csi0-max96724b-csi1.dts \
	file://camera-max96724a-csi0-single-csi1.dts \
	file://camera-single-csi0-max96724b-csi1.dts \
	file://camera-single-csi0-single-csi1.dts \
	file://camera-single-csi0.dts \
	file://camera-single-csi1.dts \
"
