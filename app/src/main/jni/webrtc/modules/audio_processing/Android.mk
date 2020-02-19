LOCAL_PATH:= $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := libwebrtc_apm

LOCAL_CFLAGS += -DWEBRTC_ANDROID

LOCAL_CPPFLAGS += -std=c++11
LOCAL_CFLAGS += -DWEBRTC_POSIX
LOCAL_CFLAGS += -DWEBRTC_NS_FIXED

LOCAL_C_INCLUDES := $(JNI_PATH)

#$(warning $(LOCAL_C_INCLUDES))


LOCAL_SRC_FILES := audio_buffer.cc \
                   audio_processing_impl.cc \
                   echo_cancellation_impl.cc \
                   echo_control_mobile_impl.cc \
                   gain_control_for_experimental_agc.cc \
                   gain_control_impl.cc \
                   high_pass_filter_impl.cc \
                   level_estimator_impl.cc \
                   noise_suppression_impl.cc \
                   processing_component.cc \
                   rms_level.cc \
                   splitting_filter.cc \
                   three_band_filter_bank.cc \
                   typing_detection.cc \
                   voice_detection_impl.cc

include $(BUILD_STATIC_LIBRARY)