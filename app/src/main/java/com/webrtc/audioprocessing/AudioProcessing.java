package com.webrtc.audioprocessing;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.media.audiofx.AutomaticGainControl;
import android.os.Handler;
import android.os.Looper;
import android.os.Process;
import android.util.Log;

import com.example.soundtouchdemo.JNISoundTouch;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class AudioProcessing {

    private Apm _apm;
    private ApmViewModel vm;
    private Context context;
    private Counters counter;

    private int _nsLevel = 2;
    private int _agcLevel = 0;
    private int _sendCount = 0;
    private int _aecPCLevel = 2;
    private int _receveCount = 0;

    private final int PORT = 5432;

    private int _aecMobileLevel = 2;
    private TrackThread _trackThread;
    private RecordThread _recordThread;
    private ReceiveThread _recvThread;

    private DatagramSocket _dataSocket;

    private final int buffer_count = 15;
    private static final int CHANNELS = 1;
    private Handler handler = new Handler();

    private String keyTargetIP = "targetIP";
    private String keyTargetPort = "targetPort";

    private static final int SAMPLE_RATE = 16000;
    private static final int BITS_PER_SAMPLE = 16;

    private static final int AEC_BUFFER_SIZE_MS = 10;
    private static final int CALLBACK_BUFFER_SIZE_MS = 10;
    private static final int BUFFERS_PER_SECOND = 1000 / CALLBACK_BUFFER_SIZE_MS;

    private SyncQueue<short[]> _receveQueue = new SyncQueue<short[]>(buffer_count);
    private static final int AEC_LOOP_COUNT = CALLBACK_BUFFER_SIZE_MS / AEC_BUFFER_SIZE_MS;
    private static final int JITTER_STEP_SIZE = CHANNELS * SAMPLE_RATE / BUFFERS_PER_SECOND;

    private Runnable run = new Runnable() {
        @Override
        public void run() {
            handler.postDelayed(this, 1000);
            vm.setRcvCount(_receveCount);
            vm.setSndCount(_sendCount);
        }
    };


    private SharedPreferences sharedpreferences;

    public AudioProcessing(Context context, ApmViewModel apmViewModel, Counters counter) {
        this.context = context;
        this.counter = counter;
        vm = apmViewModel;
        sharedpreferences = context.getSharedPreferences("MyPREFERENCES", Context.MODE_PRIVATE);
        Log.d("abcd", vm.toString());
        for (int i = 0; i < buffer_count; ++i) {
            short[] a = new short[JITTER_STEP_SIZE];
            try {
                _receveQueue.Consumer_Put(a);
            } catch (InterruptedException e) {
            }
        }

        handler.postDelayed(run, 1000);


        Start();

    }

    void getLevels() {
        if (vm.getAecPCMode0()) {
            _aecPCLevel = 0;
        } else if (vm.getAecPCMode1()) {
            _aecPCLevel = 1;
        } else if (vm.getAecPCMode2()) {
            _aecPCLevel = 2;
        }

        if (vm.getAecMobileMode0()) {
            _aecMobileLevel = 0;
        } else if (vm.getAecMobileMode1()) {
            _aecMobileLevel = 1;
        } else if (vm.getAecMobileMode2()) {
            _aecMobileLevel = 2;
        } else if (vm.getAecMobileMode3()) {
            _aecMobileLevel = 3;
        } else if (vm.getAecMobileMode4()) {
            _aecMobileLevel = 4;
        }

        if (vm.getNsMode0()) {
            _nsLevel = 0;
        } else if (vm.getNsMode1()) {
            _nsLevel = 1;
        } else if (vm.getNsMode2()) {
            _nsLevel = 2;
        } else if (vm.getNsMode3()) {
            _nsLevel = 3;
        }

        if (vm.getAgcMode0()) {
            _agcLevel = 0;
        } else if (vm.getAgcMode1()) {
            _agcLevel = 1;
        } else if (vm.getAgcMode2()) {
            _agcLevel = 2;
        }
    }

    public void Start() {
        if (_trackThread == null || _recordThread == null) {
            try {
                InetAddress localAddr = InetAddress.getByName("0.0.0.0");
                _dataSocket = new DatagramSocket(Integer.parseInt(vm.getTargetPort()), localAddr);
                _dataSocket.setReuseAddress(true);


                _dataSocket.setSendBufferSize(2 * 1024 * 1024); // 2,097,152
                _dataSocket.setReceiveBufferSize(2 * 1024 * 1024);

            } catch (SocketException ex) {
                new AlertDialog.Builder(context).setTitle("\n" + "System hint")
                        .setMessage(ex.getMessage())
                        .show();
                return;
            } catch (UnknownHostException ex) {
                new AlertDialog.Builder(context).setTitle("\n" + "System hint")
                        .setMessage(ex.getMessage())
                        .show();
                return;
            } catch (Exception ex) {
                new AlertDialog.Builder(context).setTitle("\n" + "System hint")
                        .setMessage("Network Error\n" + ex.getMessage())
                        .show();
                return;
            }

            getLevels();

            int ret = -1;
            try {

                _apm = new Apm(vm.getAecExtendFilter(), vm.getSpeechIntelligibilityEnhance(), vm.getDelayAgnostic(), vm.getBeamForming(),
                        vm.getNextGenerationAEC(), vm.getExperimentalNS(), vm.getExperimentalAGC());

                ret = _apm.HighPassFilter(vm.getHighPassFilter());

                if (vm.getAecPC()) {
                    ret = _apm.AECClockDriftCompensation(false);
                    ret = _apm.AECSetSuppressionLevel(Apm.AEC_SuppressionLevel.values()[_aecPCLevel]);
                    ret = _apm.AEC(true);
                } else if (vm.getAecMobile()) {
                    ret = _apm.AECMSetSuppressionLevel(Apm.AECM_RoutingMode.values()[_aecMobileLevel]);
                    ret = _apm.AECM(true);
                }

                ret = _apm.NSSetLevel(Apm.NS_Level.values()[_nsLevel]);
                ret = _apm.NS(vm.getNs());

                ret = _apm.VAD(vm.getVad());

                if (vm.getAgc()) {
                    ret = _apm.AGCSetAnalogLevelLimits(0, 255);
                    ret = _apm.AGCSetMode(Apm.AGC_Mode.values()[_agcLevel]);
                    ret = _apm.AGCSetTargetLevelDbfs(vm.getAgcTargetLevelInt());
                    ret = _apm.AGCSetcompressionGainDb(vm.getAgcCompressionGainInt());
                    ret = _apm.AGCEnableLimiter(true);
                    ret = _apm.AGC(true);
                }
            } catch (Exception ex) {
                new AlertDialog.Builder(context).setTitle("System hint")
                        .setMessage(ex.getMessage())
                        .show();
                return;
            }

            _recvThread = new ReceiveThread();
            _recvThread.Start();

            _trackThread = new TrackThread();
            _trackThread.Start();

            _recordThread = new RecordThread();
            _recordThread.Start();


            try {
                vm.setStart(true);
            } catch (Exception ex) {
                ex.printStackTrace();
            }

        } else {
            vm.setStart(false);

            _dataSocket.close();

            _recvThread.Stop();
            _recordThread.Stop();
            _trackThread.Stop();
            _dataSocket = null;
            _recvThread = null;
            _recordThread = null;
            _trackThread = null;

            _apm.close();

            _receveCount = 0;
            _sendCount = 0;
        }

        SharedPreferences.Editor editor = sharedpreferences.edit();

        editor.putString(keyTargetIP, vm.getTargetIP());
        editor.putString(keyTargetPort, vm.getTargetPort());
        editor.commit();
    }

    public void onSpeaker(boolean on) {
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
        audioManager.setSpeakerphoneOn(on);
//        boolean b = audioManager.isVolumeFixed();
//        boolean c = b;
    }


    public void onDestroy() {
        vm.setStart(false);
        if (_dataSocket != null) {
            _dataSocket.close();
            _dataSocket = null;
        }
        if (_recvThread != null) {
            _recvThread.Stop();
            _recvThread = null;
        }
        if (_recordThread != null) {
            _recordThread.Stop();
            _recordThread = null;
        }
        if (_trackThread != null) {
            _trackThread.Stop();
            _trackThread = null;
        }
        if (_apm != null) {
            _apm.close();
        }

        _receveCount = 0;
        _sendCount = 0;

        if (handler != null) {
            handler.removeCallbacks(run);
        }
    }


    private class RecordThread extends Thread {

        private boolean _done = false;
        private AudioRecord _audioRecord = null;

        private static final int CHANNELS = 1;

        // Default audio data format is PCM 16 bit per sample.
        // Guaranteed to be supported by all devices.
        private static final int BITS_PER_SAMPLE = 16;

        private static final int SAMPLE_RATE = 16000;

        // Requested size of each recorded buffer provided to the client.

        // Average number of callbacks per second.
        private static final int BUFFERS_PER_SECOND = 1000 / CALLBACK_BUFFER_SIZE_MS;

        // We ask for a native buffer size of BUFFER_SIZE_FACTOR * (minimum required
        // buffer size). The extra space is allocated to guard against glitches under
        // high load.
        private static final int BUFFER_SIZE_FACTOR = 2;

        public void Start() {
            _done = false;
            this.start();
        }

        public void Stop() {
            _done = true;
            try {
                this.join();
            } catch (InterruptedException ex) {
            }
        }

        @Override
        public void run() {
            super.run();

            Process.setThreadPriority(Process.THREAD_PRIORITY_URGENT_AUDIO);

            final int bytesPerFrame = CHANNELS * (BITS_PER_SAMPLE / 8);
            final int framesPerBuffer = SAMPLE_RATE / BUFFERS_PER_SECOND;

            int minBufferSize = AudioRecord.getMinBufferSize(
                    SAMPLE_RATE,
                    AudioFormat.CHANNEL_IN_MONO,
                    AudioFormat.ENCODING_PCM_16BIT);

            // Use a larger buffer size than the minimum required when creating the
            // AudioRecord instance to ensure smooth recording under load. It has been
            // verified that it does not increase the actual recording latency.

            int bufferSizeInBytes =
                    Math.max(BUFFER_SIZE_FACTOR * minBufferSize, 0);

            short[] processBuffer = new short[bytesPerFrame * framesPerBuffer / 2];
            byte[] sendBuffer = new byte[bytesPerFrame * framesPerBuffer];

            DatagramPacket dataPacket;
            try {
                InetAddress dstAddress = InetAddress.getByName(vm.getTargetIP());
                Log.d("abcd", "ip " + vm.getTargetIP() + "    port" + vm.getTargetPort());
                String port = vm.getTargetPort();
                dataPacket = new DatagramPacket(sendBuffer, 0, sendBuffer.length, dstAddress, Integer.parseInt(port));

                int audioSource = MediaRecorder.AudioSource.VOICE_COMMUNICATION;
                _audioRecord = new AudioRecord(audioSource,
                        SAMPLE_RATE,
                        AudioFormat.CHANNEL_IN_MONO,
                        AudioFormat.ENCODING_PCM_16BIT,
                        bufferSizeInBytes);

                if (AutomaticGainControl.isAvailable()) {
                    AutomaticGainControl agc = AutomaticGainControl.create(
                            _audioRecord.getAudioSessionId()
                    );
                    agc.setEnabled(false);
                }

/*
                Context context = getApplicationContext();
                AudioManager audioManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
                int index = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
                int volume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 30, 0);//*/

                _audioRecord.startRecording();

            } catch (IllegalArgumentException ex) {
                Log.e("record", ex.getMessage());
                return;
            } catch (Exception ex) {
                Log.e("record", ex.getMessage());
                return;
            }

            int out_analog_level = 200;
            try {
                while (!_done) {

                    int bytesRead = _audioRecord.read(processBuffer, 0, processBuffer.length);
                    if (bytesRead == processBuffer.length) {
                        for (int i = 0; i < AEC_LOOP_COUNT; ++i) {
                            int processBufferOffSet = i * processBuffer.length / AEC_LOOP_COUNT;

                            _apm.SetStreamDelay(vm.getAceBufferDelayMs());
                            if (vm.getAgc()) {
                                _apm.AGCSetStreamAnalogLevel(out_analog_level);
                            }

                            _apm.ProcessCaptureStream(processBuffer, processBufferOffSet);

                            if (vm.getAgc()) {
                                out_analog_level = _apm.AGCStreamAnalogLevel();
//                                Log.i("AGC", out_analog_level + "");
                            }

                            if (vm.getVad()) {
                                if (!_apm.VADHasVoice()) continue;
                            }
                        }

                        /*
                        final int USHORT_MASK = (1 << 16) - 1;
                        for(int j = 0 ; j < processBuffer.length; ++j) {
                            int sample = (int) processBuffer[j] & USHORT_MASK;
                            sample *= 5.0f;
                            processBuffer[j] = (short)(sample & USHORT_MASK);
                        }
                        //*/

                        ByteBuffer.wrap(sendBuffer).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().put(processBuffer);
                        _dataSocket.send(dataPacket);
                        _sendCount++;
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                counter.sendCount(_sendCount);

                            }
                        });


                    } else {
//                        Log.e("record", "AudioRecord.read failed: " + bytesRead);
                    }
                }
            } catch (IOException ex) {

                if (vm.getStart()) ;
                ex.printStackTrace();

            } catch (Exception ex) {
                if (vm.getStart()) ;
                ex.printStackTrace();
            }
            _audioRecord.stop();
            _audioRecord.release();
        }
    }

    private class ReceiveThread extends Thread {

        private boolean _done = false;

        public void Start() {
            _done = false;
            this.start();
        }

        public void Stop() {
            _done = true;
            try {
                this.join();
            } catch (InterruptedException ex) {

            }
        }

        @Override
        public void run() {
            super.run();

            final int bytesPerFrame = CHANNELS * (BITS_PER_SAMPLE / 8);
            byte[] recvBuffer = new byte[bytesPerFrame * (SAMPLE_RATE / BUFFERS_PER_SECOND)];
            DatagramPacket dataPacket = new DatagramPacket(recvBuffer, 0, recvBuffer.length);

            try {
                while (!_done) {
                    _dataSocket.receive(dataPacket);
                    _receveCount++;
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            counter.receivedCount(_receveCount);
                        }
                    });
                    short[] buffer = _receveQueue.Producer_Get(150L);
                    if (buffer != null) {
                        ByteBuffer.wrap(recvBuffer).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(buffer);
                        _receveQueue.Producer_Put(buffer);
                    }
                }
            } catch (IOException ex) {
                if (vm.getStart()) ;
                Log.e("recv", ex.getMessage());
            } catch (Exception ex) {
                if (vm.getStart()) ;
                Log.e("recv", ex.getMessage());
            }
        }
    }

    private class TrackThread extends Thread {

        class PlayedSamples {

            public void setPlayedSamples(int samples) {
                long current = getUnsignedInt(samples);
                if (current < _low) {
                    _high += 0x00000000ffffffffL;
                }
                _low = current;
            }

            public long getPlayedSamples() {
                return _high + _low;
            }

            private long getUnsignedInt(int data) {
                return data & 0x00000000ffffffffL;
            }

            private long _low = 0;
            private long _high = 0;
        }

        private JNISoundTouch soundtouch = new JNISoundTouch();

        private boolean _done = false;

        private AudioTrack _audioTrack = null;

        public void Start() {
            _done = false;
            this.start();
        }

        public void Stop() {
            _done = true;
            try {
                this.join();
            } catch (InterruptedException ex) {

            }
        }

        public long getUnsignedInt(int data) {
            return data & 0x00000000ffffffffL;
        }

        public int getUnsignedShortInt(short data) {
            return data & 0x0000ffff;
        }

        @Override
        public void run() {
            super.run();

            soundtouch.setSampleRate(16000);
            soundtouch.setChannels(1);

            Process.setThreadPriority(Process.THREAD_PRIORITY_URGENT_AUDIO);

            final int bytesPerFrame = CHANNELS * (BITS_PER_SAMPLE / 8);
            final int minBufferSizeInBytes = AudioTrack.getMinBufferSize(
                    SAMPLE_RATE,
                    AudioFormat.CHANNEL_OUT_MONO,
                    AudioFormat.ENCODING_PCM_16BIT);

            try {

                // Create an AudioTrack object and initialize its associated audio buffer.
                // The size of this buffer determines how long an AudioTrack can play
                // before running out of data.
                _audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                        SAMPLE_RATE,
                        AudioFormat.CHANNEL_OUT_MONO,
                        AudioFormat.ENCODING_PCM_16BIT,
                        minBufferSizeInBytes,
                        AudioTrack.MODE_STREAM);

                _audioTrack.play();

            } catch (IllegalArgumentException ex) {

                Log.e("track", ex.getMessage());
                return;
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            long writtenSamples = 0;
            PlayedSamples playedSamples = new PlayedSamples();

            try {
                while (!_done) {

                    short[] buffer = _receveQueue.Consumer_Get(150L);
                    if (buffer != null) {
                        for (int i = 0; i < AEC_LOOP_COUNT; ++i) {
                            int bufferOffSet = i * buffer.length / AEC_LOOP_COUNT;
                            if (!vm.getAecNone()) {
                                _apm.ProcessRenderStream(buffer, bufferOffSet);
                            }
                        }

                        int size = _receveQueue.UsedSize();
                        if (size >= 5 /*|| (writtenSamples - playedSamples.getPlayedSamples() >= 800)*/) {
//                            Log.d("soundtouch", "process " + size);
                            soundtouch.setTempoChange(20);
                            soundtouch.putSamples(buffer, buffer.length);

                            short[] data;
                            do {

                                data = soundtouch.receiveSamples();
                                if (data.length <= 0) break;

//                                Log.i("track", writtenSamples - playedSamples.getPlayedSamples() + "");
                                playedSamples.setPlayedSamples(_audioTrack.getPlaybackHeadPosition());
                                int samplesWritten = _audioTrack.write(data, 0, data.length);
                                if (samplesWritten != data.length) {
                                    _done = true;
                                }
                                writtenSamples += samplesWritten;

                            } while (true);
                        } else {
//                            Log.i("track", writtenSamples - playedSamples.getPlayedSamples() + "");
                            playedSamples.setPlayedSamples(_audioTrack.getPlaybackHeadPosition());
                            int samplesWritten = _audioTrack.write(buffer, 0, buffer.length);
                            if (samplesWritten != buffer.length) {
                                _done = true;
                            }
                            writtenSamples += samplesWritten;
                        }
                        _receveQueue.Consumer_Put(buffer);
                    }
                }
            } catch (Exception ex) {
                if (vm.getStart()) ;
                Log.e("track", ex.getMessage());
            }
            _audioTrack.stop();
            _audioTrack.release();
        }
    }
}
