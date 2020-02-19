MY_WEBRTC_ROOT_PATH := $(call my-dir)

#LOCAL_CPPFLAGS += -std=c++11
LOCAL_CFLAGS += -DWEBRTC_POSIX

include $(MY_WEBRTC_ROOT_PATH)/modules/audio_processing/aec/Android.mk
include $(MY_WEBRTC_ROOT_PATH)/modules/audio_processing/aecm/Android.mk
include $(MY_WEBRTC_ROOT_PATH)/modules/audio_processing/agc/Android.mk
include $(MY_WEBRTC_ROOT_PATH)/modules/audio_processing/ns/Android.mk
include $(MY_WEBRTC_ROOT_PATH)/modules/audio_processing/utility/Android.mk
include $(MY_WEBRTC_ROOT_PATH)/modules/audio_processing/vad/Android.mk
include $(MY_WEBRTC_ROOT_PATH)/modules/audio_processing/Android.mk
include $(MY_WEBRTC_ROOT_PATH)/modules/audio_processing/intelligibility/Android.mk
include $(MY_WEBRTC_ROOT_PATH)/modules/audio_processing/transient/Android.mk
include $(MY_WEBRTC_ROOT_PATH)/modules/audio_processing/beamformer/Android.mk
include $(MY_WEBRTC_ROOT_PATH)/common_audio/vad/Android.mk
include $(MY_WEBRTC_ROOT_PATH)/common_audio/signal_processing/Android.mk
include $(MY_WEBRTC_ROOT_PATH)/common_audio/Android.mk
include $(MY_WEBRTC_ROOT_PATH)/common_audio/resampler/Android.mk
include $(MY_WEBRTC_ROOT_PATH)/modules/audio_coding/codecs/isac/main/source/Android.mk
include $(MY_WEBRTC_ROOT_PATH)/modules/audio_coding/codecs/Android.mk
include $(MY_WEBRTC_ROOT_PATH)/system_wrappers/source/Android.mk
include $(MY_WEBRTC_ROOT_PATH)/base/Android.mk



LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE := libwebrtc_apms

LOCAL_MODULE_TAGS := optional

LOCAL_LDLIBS += -llog
LOCAL_LDLIBS += -landroid
LOCAL_CFLAGS += -O0


LOCAL_CPPFLAGS += -std=c++11
LOCAL_CFLAGS += -DWEBRTC_ANDROID
LOCAL_CFLAGS += -DWEBRTC_POSIX

LOCAL_C_INCLUDES := $(JNI_PATH)

LOCAL_SRC_FILES := $(JNI_PATH)/android_apm_wrapper.cpp


LOCAL_WHOLE_STATIC_LIBRARIES := libwebrtc_aec \
                                libwebrtc_aecm \
                                libwebrtc_agc \
                                libwebrtc_ns \
                                libwebrtc_utility \
                                libwebrtc_vad \
                                libwebrtc_apm \
                                libwebrtc_common_audio_vad \
                                libwebrtc_spl \
                                libwebrtc_codecs_isac \
                                libwebrtc_common_audio \
                                libwebrtc_common_audio_resampler \
                                libwebrtc_system_wrapper \
                                libwebrtc_base \
                                libwebrtc_intelligibility \
                                libwebrtc_transient \
                                libwebrtc_beamformer \
                                libwebrtc_codecs




LOCAL_PRELINK_MODULE := false

include $(BUILD_SHARED_LIBRARY)