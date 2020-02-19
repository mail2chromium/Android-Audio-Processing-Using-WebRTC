package com.webrtc.audioprocessing;

public interface Counters {
    void sendCount(int count);
    void receivedCount(int count);
}
