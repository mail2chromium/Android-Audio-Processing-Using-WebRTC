package com.webrtc.audioprocessing;


public class ApmViewModel {

    private String targetPort = "2004";
    private String targetIP = "192.168.1.167";
    private String agcCompressionGain = "9";
    private String agcTargetLevel = "6";
    private String aceBufferDelay = "150";

    private int agcTargetLevelInt = 6;
    private int agcCompressionGainInt = 9;

    private boolean start = false;

    //Speech Quality
    private boolean highPassFilter = false;
    private boolean speechIntelligibilityEnhance = true;
    private boolean beamForming = false;
    private boolean speaker = true;

    //AEC
    private boolean aecPC = false;

    private boolean aecExtendFilter = false;
    private boolean delayAgnostic = true;
    private boolean nextGenerationAEC = false;

    private boolean aecPCMode0 = false;
    private boolean aecPCMode1 = false;
    private boolean aecPCMode2 = true;

    private boolean aecMobile = true;

    private boolean aecMobileMode0 = false;
    private boolean aecMobileMode1 = false;
    private boolean aecMobileMode2 = false;
    private boolean aecMobileMode3 = false;
    private boolean aecMobileMode4 = true;

    private boolean aecNone = false;

    //NS
    private boolean ns = true;
    private boolean experimentalNS = false;
    private boolean nsMode0 = false;
    private boolean nsMode1 = false;
    private boolean nsMode2 = true;
    private boolean nsMode3 = false;

    //AGC
    private boolean agc = true;
    private boolean experimentalAGC = false;
    private boolean agcMode0 = false;
    private boolean agcMode1 = true;
    private boolean agcMode2 = false;

    //VAD
    private boolean vad = true;
    private int rcvCount = 0;
    private int sndCount = 0;


    public String getTargetPort() {
        return targetPort;
    }

    public void setTargetPort(String targetPort) {
        this.targetPort = targetPort;
    }


    public String getTargetIP() {
        return targetIP;
    }

    public void setTargetIP(String targetIP) {
        this.targetIP = targetIP;
    }


    public boolean getHighPassFilter() {
        return highPassFilter;
    }

    public void setHighPassFilter(boolean highPassFilter) {
        this.highPassFilter = highPassFilter;
    }


    public boolean getSpeechIntelligibilityEnhance() {
        return speechIntelligibilityEnhance;
    }

    public void setSpeechIntelligibilityEnhance(boolean speechIntelligibilityEnhance) {
        this.speechIntelligibilityEnhance = speechIntelligibilityEnhance;
    }


    public boolean getBeamForming() {
        return beamForming;
    }

    public void setBeamForming(boolean beamForming) {
        this.beamForming = beamForming;
    }


    public boolean getAecPC() {
        return aecPC;
    }

    public void setAecPC(boolean aecPC) {
        this.aecPC = aecPC;
    }


    public boolean getAecMobile() {
        return aecMobile;
    }

    public void setAecMobile(boolean aecMobile) {
        this.aecMobile = aecMobile;
    }


    public boolean getAecNone() {
        return aecNone;
    }

    public void setAecNone(boolean aecNone) {
        this.aecNone = aecNone;
    }


    public boolean getAecExtendFilter() {
        return aecExtendFilter;
    }

    public void setAecExtendFilter(boolean aecExtendFilter) {
        this.aecExtendFilter = aecExtendFilter;
    }


    public boolean getDelayAgnostic() {
        return delayAgnostic;
    }

    public void setDelayAgnostic(boolean delayAgnostic) {
        this.delayAgnostic = delayAgnostic;
    }


    public boolean getNextGenerationAEC() {
        return nextGenerationAEC;
    }

    public void setNextGenerationAEC(boolean nextGenerationAEC) {
        this.nextGenerationAEC = nextGenerationAEC;
    }


    public String getAceBufferDelay() {
        return aceBufferDelay;
    }

    public void setAceBufferDelay(String aceBufferDelay) {
        this.aceBufferDelay = aceBufferDelay;
    }

    public short getAceBufferDelayMs() {
        try {
            return (short) Integer.parseInt(aceBufferDelay);
        } catch (Exception e) {
            return 150;
        }
    }


    public boolean getAecPCMode0() {
        return aecPCMode0;
    }

    public void setAecPCMode0(boolean aecMode) {
        this.aecPCMode0 = aecMode;
    }


    public boolean getAecPCMode1() {
        return aecPCMode1;
    }

    public void setAecPCMode1(boolean aecMode) {
        this.aecPCMode1 = aecMode;
    }


    public boolean getAecPCMode2() {
        return aecPCMode2;
    }

    public void setAecPCMode2(boolean aecMode) {
        this.aecPCMode2 = aecMode;
    }


    public boolean getAecMobileMode0() {
        return aecMobileMode0;
    }

    public void setAecMobileMode0(boolean aecMode) {
        this.aecMobileMode0 = aecMode;
    }


    public boolean getAecMobileMode1() {
        return aecMobileMode1;
    }

    public void setAecMobileMode1(boolean aecMode) {
        this.aecMobileMode1 = aecMode;
    }


    public boolean getAecMobileMode2() {
        return aecMobileMode2;
    }

    public void setAecMobileMode2(boolean aecMode) {
        this.aecMobileMode2 = aecMode;
    }


    public boolean getAecMobileMode3() {
        return aecMobileMode3;
    }

    public void setAecMobileMode3(boolean aecMode) {
        this.aecMobileMode3 = aecMode;
    }


    public boolean getAecMobileMode4() {
        return aecMobileMode4;
    }

    public void setAecMobileMode4(boolean aecMode) {
        this.aecMobileMode4 = aecMode;
    }


    public boolean getNs() {
        return ns;
    }

    public void setNs(boolean ns) {
        this.ns = ns;
    }


    public boolean getExperimentalNS() {
        return experimentalNS;
    }

    public void setExperimentalNS(boolean experimentalNS) {
        this.experimentalNS = experimentalNS;
    }


    public boolean getNsMode0() {
        return nsMode0;
    }

    public void setNsMode0(boolean nsMode0) {
        this.nsMode0 = nsMode0;
    }


    public boolean getNsMode1() {
        return nsMode1;
    }

    public void setNsMode1(boolean nsMode1) {
        this.nsMode1 = nsMode1;
    }


    public boolean getNsMode2() {
        return nsMode2;
    }

    public void setNsMode2(boolean nsMode2) {
        this.nsMode2 = nsMode2;
    }


    public boolean getNsMode3() {
        return nsMode3;
    }

    public void setNsMode3(boolean nsMode3) {
        this.nsMode3 = nsMode3;
    }


    public boolean getAgc() {
        return agc;
    }

    public void setAgc(boolean agc) {
        this.agc = agc;
    }


    public boolean getExperimentalAGC() {
        return experimentalAGC;
    }

    public void setExperimentalAGC(boolean experimentalAGC) {
        this.experimentalAGC = experimentalAGC;
    }


    public boolean getAgcMode1() {
        return agcMode1;
    }

    public void setAgcMode1(boolean agcMode1) {
        this.agcMode1 = agcMode1;
    }


    public boolean getAgcMode2() {
        return agcMode2;
    }

    public void setAgcMode2(boolean agcMode2) {
        this.agcMode2 = agcMode2;
    }


    public boolean getAgcMode0() {
        return agcMode0;
    }

    public void setAgcMode0(boolean agcMode0) {
        this.agcMode0 = agcMode0;
    }


    public String getAgcTargetLevel() {
        return agcTargetLevel;
    }

    public void setAgcTargetLevel(String level) {
        try {
            agcTargetLevelInt = (short) Integer.parseInt(level);
            if (agcTargetLevelInt > 31) agcTargetLevelInt = 31;
            if (agcTargetLevelInt < 0) agcTargetLevelInt = 0;
            this.agcTargetLevel = agcTargetLevelInt + "";
        } catch (Exception e) {
        }
    }


    public int getAgcTargetLevelInt() {
        return agcTargetLevelInt;
    }


    public String getAgcCompressionGain() {
        return agcCompressionGain;
    }

    public void setAgcCompressionGain(String gain) {
        try {
            agcCompressionGainInt = (short) Integer.parseInt(gain);
            if (agcCompressionGainInt > 90) agcCompressionGainInt = 90;
            if (agcCompressionGainInt < 0) agcCompressionGainInt = 0;
            this.agcCompressionGain = agcCompressionGainInt + "";
        } catch (Exception e) {
        }
    }


    public int getAgcCompressionGainInt() {
        return agcCompressionGainInt;
    }


    public boolean getVad() {
        return vad;
    }

    public void setVad(boolean vad) {
        this.vad = vad;
    }

    public void setSpeaker(boolean speaker) {
        this.speaker = speaker;
    }

    public boolean getSpeaker() {
        return speaker;
    }


    public String getRcvCount() {
        return rcvCount + "";
    }

    public void setRcvCount(int rcvCount) {
        this.rcvCount = rcvCount;
    }


    public String getSndCount() {
        return sndCount + "";
    }

    public void setSndCount(int count) {
        this.sndCount = count;
    }


    public boolean getStart() {
        return start;
    }


//    public void onSpeaker(View view) {
//        boolean on = ((CheckBox) view).isChecked();
////        activity.onSpeaker(on);
//    }

    public void setStart(boolean start) {
        this.start = start;
    }

    @Override
    public String toString() {
        return "ApmViewModel{" +
                "targetPort='" + targetPort + '\'' +
                ", targetIP='" + targetIP + '\'' +
                ", agcCompressionGain='" + agcCompressionGain + '\'' +
                ", agcTargetLevel='" + agcTargetLevel + '\'' +
                ", aceBufferDelay='" + aceBufferDelay + '\'' +
                ", agcTargetLevelInt=" + agcTargetLevelInt +
                ", agcCompressionGainInt=" + agcCompressionGainInt +
                ", start=" + start +
                ", highPassFilter=" + highPassFilter +
                ", speechIntelligibilityEnhance=" + speechIntelligibilityEnhance +
                ", beamForming=" + beamForming +
                ", speaker=" + speaker +
                ", aecPC=" + aecPC +
                ", aecExtendFilter=" + aecExtendFilter +
                ", delayAgnostic=" + delayAgnostic +
                ", nextGenerationAEC=" + nextGenerationAEC +
                ", aecPCMode0=" + aecPCMode0 +
                ", aecPCMode1=" + aecPCMode1 +
                ", aecPCMode2=" + aecPCMode2 +
                ", aecMobile=" + aecMobile +
                ", aecMobileMode0=" + aecMobileMode0 +
                ", aecMobileMode1=" + aecMobileMode1 +
                ", aecMobileMode2=" + aecMobileMode2 +
                ", aecMobileMode3=" + aecMobileMode3 +
                ", aecMobileMode4=" + aecMobileMode4 +
                ", aecNone=" + aecNone +
                ", ns=" + ns +
                ", experimentalNS=" + experimentalNS +
                ", nsMode0=" + nsMode0 +
                ", nsMode1=" + nsMode1 +
                ", nsMode2=" + nsMode2 +
                ", nsMode3=" + nsMode3 +
                ", agc=" + agc +
                ", experimentalAGC=" + experimentalAGC +
                ", agcMode0=" + agcMode0 +
                ", agcMode1=" + agcMode1 +
                ", agcMode2=" + agcMode2 +
                ", vad=" + vad +
                ", rcvCount=" + rcvCount +
                ", sndCount=" + sndCount +
                '}';
    }
}
