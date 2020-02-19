LOCAL_PATH:= $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := libwebrtc_beamformer

LOCAL_CFLAGS += -DWEBRTC_ANDROID

LOCAL_CPPFLAGS += -std=c++11
LOCAL_CFLAGS += -DWEBRTC_POSIX
LOCAL_CFLAGS += -DWEBRTC_NS_FIXED

LOCAL_C_INCLUDES := $(JNI_PATH)

#$(warning $(LOCAL_C_INCLUDES))


LOCAL_SRC_FILES := array_util.cc \
                   covariance_matrix_generator.cc \
                   nonlinear_beamformer.cc


include $(BUILD_STATIC_LIBRARY)