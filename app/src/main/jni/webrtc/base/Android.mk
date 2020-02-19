LOCAL_PATH:= $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := libwebrtc_base

LOCAL_CPPFLAGS += -std=c++11
LOCAL_CFLAGS += -DWEBRTC_POSIX
LOCAL_CFLAGS += -latomic
LOCAL_CFLAGS += -DWEBRTC_ANDROID
LOCAL_CFLAGS += -D__STDC_CONSTANT_MACROS
LOCAL_CFLAGS += -D__STDC_FORMAT_MACROS


LOCAL_CFLAGS += -D__UCLIBC__   # used for no such file execinfo.h

LOCAL_C_INCLUDES := $(JNI_PATH)

#$(warning $(LOCAL_C_INCLUDES))


LOCAL_SRC_FILES := checks.cc \
                   criticalsection.cc \
                   event.cc \
                   event_tracer.cc \
                   platform_file.cc \
                   platform_thread.cc \
                   timeutils.cc \
                   logging.cc \
                   stringencode.cc






include $(BUILD_STATIC_LIBRARY)