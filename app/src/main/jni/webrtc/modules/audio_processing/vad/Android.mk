LOCAL_PATH:= $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := libwebrtc_vad

LOCAL_CFLAGS += -DWEBRTC_ANDROID

LOCAL_CPPFLAGS += -std=c++11
LOCAL_CFLAGS += -DWEBRTC_POSIX

LOCAL_C_INCLUDES := $(JNI_PATH)

#$(warning $(LOCAL_C_INCLUDES))


LOCAL_SRC_FILES := gmm.cc \
                   pitch_based_vad.cc \
                   pitch_internal.cc \
                   pole_zero_filter.cc \
                   standalone_vad.cc \
                   vad_audio_proc.cc \
                   vad_circular_buffer.cc \
                   voice_activity_detector.cc


include $(BUILD_STATIC_LIBRARY)