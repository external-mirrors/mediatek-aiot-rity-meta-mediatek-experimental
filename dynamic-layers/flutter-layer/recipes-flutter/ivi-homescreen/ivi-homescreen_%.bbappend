
HOMESCREEN_COMMIT = "22365b6251e34ef0fea33f6ec569b77a664b5dfa"
PLUGINS_COMMIT = "d3aa99df5678e423157a58d64e74d81168255285"

DEPENDS:append = " vulkan-loader "

PACKAGECONFIG:remove = " \
    backend-wayland-egl \
    egl-3d \
    egl-transparency \
    egl-multisample \
"

PACKAGECONFIG:append = " backend-wayland-vulkan "
