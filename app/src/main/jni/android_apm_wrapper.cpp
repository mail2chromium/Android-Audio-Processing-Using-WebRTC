/**
 * These are wrappers of native webrtc echo cancellation mobile edition functions.
 */
#include <jni.h>
#include <stdlib.h> // for NULL
#include <assert.h>
#include <stddef.h>
#include <unistd.h>
#include <memory>

#include <android/log.h>
#define TAG "APM"
#define LOGV(...) __android_log_print(ANDROID_LOG_VERBOSE, TAG, __VA_ARGS__)

#include "webrtc/modules/audio_processing/include/audio_processing.h"
#include "webrtc/modules/include/module_common_types.h"
#include "webrtc/common_audio/channel_buffer.h"
#include "webrtc/common_audio/include/audio_util.h"
#include "webrtc/common.h"
#include "webrtc/common_audio/resampler/include/resampler.h"
//#include "webrtc/modules/audio_processing/beamformer/mock_nonlinear_beamformer.h"

#include "com_webrtc_audioprocessing_Apm.h"

using namespace std;
using namespace webrtc;

static void set_ctx(JNIEnv *env, jobject thiz, void *ctx) {
    jclass cls = env->GetObjectClass(thiz);
    jfieldID fid = env->GetFieldID(cls, "objData", "J");
    env->SetLongField(thiz, fid, (jlong)ctx);
}

static void *get_ctx(JNIEnv *env, jobject thiz) {
    jclass cls = env->GetObjectClass(thiz);
    jfieldID fid = env->GetFieldID(cls, "objData", "J");
    return (void*)env->GetLongField(thiz, fid);
}

webrtc::Resampler* resample;

class ApmWrapper{
    const int sample_rate_hz = AudioProcessing::kSampleRate16kHz;
    const int num_input_channels = 1;

    const int reverse_sample_rate_hz = AudioProcessing::kSampleRate16kHz;
    const int num_reverse_channels = 1;

public:
    ApmWrapper(bool aecExtendFilter, bool speechIntelligibilityEnhance, bool delayAgnostic, bool beamforming, bool nextGenerationAec, bool experimentalNs, bool experimentalAgc){

        _beamForming = beamforming;

        Config config;
        config.Set<ExtendedFilter>(new ExtendedFilter(aecExtendFilter));
        config.Set<Intelligibility>(new Intelligibility(speechIntelligibilityEnhance));
        config.Set<DelayAgnostic>(new DelayAgnostic(delayAgnostic));
/*
        MockNonlinearBeamformer* beamFormer = nullptr;
        if(beamforming) {
            std::vector<webrtc::Point> geometry;
            geometry.push_back(webrtc::Point(0.f, 0.f, 0.f));
            geometry.push_back(webrtc::Point(0.05f, 0.f, 0.f));
            config.Set<Beamforming>(new Beamforming(beamforming, geometry));
            beamFormer = new MockNonlinearBeamformer(geometry);
        }*/

        config.Set<NextGenerationAec>(new NextGenerationAec(nextGenerationAec));
        config.Set<ExperimentalNs>(new ExperimentalNs(experimentalNs));
        config.Set<ExperimentalAgc>(new ExperimentalAgc(experimentalAgc));

        _apm.reset(AudioProcessing::Create(config));

        /*
        if(beamforming) {
            _apm.reset(AudioProcessing::Create(config, beamFormer));
        }else {
            _apm.reset(AudioProcessing::Create(config));
        }*/

        _frame = new AudioFrame();
        _reverseFrame = new AudioFrame();

        SetContainerFormat(sample_rate_hz, num_input_channels, _frame, &_float_cb);

        SetContainerFormat(reverse_sample_rate_hz, num_reverse_channels, _reverseFrame,
                           &_revfloat_cb);

        _apm->Initialize({{{_frame->sample_rate_hz_, _frame->num_channels_},
                         {_frame->sample_rate_hz_, _frame->num_channels_},
                         {_reverseFrame->sample_rate_hz_, _reverseFrame->num_channels_},
                         {_reverseFrame->sample_rate_hz_, _reverseFrame->num_channels_}}});
    }

    ~ApmWrapper(){
        delete _frame;
        delete _reverseFrame;
    }

    int ProcessStream(int16_t* data){
        std::copy(data, data + _frame->samples_per_channel_, _frame->data_);
//        ConvertToFloat(*_frame, _float_cb.get());
        int ret = _apm->ProcessStream(_frame);
        std::copy(_frame->data_, _frame->data_ + _frame->samples_per_channel_, data);
        return ret;
    }

    int ProcessReverseStream(int16_t* data){
        std::copy(data, data + _reverseFrame->samples_per_channel_, _reverseFrame->data_);
//        ConvertToFloat(*_reverseFrame, _revfloat_cb.get());
        int ret = _apm->ProcessReverseStream(_reverseFrame);
        if(_beamForming){
            std::copy(_reverseFrame->data_, _reverseFrame->data_ + _reverseFrame->samples_per_channel_, data);
        }
        return ret;
//        return _apm->AnalyzeReverseStream(_reverseFrame);
    }

public:
    unique_ptr<AudioProcessing> _apm;

private:
    template <typename T>
    void SetContainerFormat(int sample_rate_hz,
                            size_t num_channels,
                            AudioFrame* frame,
                            unique_ptr<ChannelBuffer<T> >* cb) {
        SetFrameSampleRate(frame, sample_rate_hz);
        frame->num_channels_ = num_channels;
        cb->reset(new ChannelBuffer<T>(frame->samples_per_channel_, num_channels));
    }

    void SetFrameSampleRate(AudioFrame* frame,
                            int sample_rate_hz) {
        frame->sample_rate_hz_ = sample_rate_hz;
        frame->samples_per_channel_ = AudioProcessing::kChunkSizeMs *
                                      sample_rate_hz / 1000;
    }

    void ConvertToFloat(const int16_t* int_data, ChannelBuffer<float>* cb) {
        ChannelBuffer<int16_t> cb_int(cb->num_frames(),
                                      cb->num_channels());
        Deinterleave(int_data,
                     cb->num_frames(),
                     cb->num_channels(),
                     cb_int.channels());
        for (size_t i = 0; i < cb->num_channels(); ++i) {
            S16ToFloat(cb_int.channels()[i],
                       cb->num_frames(),
                       cb->channels()[i]);
        }
    }

    void ConvertToFloat(const AudioFrame& frame, ChannelBuffer<float>* cb) {
        ConvertToFloat(frame.data_, cb);
    }

private:
    AudioFrame *_frame;
    AudioFrame *_reverseFrame;

    unique_ptr<ChannelBuffer<float>> _float_cb;
    unique_ptr<ChannelBuffer<float>> _revfloat_cb;

    bool _beamForming = false;
};


#ifdef __cplusplus
extern "C" {
#endif


    JNIEXPORT jint JNI_OnLoad(JavaVM *vm , void *reserved ) {
        return JNI_VERSION_1_6;
    }

JNIEXPORT jboolean JNICALL Java_com_webrtc_audioprocessing_Apm_nativeCreateApmInstance
        (JNIEnv *env, jobject thiz, jboolean aecExtendFilter, jboolean speechIntelligibilityEnhance, jboolean delayAgnostic, jboolean beamforming, jboolean nextGenerationAec, jboolean experimentalNs, jboolean experimentalAgc) {
        ApmWrapper* apm = new ApmWrapper(aecExtendFilter, speechIntelligibilityEnhance, delayAgnostic, beamforming, nextGenerationAec, experimentalNs, experimentalAgc);

        if (apm == nullptr)
            return JNI_FALSE;
        else {
            set_ctx(env, thiz, apm);
            LOGV("created");
            return JNI_TRUE;
        }
    }


JNIEXPORT void JNICALL Java_com_webrtc_audioprocessing_Apm_nativeFreeApmInstance
(JNIEnv *env, jobject thiz) {
        ApmWrapper *apm = (ApmWrapper*) get_ctx(env, thiz);
        delete apm;
        LOGV("destroyed");
    }


JNIEXPORT jint JNICALL Java_com_webrtc_audioprocessing_Apm_high_1pass_1filter_1enable
        (JNIEnv *env, jobject thiz, jboolean enable){

    ApmWrapper *apm = (ApmWrapper*) get_ctx(env, thiz);
    return apm->_apm->high_pass_filter()->Enable(enable);
}


JNIEXPORT jint JNICALL Java_com_webrtc_audioprocessing_Apm_aec_1enable
        (JNIEnv *env, jobject thiz, jboolean enable){

    ApmWrapper *apm = (ApmWrapper*) get_ctx(env, thiz);
    return apm->_apm->echo_cancellation()->Enable(enable);
}


JNIEXPORT jint JNICALL Java_com_webrtc_audioprocessing_Apm_aec_1clock_1drift_1compensation_1enable
(JNIEnv *env, jobject thiz, jboolean enable){

    ApmWrapper *apm = (ApmWrapper*) get_ctx(env, thiz);
    return apm->_apm->echo_cancellation()->enable_drift_compensation(enable);

}

JNIEXPORT jint JNICALL Java_com_webrtc_audioprocessing_Apm_aec_1set_1suppression_1level
        (JNIEnv *env, jobject thiz, jint level){

    ApmWrapper *apm = (ApmWrapper*) get_ctx(env, thiz);
    if ( level < EchoCancellation::kLowSuppression ){
        level = EchoCancellation::kLowSuppression;
    }else if(level > EchoCancellation::kHighSuppression){
        level = EchoCancellation::kHighSuppression;
    }
    return apm->_apm->echo_cancellation()->set_suppression_level((EchoCancellation::SuppressionLevel)level);
}



JNIEXPORT jint JNICALL Java_com_webrtc_audioprocessing_Apm_aecm_1enable
        (JNIEnv *env, jobject thiz, jboolean enable){

    ApmWrapper *apm = (ApmWrapper*) get_ctx(env, thiz);
    return apm->_apm->echo_control_mobile()->Enable(enable);
}

JNIEXPORT jint JNICALL Java_com_webrtc_audioprocessing_Apm_aecm_1set_1suppression_1level
        (JNIEnv *env, jobject thiz, jint level){

    ApmWrapper *apm = (ApmWrapper*) get_ctx(env, thiz);
    if(level < EchoControlMobile::kQuietEarpieceOrHeadset){
        level = EchoControlMobile::kQuietEarpieceOrHeadset;
    }else if(level > EchoControlMobile::kLoudSpeakerphone){
        level = EchoControlMobile::kLoudSpeakerphone;
    }
    return apm->_apm->echo_control_mobile()->set_routing_mode((EchoControlMobile::RoutingMode)level);

}


JNIEXPORT jint JNICALL Java_com_webrtc_audioprocessing_Apm_ns_1set_1level
(JNIEnv *env, jobject thiz, jint level){

    if(level < NoiseSuppression::kLow){
        level = NoiseSuppression::kLow;
    }else if(level > NoiseSuppression::kVeryHigh){
        level = NoiseSuppression::kVeryHigh;
    }


    ApmWrapper *apm = (ApmWrapper*) get_ctx(env, thiz);
    return apm->_apm->noise_suppression()->set_level((NoiseSuppression::Level)level);
}


JNIEXPORT jint JNICALL Java_com_webrtc_audioprocessing_Apm_ns_1enable
(JNIEnv *env, jobject thiz, jboolean enable){

    ApmWrapper *apm = (ApmWrapper*) get_ctx(env, thiz);
    return apm->_apm->noise_suppression()->Enable(enable);
}


JNIEXPORT jint JNICALL Java_com_webrtc_audioprocessing_Apm_agc_1set_1analog_1level_1limits
(JNIEnv *env, jobject thiz, jint minimum, jint maximum){

    if(minimum < 0){
        minimum = 0;
    }else if(minimum > 65535){
        minimum = 65535;
    }

    if(maximum < 0){
        maximum = 0;
    }else if(maximum > 65535){
        maximum = 65535;
    }

    if(minimum > maximum){
        int temp = minimum;
        minimum = maximum;
        maximum = temp;
    }

    ApmWrapper *apm = (ApmWrapper*) get_ctx(env, thiz);
    return apm->_apm->gain_control()->set_analog_level_limits(minimum, maximum);
}


JNIEXPORT jint JNICALL Java_com_webrtc_audioprocessing_Apm_agc_1set_1mode
(JNIEnv *env, jobject thiz, jint mode){

    if(mode < GainControl::Mode::kAdaptiveAnalog){
        mode = GainControl::Mode::kAdaptiveAnalog;
    }else if(mode > GainControl::Mode::kFixedDigital){
        mode = GainControl::Mode::kFixedDigital;
    }

    ApmWrapper *apm = (ApmWrapper*) get_ctx(env, thiz);
    return apm->_apm->gain_control()->set_mode((GainControl::Mode)mode);
}

JNIEXPORT jint JNICALL Java_com_webrtc_audioprocessing_Apm_agc_1set_1target_1level_1dbfs
  (JNIEnv *env, jobject thiz, jint level){

    ApmWrapper *apm = (ApmWrapper*) get_ctx(env, thiz);
    return apm->_apm->gain_control()->set_target_level_dbfs(level);
}


JNIEXPORT jint JNICALL Java_com_webrtc_audioprocessing_Apm_agc_1set_1compression_1gain_1db
        (JNIEnv *env, jobject thiz, jint gain){
    ApmWrapper *apm = (ApmWrapper*) get_ctx(env, thiz);
    return apm->_apm->gain_control()->set_compression_gain_db(gain);
}


JNIEXPORT jint JNICALL Java_com_webrtc_audioprocessing_Apm_agc_1enable_1limiter
        (JNIEnv *env, jobject thiz, jboolean enable){
    ApmWrapper *apm = (ApmWrapper*) get_ctx(env, thiz);
    return apm->_apm->gain_control()->enable_limiter(enable);
}


JNIEXPORT int JNICALL Java_com_webrtc_audioprocessing_Apm_agc_1enable
(JNIEnv *env, jobject thiz, jboolean enable){

    ApmWrapper *apm = (ApmWrapper*) get_ctx(env, thiz);
    return apm->_apm->gain_control()->Enable(enable);

}

JNIEXPORT jint JNICALL Java_com_webrtc_audioprocessing_Apm_agc_1set_1stream_1analog_1level
(JNIEnv *env, jobject thiz, jint level){

    ApmWrapper *apm = (ApmWrapper*) get_ctx(env, thiz);
    return apm->_apm->gain_control()->set_stream_analog_level(level);
}


JNIEXPORT jint JNICALL Java_com_webrtc_audioprocessing_Apm_agc_1stream_1analog_1level
        (JNIEnv *env, jobject thiz){

    ApmWrapper *apm = (ApmWrapper*) get_ctx(env, thiz);
    return apm->_apm->gain_control()->stream_analog_level();
}


JNIEXPORT jint JNICALL Java_com_webrtc_audioprocessing_Apm_vad_1enable
(JNIEnv *env, jobject thiz, jboolean enable){

    ApmWrapper *apm = (ApmWrapper*) get_ctx(env, thiz);
    return apm->_apm->voice_detection()->Enable(enable);

}


JNIEXPORT jint JNICALL Java_com_webrtc_audioprocessing_Apm_vad_1set_1likelihood
        (JNIEnv *env, jobject thiz, jint likelihood){

    ApmWrapper *apm = (ApmWrapper*) get_ctx(env, thiz);
    if(likelihood < VoiceDetection::kVeryLowLikelihood){
        likelihood = VoiceDetection::kVeryLowLikelihood;
    }else if(likelihood > VoiceDetection::kHighLikelihood){
        likelihood = VoiceDetection::kHighLikelihood;
    }


    return apm->_apm->voice_detection()->set_likelihood((VoiceDetection::Likelihood)likelihood);
}


JNIEXPORT jboolean JNICALL Java_com_webrtc_audioprocessing_Apm_vad_1stream_1has_1voice
        (JNIEnv *env, jobject thiz){

    ApmWrapper *apm = (ApmWrapper*) get_ctx(env, thiz);
    return apm->_apm->voice_detection()->stream_has_voice();
}


JNIEXPORT jint JNICALL Java_com_webrtc_audioprocessing_Apm_ProcessStream
        (JNIEnv *env, jobject thiz, jshortArray nearEnd, jint offset){

    ApmWrapper *apm = (ApmWrapper*) get_ctx(env, thiz);
    short *buffer = (short*)env->GetShortArrayElements(nearEnd, nullptr);
    int ret = apm->ProcessStream(buffer + offset);
    env->ReleaseShortArrayElements(nearEnd, buffer, 0);
    return ret;
}


JNIEXPORT jint JNICALL Java_com_webrtc_audioprocessing_Apm_ProcessReverseStream
        (JNIEnv *env, jobject thiz, jshortArray farEnd, jint offset){

    ApmWrapper *apm = (ApmWrapper*) get_ctx(env, thiz);
    short *buffer = (short*)env->GetShortArrayElements(farEnd, nullptr);
    int ret = apm->ProcessReverseStream(buffer + offset);

    env->ReleaseShortArrayElements(farEnd, buffer, 0);
    return ret;
}


JNIEXPORT jint JNICALL Java_com_webrtc_audioprocessing_Apm_set_1stream_1delay_1ms
        (JNIEnv *env, jobject thiz, jint delay){

    ApmWrapper *apm = (ApmWrapper*) get_ctx(env, thiz);
    return apm->_apm->set_stream_delay_ms(delay);
}



JNIEXPORT jboolean JNICALL Java_com_webrtc_audioprocessing_Apm_SamplingInit(JNIEnv *env, jobject thiz, jint inFreq, jint outFreq, jlong num_channels)
{
    resample = new webrtc::Resampler(inFreq,outFreq,num_channels);

    if (resample == nullptr)
    {
       return false;
    }

    return true;

}


JNIEXPORT jint JNICALL Java_com_webrtc_audioprocessing_Apm_SamplingReset(JNIEnv *env, jobject thiz, jint inFreq, jint outFreq, jlong num_channels)
{

    int val = resample->Reset(inFreq,outFreq,num_channels);

    return val;

}

JNIEXPORT jint JNICALL Java_com_webrtc_audioprocessing_Apm_SamplingResetIfNeeded(JNIEnv* env, jobject thiz, jint inFreq, jint outFreq, jlong num_channels)
{
	int val = resample->ResetIfNeeded(inFreq,outFreq,num_channels);

    return val;
}

JNIEXPORT jint JNICALL Java_com_webrtc_audioprocessing_Apm_SamplingPush(JNIEnv* env, jobject thiz, jshortArray samplesIn, jlong lengthIn,
jshortArray samplesOut, jlong maxLen, jlong outLen)
{
    size_t len = 0;
    len = outLen;
    short *input = (short*)env->GetShortArrayElements(samplesIn, nullptr);

    short *output = (short*)env->GetShortArrayElements(samplesOut, nullptr);

	int val = resample->Push(input,lengthIn,output,maxLen,len);

	env->ReleaseShortArrayElements(samplesIn, input, 0);

	env->ReleaseShortArrayElements(samplesOut, output, 0);

	return val;
}


JNIEXPORT jboolean JNICALL Java_com_webrtc_audioprocessing_Apm_SamplingDestroy(JNIEnv* env, jobject thiz)
{
	  resample = nullptr;
      delete resample;

      return true;
}

#ifdef __cplusplus
}
#endif
