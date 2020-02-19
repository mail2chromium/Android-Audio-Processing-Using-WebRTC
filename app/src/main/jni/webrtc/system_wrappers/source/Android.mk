LOCAL_PATH:= $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := libwebrtc_system_wrapper

LOCAL_CPPFLAGS += -std=c++11
LOCAL_CFLAGS += -DWEBRTC_POSIX
LOCAL_CFLAGS += -DWEBRTC_NS_FIXED
LOCAL_CFLAGS += -DWEBRTC_ANDROID

LOCAL_C_INCLUDES := $(JNI_PATH)

#$(warning $(LOCAL_C_INCLUDES))


LOCAL_SRC_FILES := aligned_malloc.cc \
                   file_impl.cc \
                   logging.cc \
                   metrics_default.cc \
                   rw_lock.cc \
                   rw_lock_posix.cc \
                   trace_impl.cc \
                   trace_posix.cc


include $(BUILD_STATIC_LIBRARY)