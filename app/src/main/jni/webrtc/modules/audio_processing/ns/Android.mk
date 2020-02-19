LOCAL_PATH:= $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := libwebrtc_ns

LOCAL_CFLAGS += -DWEBRTC_ANDROID

LOCAL_C_INCLUDES := $(JNI_PATH)

LOCAL_CFLAGS += -DWEBRTC_NS_FIXED

#$(warning $(LOCAL_C_INCLUDES))


LOCAL_SRC_FILES := noise_suppression.c \
                   noise_suppression_x.c \
                   ns_core.c \
                   nsx_core.c \
                   nsx_core_c.c

ifeq ($(TARGET_ARCH_ABI),armeabi)

#LOCAL_SRC_FILES += nsx_core_c.c

else ifeq ($(TARGET_ARCH_ABI),armeabi-v7a)

LOCAL_CFLAGS += -DWEBRTC_HAS_NEON
#LOCAL_CFLAGS += -mfloat-abi=softfp
#LOCAL_CFLAGS += -mfpu=neon

LOCAL_SRC_FILES += nsx_core_neon.c



LOCAL_ARM_NEON  := true

else ifeq ($(TARGET_ARCH_ABI),arm64-v8a)

LOCAL_CFLAGS += -DWEBRTC_HAS_NEON
LOCAL_CFLAGS += -DWEBRTC_ARCH_ARM64
#LOCAL_CFLAGS += -mfloat-abi=softfp
#LOCAL_CFLAGS += -mfpu=neon

LOCAL_SRC_FILES += nsx_core_neon.c



LOCAL_ARM_NEON  := true

endif #TARGET_ARCH_ABI == armeabi-v7a

include $(BUILD_STATIC_LIBRARY)