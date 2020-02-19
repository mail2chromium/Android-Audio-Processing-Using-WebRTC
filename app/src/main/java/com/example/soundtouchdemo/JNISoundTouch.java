package com.example.soundtouchdemo;

public class JNISoundTouch {

	public JNISoundTouch(){
		create();
		_init = true;
	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		if (_init) {
			close();
		}
	}

	public void close(){
		if(_init){
			destroy();
			_init = false;
		}
	}

	public native void setSampleRate(int sampleRate);
	public native void setChannels(int channel);
	public native void setTempoChange(float newTempo); //变速不变调  newTempo:[-50, 100]
	public native void setPitchSemiTones(int newPitch);
	public native void setRateChange(float newRate);
	
	public native void putSamples(short[] samples, int len);
	public native short[] receiveSamples();

	private native void create();
	private native void destroy();


	private long objData; // do not modify it.
	private boolean _init = false;
	
	static{
		System.loadLibrary("soundtouch");
	}
}
