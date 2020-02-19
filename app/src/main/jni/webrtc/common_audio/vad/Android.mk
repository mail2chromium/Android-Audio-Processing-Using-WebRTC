LOCAL_PATH:= $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := libwebrtc_common_audio_vad

LOCAL_CFLAGS += -DWEBRTC_ANDROID

LOCAL_CPPFLAGS += -std=c++11
LOCAL_CFLAGS += -DWEBRTC_POSIX

LOCAL_C_INCLUDES := $(JNI_PATH)

#$(warning $(LOCAL_C_INCLUDES))


LOCAL_SRC_FILES := vad.cc \
                   vad_core.c \
                   vad_filterbank.c \
                   vad_gmm.c \
                   vad_sp.c \
                   webrtc_vad.c



include $(BUILD_STATIC_LIBRARY)