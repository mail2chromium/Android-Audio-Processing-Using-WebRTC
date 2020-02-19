package com.webrtc.audioprocessing;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Created by sino on 2015-05-29.
 */
public class SyncQueue<T> {

    private ArrayBlockingQueue<T> _free ;
    private ArrayBlockingQueue<T> _used ;
    private int _total;

    public SyncQueue(int elementCount){
        _free = new ArrayBlockingQueue<T>(elementCount);
        _used = new ArrayBlockingQueue<T>(elementCount);
        _total = elementCount;
    }

    public void Consumer_Put(T e) throws InterruptedException {
        _free.put(e);
    }

    public T Consumer_Get(long timeOut) throws InterruptedException {
        return _used.poll(timeOut, TimeUnit.MILLISECONDS);
    }

    public void Producer_Put(T e) throws InterruptedException {
        _used.put(e);
    }

    public T Producer_Get(long timeOut) throws InterruptedException {
        return _free.poll(timeOut, TimeUnit.MILLISECONDS);
    }

    public int Total(){return _total;}

    public int UsedSize(){
        return _used.size();
    }

    public boolean isFreeEmpty(){
        return _free.isEmpty();
    }
    public boolean isUsedEmpty(){
        return _used.isEmpty();
    }
}
