LOCAL_PATH:= $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := libwebrtc_aec

LOCAL_CFLAGS += -DWEBRTC_ANDROID

LOCAL_C_INCLUDES := $(LOCAL_PATH) \
                    $(JNI_PATH)

#$(warning $(LOCAL_C_INCLUDES))


LOCAL_SRC_FILES := aec_resampler.c \
                   echo_cancellation.c \
                   aec_core.c \
                   aec_rdft.c


ifeq ($(TARGET_ARCH_ABI),armeabi)

#LOCAL_SRC_FILES += aec_core.c


else ifeq ($(TARGET_ARCH_ABI),armeabi-v7a)

LOCAL_CFLAGS += -DWEBRTC_HAS_NEON
#LOCAL_CFLAGS += -mfloat-abi=softfp
#LOCAL_CFLAGS += -mfpu=neon

LOCAL_SRC_FILES += aec_core_neon.c \
                   aec_rdft_neon.c

LOCAL_ARM_NEON  := true

else ifeq ($(TARGET_ARCH_ABI),arm64-v8a)

LOCAL_CFLAGS += -DWEBRTC_HAS_NEON
LOCAL_CFLAGS += -DWEBRTC_ARCH_ARM64
#LOCAL_CFLAGS += -mfloat-abi=softfp
#LOCAL_CFLAGS += -mfpu=neon

LOCAL_SRC_FILES += aec_core_neon.c \
                   aec_rdft_neon.c

LOCAL_ARM_NEON  := true

endif #TARGET_ARCH_ABI == armeabi-v7a

include $(BUILD_STATIC_LIBRARY)