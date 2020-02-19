LOCAL_PATH:= $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := libwebrtc_common_audio_resampler

LOCAL_CFLAGS += -DWEBRTC_ANDROID

LOCAL_CPPFLAGS += -std=c++11
LOCAL_CFLAGS += -DWEBRTC_POSIX

LOCAL_C_INCLUDES := $(JNI_PATH)

#$(warning $(LOCAL_C_INCLUDES))


LOCAL_SRC_FILES := push_resampler.cc \
                   push_sinc_resampler.cc \
                   resampler.cc \
                   sinusoidal_linear_chirp_source.cc \
                   sinc_resampler.cc

ifeq ($(TARGET_ARCH_ABI),armeabi)

#LOCAL_SRC_FILES += sinc_resampler.cc

else ifeq ($(TARGET_ARCH_ABI),armeabi-v7a)

LOCAL_CFLAGS += -DWEBRTC_HAS_NEON
#LOCAL_CFLAGS += -mfloat-abi=softfp
#LOCAL_CFLAGS += -mfpu=neon

LOCAL_SRC_FILES += sinc_resampler_neon.cc



LOCAL_ARM_NEON  := true


else ifeq ($(TARGET_ARCH_ABI),arm64-v8a)

LOCAL_CFLAGS += -DWEBRTC_HAS_NEON
LOCAL_CFLAGS += -DWEBRTC_ARCH_ARM64
#LOCAL_CFLAGS += -mfloat-abi=softfp
#LOCAL_CFLAGS += -mfpu=neon

LOCAL_SRC_FILES += sinc_resampler_neon.cc



LOCAL_ARM_NEON  := true


endif #TARGET_ARCH_ABI == armeabi-v7a

include $(BUILD_STATIC_LIBRARY)