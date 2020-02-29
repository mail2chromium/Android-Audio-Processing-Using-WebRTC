# "APM (Audio Processing Module) should be placed in the signal chain as close to the audio hardware abstraction layer (HAL) as possible."

-----

**Getting Started**
------
High performance audio apps typically require more functionality than the simple ability to play or record sound. They demand responsive realtime system behavior. Webrtc provides browsers and *mobile applications* with Real-Time Communications (RTC) capabilities via simple APIs. 

WebRTC supports  (Audio Microphone Collection), (Encoding), and (RTP packet transmission). Features of *Audio Data* that Webrtc-APM accepts only include;

- **16-bit** linear PCM Audio Data.
- 160-frames of **10 ms**. (Optimal Sample Rate)
- One or Multiple channels should be interleaved.


-----
**Unreliable Audio Issues**
-----

Most of the time, voice communication is just a barrier in *VoIP Industry* due to **muffled and stuttering voice**. Which includes `distortion`, `echo`, `noise` and very `unstisfactory` output results. The most common and even more worse **causes** that make bad real-time voice communications are given as:

- Packet loss concealment
- Echo cancellation
- Bandwidth adaptivity
- Dynamic jitter buffering
- Automatic gain control problems
- Noise reduction and suppression



**Choice of Audio Processing Technique**:
-----

There are two general approaches to audio programming in Android, either to use built-in (Android-SDK) or use (Android-NDK) approach. If you want to stay in the SDK in java, then you should simply try [AudioRecord](https://developer.android.com/reference/android/media/AudioRecord) & [AudioTrack](https://developer.android.com/reference/android/media/AudioTrack).

1. The downside of first approach is that your *audio processing* also remains in java code, which could potentially be slower than compiled C-Code. The audio latency, without processing, is practically the same though.

2. If you choose the *NDK* approach (C with OpenSSL) over SDK (Java with AudioTrack), the setup will be more complex. So the very good example is to go with well-optimized audio-processing libraries such as;

* [Speex](https://www.speex.org/)
* [TarsosDSP](http://sapandiwakar.in/audio-processing-on-android-using-tarsosdsp/)
* [Webrtc](https://chromium.googlesource.com/chromiumos/third_party/webrtc-apm/+/refs/heads/stabilize-12371.39.B/README.md)

If you stick with Java, you will probably need a *solid FFT library* with support for Java (through a wrapper), so the best choice should be **webrtc**.

------
**Features**
------
WebRTC offers a complete stack for voice communications. It includes not only the necessary codecs, but other components necessary to great user experiences. This `APM` includes series of software-based algorithms such;

- [Acoustic Echo Cancellation](https://github.com/mail2chromium/Android-Acoustic-Echo-Cancelletion-Using-WebRTC) **(AEC)**
- [Automatic Gain Controller](https://github.com/mail2chromium/Android-Automatic-Gain-Control-Using-WebRTC) **(AGC)** 
- [Noise Suppression](https://github.com/mail2chromium/Android-Noise-Suppression-Using-WebRTC) **(NS)**
- [Voice Activity Detection](https://github.com/mail2chromium/Android-Voice-Activity-Detection-Using-WebRTC) **(VAD)** 
- [Webrtc Audio Codecs](https://developer.mozilla.org/en-US/docs/Web/Media/Formats/WebRTC_codecs)  (The currently supported **Voice-Codecs** are `Opus`, `G.711`, `G.722`, `iLBC`, and `iSAC`)

Which also includes hardware access and control across multiple platforms i.e **Mobiles**.
These algorithms are mainly processed **after** collection of audio data from microphone and **before** encoding of audio data.


-----
**Basic workflow:**
-----
AudioProcessing is an event-driven system, which includes follwing events such as;

- APM Initialization Event
- Capturing Audio Event
- Rendering the Audio Event
- Play Audio Event
- Release APM Event

To enable each of the audio processing module and before getting into basic event flow of APM, one should first invoke Audio Processing Initialization & Configuration such as;  

```
AudioProcessing * apm = AudioProcessing :: Create (0);

// Enable retries estimation component
apm-> level_estimator () -> Enable (true);

// Enable echo cancellation module
apm-> echo_cancellation () -> Enable (true); 
apm-> echo_cancellation () -> enable_metrics (true);

// Enable clock compensation module (sound capture device clock frequency clock frequency and playback devices may be different)
apm-> echo_cancellation () -> enable_drift_compensation (true);

// Enable gain control module!
apm-> gain_control () -> Enable (true);

// high-pass filter components, DC offset and low frequency noise filtering, client must be enabled
apm-> high_pass_filter () -> Enable (true); 

// noise suppression components, client must be enabled
apm-> noise_suppression () -> Enable (true); 

// enable voice detection component, to detect whether there voices
apm-> voice_detection () -> Enable (true); 

// Set the voice detection threshold, the threshold bigger voice less likely to be ignored,
// some noise may be treated the same voice.
apm-> voice_detection () -> set_likelihood (VoiceDetection :: kModerateLikelihood); 

// Reserved internal state set by the user in all cases of re-initialization apm, 
// to start processing a new audio stream. After creating the first stream does not necessarily need to call this method.
apm-> Initialize ();
```

After instantiation and configuration of APM module, now we'll look into for various events involved in webrtc APM architecture such as;

-------


**1. APM Initialization Event**

------

In this event, we'll simply set the required parameter for APM such as the (sample rate), (audio capturing & playback device) and (number of channels) of local and remote audio stream such as;

```

// set the sample rate of local and remote audio stream
apm-> set_sample_rate_hz (sample_rate_hz); 

// set the sample rate audio equipment, we assume that the audio capture and playback device 
// using the same sampling rate. (Must be called when the drift component is enabled)

apm-> echo_cancellation () -> set_device_sample_rate_hz (); 
// set the local and remote audio stream of the number of channels
apm-> set_num_channels (num_capture_input_channels, num_capture_output_channels);


```

-------


**2. Capturing Audio Event**

------

To capture (custom audio data) or (audio bytes) from hardware abstraction layer such as microphone, one must know the following steps as given; 

```
apm-> gain_control () -> set_stream_analog_level (capture_level);

// set the delay in milliseconds between local and remote audio streams of. 
// This delay is the time difference and the distal end of the audio stream between the local audio streams

apm-> set_stream_delay_ms (delay_ms + extra_delay_ms); 

// delay between local audio streams is calculated as:

delay = (t_render – t_analyze) + (t_process – t_capture) ;

```

In the above code snippet;

- t_analyze end audio stream is time to `AnalyzeReverseStream ()` method;
- t_render is just similar to the distal end of the `audio frame playback` time;
- t_capture local `audio capture time` frame;
- t_process is the `same time frame` was given to local audio ProcessStream () method.

```
// Set the difference between the audio device to capture and playback sampling rate. 
// (Must be called when the drift component is enabled)

apm-> echo_cancellation () -> set_stream_drift_samples (drift_samples);

// processing audio streams, including all aspects of the deal. (Such as gain adjustment,
// echo cancellation, noise suppression, voice activity detection, high throughput rate 
// without decoding Oh! Do for pcm data processing)
 
int err = apm-> ProcessStream (& near_frame); 

// under emulation mode, you must call this method after ProcessStream, get the recommended analog value of new audio HAL.
capture_level = apm-> gain_control () -> stream_analog_level (); 

// detect whether there is a voice, you must call this method after ProcessStream
stream_has_voice = apm-> voice_detection () -> stream_has_voice ();

// returns the internal voice priority calculated the probability of the current frame.
ns_speech_prob = apm-> noise_suppression () -> speech_probability (); 

```

-------


**3. Rendering the Audio Event**

------

After capturing of Audio Data, incoming stream is just passed to 


```
// Capture frame arrives from the audio HAL
ProcessStream(capture_frame)
```

*ProcessStream()* will process this audio-data from APM.Now to get Ouput audio-data that is further played, actually obtained as given;

```
// Render frame arrives bound for the audio HAL
AnalyzeReverseStream(render_frame);
``` 

By this way, by default, capture event is used to capture Mono-Channel stream while render object is used to fetch stereo-channel stream as given 
```
// Mono capture and stereo render.
apm->set_num_channels(1, 1);
apm->set_num_reverse_channels(2);
```

Repeate render and capture processing for the duration of the call.

-------


**4. - Play Audio Event**

------

To analysis far end `10ms` frame data of the audio stream, these data provide a reference for echo suppression. (Enable echo suppression when calling needs)

```
apm-> AnalyzeReverseStream (& far_frame));
``` 

-------


**5. Release APM Event**

------

At the end, APM Module must be released such as;

```
AudioProcessing release
AudioProcessing :: Destroy (apm);
apm = NULL;
```

If you want to initialize the call again then you can use this simply;

```
// Start a new call...
apm->Initialize();
```


-------

**Webrtc Native Modules Hierarchy** 

-------

WebRTC Native Code package is meant for Android Developers who want to integrate Custom WebRTC into their applications. 
WebRTC native code package can be found at: [Webrtc Native Guide](https://github.com/mail2chromium/Android-Native-Development-For-WebRTC).

While working with webrtc native development in Android, *JNI Folder* should include complete `webrtc ndk stack` for native development. Once you have compiled your shared library using `ndk-build` command. Then you can only use `*.so` files into your lib folder by specifying the path where your `*.so` files are located in `build.gradle` file such as;

```
    sourceSets {
        main {
            jniLibs.srcDirs = ['src/main/libs']
        }
```

 To learn the complete workaround of Webrtc NDK Development, Visit Reference: [Android Webrtc NDK Setup](https://github.com/mail2chromium/Android-Native-Development-For-WebRTC).

Here is simple hierarchy of webrtc native stack inside *JNI-Folder* of android project.

![Native Modules Hierarchy](https://github.com/mail2chromium/Android-Audio-Processing-Using-WebRTC/blob/master/content.png)


-------

**Audio Processing Content**

-------

Here is the content of the `<audioprocessing.h>` file:

```
class AudioFrame;

template<typename T>
class Beamformer;

class StreamConfig;
class ProcessingConfig;

class EchoCancellation;
class EchoControlMobile;
class GainControl;
class HighPassFilter;
class LevelEstimator;
class NoiseSuppression;
class VoiceDetection;

```

* AudioFrame: It mainly records the channel basic information, data, VAD mark time stamp, sampling frequency, channel number, etc.

* EchoCancellation: Echo Cancellation Module (AEC). It should be used when using external speakers. In some cases using headset communication, echo will also exist (because the microphone has a space or weak electrical coupling with the speaker). If it affects the call, it should be turned on .

* EchoControlMobile: Echo Suppression Module (AES). This module is similar to the echo cancellation module in function, but the implementation method is different. This module is implemented using a fixed telephone, and the amount of calculation is much smaller than the echo cancellation module. Ideal for mobile platforms. But the speech damage is great.

* GainControl: Gain Control Module (AGC). This module uses the characteristics of the voice to adjust the system hardware volume and the output signal size. The input volume can be controlled on the hardware. The software can only adjust the amplitude of the original signal. If the original signal has been broken, or the input signal is relatively small, there is nothing to do.

* HighPassFilter: A high-pass filter that suppresses unwanted low-frequency signals. Internally, this is done using a customized IIR (`Infinite Impulse Response (digital filter design and signal processing`)). You can modify the parameters to select the corresponding cut-off frequency. For some equipment with power frequency interference, a high-pass filter is required.

* LevelEstimator: Estimates the energy value of the signal.

* NoiseSuppression: Noise Suppression Module (NS / SE). This module is generally used in the presence of environmental noise, or when the data collected by the microphone has obvious noise.

* VoiceDetection: Voice activated detection module (VAD), which is used to detect the presence of voice. Used for codec and subsequent related processing. During a voice call, if one party is listening but not speaking, it will detect that no data has been collected at the output, and no data will be sent at this time. In this way, the sending status of the data is dynamically adjusted according to whether data is collected, and unnecessary waste of bandwidth is reduced.


Stream Division of APM:
----

Stream division is often known as two times processing of APM. WebRtcAPM is divided into **two streams** such as;

- A Near-End Stream (The near-end stream refers to the data entered from the microphone;)
- A Far-End Stream ( The far-end stream refers to the received data;)

 Now introduce them separately, this part of the code is in `<audio_processing_impl.cc>`, as given;

-------

**To Process far-end stream:** 

-------

In this process, it involves 3-steps as given

1. AEC process, recording farend and related operations in AEC;

2. AES process, recording farend and related operations in AES;

3. AGC process, calculating farend and its related features.


```
int AudioProcessingImpl::ProcessReverseStreamLocked() {
  AudioBuffer* ra = render_.render_audio.get();
  if (rev_analysis_needed()) {
    ra->SplitIntoFrequencyBands();
  }

  if (capture_nonlocked_.intelligibility_enabled) {
    public_submodules_->intelligibility_enhancer->ProcessRenderAudio(
        ra->split_channels_f(kBand0To8kHz), capture_nonlocked_.split_rate,
        ra->num_channels());
  }

  RETURN_ON_ERR(public_submodules_->echo_cancellation->ProcessRenderAudio(ra));
  RETURN_ON_ERR(
      public_submodules_->echo_control_mobile->ProcessRenderAudio(ra));
  if (!constants_.use_experimental_agc) {
    RETURN_ON_ERR(public_submodules_->gain_control->ProcessRenderAudio(ra));
  }

  if (rev_synthesis_needed()) {
    ra->MergeFrequencyBands();
  }

  return kNoError;
}

```

------

**To Process near-end Streams**

------


 Nearend's processing is comprehensive approach than far-end processing. It includes following steps to move accordingly which are given as follows;
 
 1. Frequency Division 
 2. High-Pass Filtering 
 3. AEC
 4. NS || NC
 5. AES 
 6. VAD 
 7. AGC 
 8. Frequency Band Combination 
 9. Volume Adjustment

```

int AudioProcessingImpl::ProcessStreamLocked() {
  // Ensure that not both the AEC and AECM are active at the same time.
  // Simplify once the public API Enable functions for these
  // are moved to APM.
  RTC_DCHECK(!(public_submodules_->echo_cancellation->is_enabled() &&
               public_submodules_->echo_control_mobile->is_enabled()));

#ifdef WEBRTC_AUDIOPROC_DEBUG_DUMP
  if (debug_dump_.debug_file->is_open()) {
    audioproc::Stream* msg = debug_dump_.capture.event_msg->mutable_stream();
    msg->set_delay(capture_nonlocked_.stream_delay_ms);
    msg->set_drift(
        public_submodules_->echo_cancellation->stream_drift_samples());
    msg->set_level(gain_control()->stream_analog_level());
    msg->set_keypress(capture_.key_pressed);
  }
#endif

  MaybeUpdateHistograms();

  AudioBuffer* ca = capture_.capture_audio.get();

  if (constants_.use_experimental_agc &&
      public_submodules_->gain_control->is_enabled()) {
    private_submodules_->agc_manager->AnalyzePreProcess(
        ca->channels()[0], ca->num_channels(),
        capture_nonlocked_.fwd_proc_format.num_frames());
  }

  if (fwd_analysis_needed()) {
    ca->SplitIntoFrequencyBands();
  }

  if (capture_nonlocked_.beamformer_enabled) {
    private_submodules_->beamformer->ProcessChunk(*ca->split_data_f(),
                                                  ca->split_data_f());
    ca->set_num_channels(1);
  }

  public_submodules_->high_pass_filter->ProcessCaptureAudio(ca);
  RETURN_ON_ERR(public_submodules_->gain_control->AnalyzeCaptureAudio(ca));
  public_submodules_->noise_suppression->AnalyzeCaptureAudio(ca);

  // Ensure that the stream delay was set before the call to the
  // AEC ProcessCaptureAudio function.
  if (public_submodules_->echo_cancellation->is_enabled() &&
      !was_stream_delay_set()) {
    return AudioProcessing::kStreamParameterNotSetError;
  }

  RETURN_ON_ERR(public_submodules_->echo_cancellation->ProcessCaptureAudio(
      ca, stream_delay_ms()));

  if (public_submodules_->echo_control_mobile->is_enabled() &&
      public_submodules_->noise_suppression->is_enabled()) {
    ca->CopyLowPassToReference();
  }
  public_submodules_->noise_suppression->ProcessCaptureAudio(ca);
  if (capture_nonlocked_.intelligibility_enabled) {
    RTC_DCHECK(public_submodules_->noise_suppression->is_enabled());
    int gain_db = public_submodules_->gain_control->is_enabled() ?
                  public_submodules_->gain_control->compression_gain_db() :
                  0;
    public_submodules_->intelligibility_enhancer->SetCaptureNoiseEstimate(
        public_submodules_->noise_suppression->NoiseEstimate(), gain_db);
  }

  // Ensure that the stream delay was set before the call to the
  // AECM ProcessCaptureAudio function.
  if (public_submodules_->echo_control_mobile->is_enabled() &&
      !was_stream_delay_set()) {
    return AudioProcessing::kStreamParameterNotSetError;
  }

  RETURN_ON_ERR(public_submodules_->echo_control_mobile->ProcessCaptureAudio(
      ca, stream_delay_ms()));

  public_submodules_->voice_detection->ProcessCaptureAudio(ca);

  if (constants_.use_experimental_agc &&
      public_submodules_->gain_control->is_enabled() &&
      (!capture_nonlocked_.beamformer_enabled ||
       private_submodules_->beamformer->is_target_present())) {
    private_submodules_->agc_manager->Process(
        ca->split_bands_const(0)[kBand0To8kHz], ca->num_frames_per_band(),
        capture_nonlocked_.split_rate);
  }
  RETURN_ON_ERR(public_submodules_->gain_control->ProcessCaptureAudio(
      ca, echo_cancellation()->stream_has_echo()));

  if (fwd_synthesis_needed()) {
    ca->MergeFrequencyBands();
  }

  // TODO(aluebs): Investigate if the transient suppression placement should be
  // before or after the AGC.
  if (capture_.transient_suppressor_enabled) {
    float voice_probability =
        private_submodules_->agc_manager.get()
            ? private_submodules_->agc_manager->voice_probability()
            : 1.f;

    public_submodules_->transient_suppressor->Suppress(
        ca->channels_f()[0], ca->num_frames(), ca->num_channels(),
        ca->split_bands_const_f(0)[kBand0To8kHz], ca->num_frames_per_band(),
        ca->keyboard_data(), ca->num_keyboard_frames(), voice_probability,
        capture_.key_pressed);
  }

  // The level estimator operates on the recombined data.
  public_submodules_->level_estimator->ProcessStream(ca);

  capture_.was_stream_delay_set = false;
  return kNoError;
}

```

It can be seen that nearend's processing is comprehensive and the process is clear. It can be more practical to open different modules to meet the needs of different scenarios, which has a positive improvement effect for general communication systems. But in the actual work also found some hidden dangers in the process. 

In addition, the processing of `each module` of the structure is *relatively low*, which should be an excellent feature. However, it is difficult to reach the target effect in signal processing in complex cases. The waste of computational load due to low coupling is even more un-avoidable.

----

**Conclusion**

----

Audio signal processing is a subfield of signal processing that is concerned with the electronic manipulation of audio signals. Audio signals are electronic representations of sound waves—longitudinal waves which travel through air, consisting of compressions and rarefactions. The energy contained in audio signals is typically measured in decibels. As audio signals may be represented in either digital or analog format, processing may occur in either domain. Analog processors operate directly on the electrical signal, while digital processors operate mathematically on its digital representation.

WebRTC APM module is a complete package to perform every mathematical operation over signal processing. Best use of APM is, first to understand its basic functionality. I've tried to provide the best possible understanding of WebRTC module with a working example.

