# "APM (Audio Processing Module) should be placed in the signal chain as close to the audio hardware abstraction layer (HAL) as possible."

-----

**Getting Started**
------
High performance audio apps typically require more functionality than the simple ability to play or record sound. They demand responsive realtime system behavior. Webrtc provides browsers and *mobile applications* with Real-Time Communications (RTC) capabilities via simple APIs. 

WebRTC supports  (Audio Microphone Collection), (Encoding), and (RTP packet transmission). Features of *Audio Data* that Webrtc-APM accepts only include;

- 16-bit linear PCM Audio Data.
- 160-frames of 10 ms. (Optimal Sample Rate)
- Multiple channels should be interleaved.


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
AudioProcessing is event-driven system, which includes follwing events such as;

- Initialize Event
- Play Event
- Capturing Audio Event
- Rendering the Audio Event
- Release APM Event


-------
**Webrtc Native Modules Hierarchy** 
-------



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
