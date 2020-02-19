LOCAL_PATH:= $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := libwebrtc_transient

LOCAL_CFLAGS += -DWEBRTC_ANDROID

LOCAL_CPPFLAGS += -std=c++11
LOCAL_CFLAGS += -DWEBRTC_POSIX
LOCAL_CFLAGS += -DWEBRTC_NS_FIXED

LOCAL_C_INCLUDES := $(JNI_PATH)

#$(warning $(LOCAL_C_INCLUDES))


LOCAL_SRC_FILES := click_annotate.cc \
                   file_utils.cc \
                   moving_moments.cc \
                   transient_detector.cc \
                   transient_suppressor.cc \
                   wpd_node.cc \
                   wpd_tree.cc

include $(BUILD_STATIC_LIBRARY)