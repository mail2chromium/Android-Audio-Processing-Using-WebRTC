LOCAL_PATH:= $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := libwebrtc_common_audio

LOCAL_CFLAGS += -DWEBRTC_ANDROID

LOCAL_CPPFLAGS += -std=c++11
LOCAL_CFLAGS += -DWEBRTC_POSIX

LOCAL_C_INCLUDES := $(JNI_PATH)

#$(warning $(LOCAL_C_INCLUDES))


LOCAL_SRC_FILES := audio_converter.cc \
                   audio_ring_buffer.cc \
                   audio_util.cc \
                   blocker.cc \
                   channel_buffer.cc \
                   fft4g.c \
                   lapped_transform.cc \
                   real_fourier.cc \
                   real_fourier_ooura.cc \
                   ring_buffer.c \
                   sparse_fir_filter.cc \
                   window_generator.cc \
                   fir_filter.cc

ifeq ($(TARGET_ARCH_ABI),armeabi)

#LOCAL_SRC_FILES += fir_filter.cc


else ifeq ($(TARGET_ARCH_ABI),armeabi-v7a)

LOCAL_CFLAGS += -DWEBRTC_HAS_NEON
#LOCAL_CFLAGS += -mfloat-abi=softfp
#LOCAL_CFLAGS += -mfpu=neon

LOCAL_SRC_FILES += fir_filter_neon.cc



LOCAL_ARM_NEON  := true

else ifeq ($(TARGET_ARCH_ABI),arm64-v8a)

LOCAL_CFLAGS += -DWEBRTC_HAS_NEON
LOCAL_CFLAGS += -DWEBRTC_ARCH_ARM64
#LOCAL_CFLAGS += -mfloat-abi=softfp
#LOCAL_CFLAGS += -mfpu=neon

LOCAL_SRC_FILES += fir_filter_neon.cc



LOCAL_ARM_NEON  := true

endif #TARGET_ARCH_ABI == armeabi-v7a






include $(BUILD_STATIC_LIBRARY)