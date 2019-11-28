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

Most of the time voice communication is just a barrier in *VoIP Industry* due to **muffled and stuttering voice**. Which includes `distortion`, `echo`, `noise` and very `unstisfactory` output results. The most common and even more worse **causes** that make make real-time communication such as:

- Packet loss concealment
- Echo cancellation
- Bandwidth adaptivity
- Dynamic jitter buffering
- Automatic gain control problems
- Noise reduction and suppression


------
**Features**
------
WebRTC offers a complete stack for voice communications. It includes not only the necessary codecs, but other components necessary to great user experiences. This `APM` includes series of software-based algorithms such;

- [Acoustic Echo Cancellation](https://github.com/mail2chromium/Android-Acoustic-Echo-Cancelletion-Using-WebRTC) **(AEC)**
- [Automatic Gain Controller](https://github.com/mail2chromium/Android-Automatic-Gain-Control-Using-WebRTC) **(AGC)** 
- [Noise Suppression](https://github.com/mail2chromium/Android-Noise-Suppression-Using-WebRTC) **(NS)**
- [Voice Activity Detection](https://github.com/mail2chromium/Android-Voice-Activity-Detection-Using-WebRTC) **(VAD)** 
- [Webrtc Audio Codecs](https://developer.mozilla.org/en-US/docs/Web/Media/Formats/WebRTC_codecs)  (The currently supported **Voice-Codecs** are `Opus`, `G.711`, `G.722`, `iLBC`, and `iSAC`)

which also includes hardware access and control across multiple platforms i.e **Mobiles**.
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

To enable each of the audio processing module and before getting into basic event flow of APM, one should first invoke Audio Processing Instialization & Configuration such as;  

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

After instantiation and configuration of APM module, now we'll look into account for various events involved in webrtc APM architecture such as;

-------


1. APM Initialization Event:
------

In this event, we'll simply set the required parameter for AM such as the (sample rate), (audio capturing & playback device) and (number of channels) of local and remote audio stream such as;

```

// set the sample rate of local and remote audio stream
apm-> set_sample_rate_hz (sample_rate_hz); 

// set the sample rate audio equipment, we assume that the audio capture and playback device 
// using the same sampling rate. (Must be called when the drift component is enabled)

apm-> echo_cancellation () -> set_device_sample_rate_hz (); 
// set the local and remote audio stream of the number of channels
apm-> set_num_channels (num_capture_input_channels, num_capture_output_channels);


```

2. Capturing Audio Event
------

To capture custom audio data from hardware abstraction layer such as microphone, one must know the following steps as given; 

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

3. Rendering the Audio Event
------

After capturing of Audio Data, incoming stream is just passed to 


```
// Capture frame arrives from the audio HAL
ProcessStream(capture_frame)
```

that will process this input-data from APM.Now to get Ouput-data that is further played is actually obtained such as;

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


4. - Play Audio Event
------

To analysis far end `10ms` frame data of the audio stream, these data provide a reference for echo suppression. (Enable echo suppression when calling needs)

```
apm-> AnalyzeReverseStream (& far_frame));
``` 

5. Release APM Event
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

![Native Modules Hierarchy](https://github.com/mail2chromium/Android-Audio-Processing-Using-WebRTC/blob/master/Content.png)


-------
**Audio Processing Content**
-------

Here is the content of the `<audioprocessing.h>` file:
```class AudioFrame;

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
class VoiceDetection;```
