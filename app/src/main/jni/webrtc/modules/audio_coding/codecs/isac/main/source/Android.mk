LOCAL_PATH:= $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := libwebrtc_codecs_isac

LOCAL_CFLAGS += -DWEBRTC_ANDROID

LOCAL_CPPFLAGS += -std=c++11
LOCAL_CFLAGS += -DWEBRTC_POSIX

LOCAL_C_INCLUDES := $(JNI_PATH) \
                    $(LOCAL_PATH) \
                    $(JNI_PATH)/webrtc/modules/audio_coding/codecs/isac/main/include \
                    $(JNI_PATH)/webrtc/common_audio/signal_processing/include

#$(warning $(LOCAL_C_INCLUDES))


LOCAL_SRC_FILES := arith_routines.c \
                   arith_routines_hist.c \
                   arith_routines_logist.c \
                   audio_decoder_isac.cc \
                   audio_encoder_isac.cc \
                   bandwidth_estimator.c \
                   crc.c \
                   decode.c \
                   decode_bwe.c \
                   encode.c \
                   encode_lpc_swb.c \
                   entropy_coding.c \
                   fft.c \
                   filter_functions.c \
                   filterbank_tables.c \
                   filterbanks.c \
                   intialize.c \
                   isac.c \
                   lattice.c \
                   lpc_analysis.c \
                   lpc_gain_swb_tables.c \
                   lpc_shape_swb12_tables.c \
                   lpc_shape_swb16_tables.c \
                   lpc_tables.c \
                   pitch_estimator.c \
                   pitch_filter.c \
                   pitch_gain_tables.c \
                   pitch_lag_tables.c \
                   spectrum_ar_model_tables.c \
                   transform.c




include $(BUILD_STATIC_LIBRARY)