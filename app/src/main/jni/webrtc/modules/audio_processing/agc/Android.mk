LOCAL_PATH:= $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := libwebrtc_agc

LOCAL_CFLAGS += -DWEBRTC_ANDROID

LOCAL_CPPFLAGS += -std=c++11
LOCAL_CFLAGS += -DWEBRTC_POSIX

LOCAL_C_INCLUDES := $(JNI_PATH)

#$(warning $(LOCAL_C_INCLUDES))


LOCAL_SRC_FILES := agc.cc \
                   agc_manager_direct.cc \
                   histogram.cc \
                   utility.cc \
                   legacy/analog_agc.c \
                   legacy/digital_agc.c

include $(BUILD_STATIC_LIBRARY)