LOCAL_PATH:= $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := libwebrtc_spl

LOCAL_CFLAGS += -DWEBRTC_ANDROID

LOCAL_CFLAGS += -DWEBRTC_POSIX

LOCAL_C_INCLUDES := $(JNI_PATH)

#$(warning $(LOCAL_C_INCLUDES))


LOCAL_SRC_FILES := auto_corr_to_refl_coef.c \
                   auto_correlation.c \
                   complex_fft.c \
                   copy_set_operations.c \
                   division_operations.c \
                   dot_product_with_scale.c \
                   energy.c \
                   filter_ar.c \
                   filter_ma_fast_q12.c \
                   get_hanning_window.c \
                   get_scaling_square.c \
                   ilbc_specific_functions.c \
                   levinson_durbin.c \
                   randomization_functions.c \
                   real_fft.c \
                   refl_coef_to_lpc.c \
                   resample.c \
                   resample_48khz.c \
                   resample_by_2.c \
                   resample_by_2_internal.c \
                   resample_fractional.c \
                   spl_init.c \
                   spl_sqrt.c \
                   splitting_filter.c \
                   sqrt_of_one_minus_x_squared.c \
                   vector_scaling_operations.c


ifeq ($(TARGET_ARCH_ABI),armeabi)

LOCAL_SRC_FILES += cross_correlation.c \
                   downsample_fast.c \
                   min_max_operations.c \
                   filter_ar_fast_q12.c \
                   spl_sqrt_floor_arm.S \
                   complex_bit_reverse_arm.S

else ifeq ($(TARGET_ARCH_ABI),armeabi-v7a)

LOCAL_CFLAGS += -DWEBRTC_HAS_NEON
#LOCAL_CFLAGS += -mfloat-abi=softfp
#LOCAL_CFLAGS += -mfpu=neon

LOCAL_SRC_FILES += cross_correlation_neon.c \
                   downsample_fast_neon.c \
                   min_max_operations_neon.c \
                   filter_ar_fast_q12_armv7.S \
                   spl_sqrt_floor_arm.S \
                   complex_bit_reverse_arm.S



LOCAL_ARM_NEON  := true

else ifeq ($(TARGET_ARCH_ABI),arm64-v8a)


LOCAL_CFLAGS += -DWEBRTC_HAS_NEON
LOCAL_CFLAGS += -DWEBRTC_ARCH_ARM64
#LOCAL_CFLAGS += -mfloat-abi=softfp
#LOCAL_CFLAGS += -mfpu=neon

LOCAL_SRC_FILES += cross_correlation_neon.c \
                   downsample_fast_neon.c \
                   min_max_operations_neon.c \
                   filter_ar_fast_q12.c \
                   spl_sqrt_floor.c \
                   complex_bit_reverse.c



LOCAL_ARM_NEON  := true


endif #TARGET_ARCH_ABI == armeabi-v8a






include $(BUILD_STATIC_LIBRARY)