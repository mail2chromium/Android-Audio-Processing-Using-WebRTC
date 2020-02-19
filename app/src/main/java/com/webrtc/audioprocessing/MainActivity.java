package com.webrtc.audioprocessing;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.apmtest.R;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, RadioGroup.OnCheckedChangeListener, Counters {
    private EditText etTargetPort;
    private EditText etTargetIP;
    private EditText etAgcTargetLevel;
    private EditText etAgcCompressionGain;
    private View aecPcView;
    private View aecBufferDelayView;
    private View nsModeView;
    private View agcModeView;
    private View agcTlView;
    private View agcCgView;


    private CheckBox cbHighPassFilter;
    private CheckBox cbSpeechIntelligibilityEnhance;
    private CheckBox cbSpeaker;
    private RadioGroup rgAec;
    private CheckBox cbAecExtendFilter;
    private CheckBox cbDelayAgnostic;
    private CheckBox cbNextGenerationAEC;
    private RadioGroup rgPcMode;
    private RadioGroup rgMobileMode;
    private CheckBox cbNS;
    private CheckBox cbExperimentalNs;
    private RadioGroup rgNsMode;
    private RadioGroup rgAgcMode;
    private CheckBox cbAgc;
    private CheckBox cbExperimentalAGC;
    private CheckBox cbVad;
    private Button btnStart;
    private RadioButton rbAecPC;
    private RadioButton rbAecMobile;
    private RadioButton rbAecNone;
    private RadioButton rbLowSuppression;
    private RadioButton rbModerateSuppression;
    private RadioButton rbHighSuppression;
    private RadioButton rbQuietEarpieceOrHeadset;
    private RadioButton rbEarpiece;
    private RadioButton rbLoudEarpiece;
    private RadioButton rbSpeakerphone;
    private RadioButton rbLoudSpeakerphone;
    private EditText etAceBufferDelay;
    private RadioButton rbLow;
    private RadioButton rbModerate;
    private RadioButton rbHigh;
    private RadioButton rbVeryHigh;
    private RadioButton rbAdaptiveAnalog;
    private RadioButton rbAdaptiveDigital;
    private RadioButton rbFixedDigital;
    private TextView tvReceivedCount;
    private TextView tvSendCount;

    private ApmViewModel apmViewModel;
    private AudioProcessing audioProcessing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        registerClicks();
        try {
            apmViewModel = new ApmViewModel();
        } catch (Exception e) {
            e.printStackTrace();
        }
        etTargetIP.setText(apmViewModel.getTargetIP());
        etTargetPort.setText(apmViewModel.getTargetPort());
        etAgcTargetLevel.setText(apmViewModel.getAgcTargetLevel());
        etAgcCompressionGain.setText(apmViewModel.getAgcCompressionGain());
        etAceBufferDelay.setText(apmViewModel.getAceBufferDelay());



        AecBufferDelayView();


    }

    private void registerClicks() {
        cbHighPassFilter.setOnClickListener(this);
        cbSpeechIntelligibilityEnhance.setOnClickListener(this);
        cbSpeaker.setOnClickListener(this);
        rgAec.setOnCheckedChangeListener(this);
        cbAecExtendFilter.setOnClickListener(this);
        cbDelayAgnostic.setOnClickListener(this);
        cbNextGenerationAEC.setOnClickListener(this);
        rgPcMode.setOnCheckedChangeListener(this);
        rgMobileMode.setOnCheckedChangeListener(this);
        cbNS.setOnClickListener(this);
        cbExperimentalNs.setOnClickListener(this);
        rgNsMode.setOnCheckedChangeListener(this);
        rgAgcMode.setOnCheckedChangeListener(this);
        cbAgc.setOnClickListener(this);
        cbExperimentalAGC.setOnClickListener(this);
        cbVad.setOnClickListener(this);
        btnStart.setOnClickListener(this);
    }

    private void initViews() {
        etTargetPort = findViewById(R.id.etTargetPort);
        etTargetIP = findViewById(R.id.etTargetIP);
        etAgcTargetLevel = findViewById(R.id.etAgcTargetLevel);
        etAgcCompressionGain = findViewById(R.id.etAgcCompressionGain);
        cbHighPassFilter = findViewById(R.id.cbHighPassFilter);
        cbSpeechIntelligibilityEnhance = findViewById(R.id.cbSpeechIntelligibilityEnhance);
        cbSpeaker = findViewById(R.id.cbSpeaker);
        rgAec = findViewById(R.id.rgAec);
        cbAecExtendFilter = findViewById(R.id.cbAecExtendFilter);
        cbDelayAgnostic = findViewById(R.id.cbDelayAgnostic);
        cbNextGenerationAEC = findViewById(R.id.cbNextGenerationAEC);
        rgPcMode = findViewById(R.id.rgPcMode);
        rgMobileMode = findViewById(R.id.rgMobileMode);
        cbNS = findViewById(R.id.cbNS);
        cbExperimentalNs = findViewById(R.id.cbExperimentalNs);
        rgNsMode = findViewById(R.id.rgNsMode);
        rgAgcMode = findViewById(R.id.rgAgcMode);
        cbAgc = findViewById(R.id.cbAgc);
        cbExperimentalAGC = findViewById(R.id.cbExperimentalAGC);
        cbVad = findViewById(R.id.cbVad);
        btnStart = findViewById(R.id.btnStart);
        aecPcView = findViewById(R.id.aecPcView);
        aecBufferDelayView = findViewById(R.id.aecBufferDelayView);
        nsModeView = findViewById(R.id.nsModeView);
        agcModeView = findViewById(R.id.agcModeView);
        agcTlView = findViewById(R.id.agcTlView);
        agcCgView = findViewById(R.id.agcCgView);
        etTargetPort = findViewById(R.id.etTargetPort);
        etTargetIP = findViewById(R.id.etTargetIP);
        etAgcTargetLevel = findViewById(R.id.etAgcTargetLevel);
        etAgcCompressionGain = findViewById(R.id.etAgcCompressionGain);
        rbAecPC = findViewById(R.id.rbAecPC);
        rbAecMobile = findViewById(R.id.rbAecMobile);
        rbAecNone = findViewById(R.id.rbAecNone);
        rbLowSuppression = findViewById(R.id.rbLowSuppression);
        rbModerateSuppression = findViewById(R.id.rbModerateSuppression);
        rbHighSuppression = findViewById(R.id.rbHighSuppression);
        rbQuietEarpieceOrHeadset = findViewById(R.id.rbQuietEarpieceOrHeadset);
        rbEarpiece = findViewById(R.id.rbEarpiece);
        rbLoudEarpiece = findViewById(R.id.rbLoudEarpiece);
        rbSpeakerphone = findViewById(R.id.rbSpeakerphone);
        rbLoudSpeakerphone = findViewById(R.id.rbLoudSpeakerphone);
        etAceBufferDelay = findViewById(R.id.etAceBufferDelay);
        rbLow = findViewById(R.id.rbLow);
        rbModerate = findViewById(R.id.rbModerate);
        rbHigh = findViewById(R.id.rbHigh);
        rbVeryHigh = findViewById(R.id.rbVeryHigh);
        rbAdaptiveAnalog = findViewById(R.id.rbAdaptiveAnalog);
        rbAdaptiveDigital = findViewById(R.id.rbAdaptiveDigital);
        rbFixedDigital = findViewById(R.id.rbFixedDigital);
        tvReceivedCount = findViewById(R.id.tvReceivedCount);
        tvSendCount = findViewById(R.id.tvSendCount);


        btnStart.setText("Start");
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.cbHighPassFilter:
                cbHighPassFilter.setChecked(!apmViewModel.getHighPassFilter());
                apmViewModel.setHighPassFilter(!apmViewModel.getHighPassFilter());
                break;
            case R.id.cbSpeechIntelligibilityEnhance:
                cbSpeechIntelligibilityEnhance.setChecked(!apmViewModel.getSpeechIntelligibilityEnhance());
                apmViewModel.setSpeechIntelligibilityEnhance(!apmViewModel.getSpeechIntelligibilityEnhance());
                break;
            case R.id.cbSpeaker:
                cbSpeaker.setChecked(!apmViewModel.getSpeaker());
                apmViewModel.setSpeaker(!apmViewModel.getSpeaker());
                break;
            case R.id.cbAecExtendFilter:
                cbAecExtendFilter.setChecked(!apmViewModel.getAecExtendFilter());
                apmViewModel.setAecExtendFilter(!apmViewModel.getAecExtendFilter());
                break;
            case R.id.cbDelayAgnostic:
                cbDelayAgnostic.setChecked(!apmViewModel.getDelayAgnostic());
                apmViewModel.setDelayAgnostic(!apmViewModel.getDelayAgnostic());
                AecBufferDelayView();
                break;
            case R.id.cbNextGenerationAEC:
                cbNextGenerationAEC.setChecked(!apmViewModel.getNextGenerationAEC());
                apmViewModel.setNextGenerationAEC(!apmViewModel.getNextGenerationAEC());
                break;
            case R.id.cbNS:
                apmViewModel.setNs(!apmViewModel.getNs());

                if (apmViewModel.getNs()) {
                    cbExperimentalNs.setVisibility(View.VISIBLE);
                    nsModeView.setVisibility(View.VISIBLE);
                } else {
                    cbExperimentalNs.setChecked(false);
                    apmViewModel.setExperimentalNS(false);
                    cbExperimentalNs.setVisibility(View.GONE);
                    nsModeView.setVisibility(View.GONE);
                }
                break;
            case R.id.cbExperimentalNs:
                apmViewModel.setExperimentalNS(!apmViewModel.getExperimentalNS());
                break;
            case R.id.cbAgc:
                apmViewModel.setAgc(!apmViewModel.getAgc());
                if (apmViewModel.getAgc()) {
                    cbExperimentalAGC.setVisibility(View.VISIBLE);
                    agcModeView.setVisibility(View.VISIBLE);
                    agcTlView.setVisibility(View.VISIBLE);
                    agcCgView.setVisibility(View.VISIBLE);
                } else {
                    cbExperimentalAGC.setChecked(false);
                    apmViewModel.setExperimentalAGC(false);
                    cbExperimentalAGC.setVisibility(View.GONE);
                    agcModeView.setVisibility(View.GONE);
                    agcTlView.setVisibility(View.GONE);
                    agcCgView.setVisibility(View.GONE);
                }
                break;
            case R.id.cbVad:
                cbVad.setChecked(!apmViewModel.getVad());
                apmViewModel.setVad(!apmViewModel.getVad());
                break;


            case R.id.btnStart:
                if (btnStart.getText().toString().equalsIgnoreCase("stop")) {
                    btnStart.setText("Start");
                    etTargetPort.setEnabled(true);
                    etTargetIP.setEnabled(true);
                    etAgcTargetLevel.setEnabled(true);
                    etAgcCompressionGain.setEnabled(true);
                    cbHighPassFilter.setEnabled(true);
                    cbSpeechIntelligibilityEnhance.setEnabled(true);
                    cbSpeaker.setEnabled(true);
                    rgAec.setEnabled(true);
                    cbAecExtendFilter.setEnabled(true);
                    cbDelayAgnostic.setEnabled(true);
                    cbNextGenerationAEC.setEnabled(true);
                    rgPcMode.setEnabled(true);
                    rgMobileMode.setEnabled(true);
                    cbNS.setEnabled(true);
                    cbExperimentalNs.setEnabled(true);
                    rgNsMode.setEnabled(true);
                    cbAgc.setEnabled(true);
                    cbExperimentalAGC.setEnabled(true);
                    aecPcView.setEnabled(true);
                    aecBufferDelayView.setEnabled(true);
                    nsModeView.setEnabled(true);
                    agcModeView.setEnabled(true);
                    agcTlView.setEnabled(true);
                    etTargetPort.setEnabled(true);
                    etTargetIP.setEnabled(true);
                    etAgcTargetLevel.setEnabled(true);
                    etAgcCompressionGain.setEnabled(true);
                    rbAecPC.setEnabled(true);
                    rbAecMobile.setEnabled(true);
                    rbAecNone.setEnabled(true);
                    rbLowSuppression.setEnabled(true);
                    rbModerateSuppression.setEnabled(true);
                    rbHighSuppression.setEnabled(true);
                    rbQuietEarpieceOrHeadset.setEnabled(true);
                    rbEarpiece.setEnabled(true);
                    rbLoudEarpiece.setEnabled(true);
                    rbSpeakerphone.setEnabled(true);
                    rbLoudSpeakerphone.setEnabled(true);
                    etAceBufferDelay.setEnabled(true);
                    rbLow.setEnabled(true);
                    rbModerate.setEnabled(true);
                    rbHigh.setEnabled(true);
                    rbVeryHigh.setEnabled(true);
                    rbAdaptiveAnalog.setEnabled(true);
                    rbAdaptiveDigital.setEnabled(true);
                    rbFixedDigital.setEnabled(true);
                    audioProcessing.onDestroy();
                } else {
                    btnStart.setText("Stop");
                    apmViewModel.setStart(true);
                    cbHighPassFilter.setEnabled(false);
                    etTargetPort.setEnabled(false);
                    etTargetIP.setEnabled(false);
                    etAgcTargetLevel.setEnabled(false);
                    etAgcCompressionGain.setEnabled(false);
                    cbHighPassFilter.setEnabled(false);
                    cbSpeechIntelligibilityEnhance.setEnabled(false);
                    cbSpeaker.setEnabled(false);
                    rgAec.setEnabled(false);
                    cbAecExtendFilter.setEnabled(false);
                    cbDelayAgnostic.setEnabled(false);
                    cbNextGenerationAEC.setEnabled(false);
                    rgPcMode.setEnabled(false);
                    rgMobileMode.setEnabled(false);
                    cbNS.setEnabled(false);
                    cbExperimentalNs.setEnabled(false);
                    rgNsMode.setEnabled(false);
                    rgAgcMode.setEnabled(false);
                    cbAgc.setEnabled(false);
                    cbExperimentalAGC.setEnabled(false);
                    aecPcView.setEnabled(false);
                    aecBufferDelayView.setEnabled(false);
                    nsModeView.setEnabled(false);
                    agcModeView.setEnabled(false);
                    agcTlView.setEnabled(false);
                    agcCgView.setEnabled(false);
                    etTargetPort.setEnabled(false);
                    etTargetIP.setEnabled(false);
                    etAgcTargetLevel.setEnabled(false);
                    etAgcCompressionGain.setEnabled(false);
                    rbAecPC.setEnabled(false);
                    rbAecMobile.setEnabled(false);
                    rbAecNone.setEnabled(false);
                    rbLowSuppression.setEnabled(false);
                    rbModerateSuppression.setEnabled(false);
                    rbHighSuppression.setEnabled(false);
                    rbQuietEarpieceOrHeadset.setEnabled(false);
                    rbEarpiece.setEnabled(false);
                    rbLoudEarpiece.setEnabled(false);
                    rbSpeakerphone.setEnabled(false);
                    rbLoudSpeakerphone.setEnabled(false);
                    etAceBufferDelay.setEnabled(false);
                    rbLow.setEnabled(false);
                    rbModerate.setEnabled(false);
                    rbHigh.setEnabled(false);
                    rbVeryHigh.setEnabled(false);
                    rbAdaptiveAnalog.setEnabled(false);
                    rbAdaptiveDigital.setEnabled(false);
                    rbFixedDigital.setEnabled(false);
                    apmViewModel.setTargetIP(etTargetIP.getText().toString());
                    apmViewModel.setTargetPort(etTargetPort.getText().toString());
                    apmViewModel.setAgcTargetLevel(etAgcTargetLevel.getText().toString());
                    apmViewModel.setAgcCompressionGain(etAgcCompressionGain.getText().toString());
                    apmViewModel.setAceBufferDelay(etAceBufferDelay.getText().toString());
                    Log.d("abc",apmViewModel.toString());
                    audioProcessing = new AudioProcessing(this, apmViewModel, this);
                    audioProcessing.onSpeaker(true);
                }

                break;
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        int radioBtnID = group.getCheckedRadioButtonId();
        if (radioBtnID == R.id.rbAecPC) {
            aecPcView.setVisibility(View.VISIBLE);
            rgMobileMode.setVisibility(View.GONE);


            AecBufferDelayView();
        } else if (radioBtnID == R.id.rbAecMobile) {
            rgMobileMode.setVisibility(View.VISIBLE);
            aecBufferDelayView.setVisibility(View.VISIBLE);
            aecPcView.setVisibility(View.GONE);

            if (apmViewModel.getAecExtendFilter() || apmViewModel.getNextGenerationAEC() || !apmViewModel.getDelayAgnostic()) {
                apmViewModel.setAecExtendFilter(false);
                apmViewModel.setNextGenerationAEC(false);
                cbNextGenerationAEC.setChecked(false);
                cbAecExtendFilter.setChecked(false);
                cbDelayAgnostic.setChecked(true);
                apmViewModel.setDelayAgnostic(true);

            }
        } else if (radioBtnID == R.id.rbAecNone) {
            rgMobileMode.setVisibility(View.GONE);
            aecPcView.setVisibility(View.GONE);
            aecBufferDelayView.setVisibility(View.GONE);

            if (apmViewModel.getAecExtendFilter() || apmViewModel.getNextGenerationAEC() || !apmViewModel.getDelayAgnostic()) {
                apmViewModel.setAecExtendFilter(false);
                apmViewModel.setNextGenerationAEC(false);
                cbNextGenerationAEC.setChecked(false);
                cbAecExtendFilter.setChecked(false);
                cbDelayAgnostic.setChecked(true);
                apmViewModel.setDelayAgnostic(true);
            }
        }

        if (radioBtnID == R.id.rbLowSuppression) {
            apmViewModel.setAecPCMode0(true);
            apmViewModel.setAecPCMode1(false);
            apmViewModel.setAecPCMode2(false);
        } else if (radioBtnID == R.id.rbModerateSuppression) {
            apmViewModel.setAecPCMode0(false);
            apmViewModel.setAecPCMode1(true);
            apmViewModel.setAecPCMode2(false);
        } else if (radioBtnID == R.id.rbHighSuppression) {
            apmViewModel.setAecPCMode0(false);
            apmViewModel.setAecPCMode1(false);
            apmViewModel.setAecPCMode2(true);
        }

        if (radioBtnID == R.id.rbQuietEarpieceOrHeadset) {
            apmViewModel.setAecMobileMode0(true);
            apmViewModel.setAecMobileMode1(false);
            apmViewModel.setAecMobileMode2(false);
            apmViewModel.setAecMobileMode3(false);
            apmViewModel.setAecMobileMode4(false);
        }
        if (radioBtnID == R.id.rbEarpiece) {
            apmViewModel.setAecMobileMode0(false);
            apmViewModel.setAecMobileMode1(true);
            apmViewModel.setAecMobileMode2(false);
            apmViewModel.setAecMobileMode3(false);
            apmViewModel.setAecMobileMode4(false);
        }
        if (radioBtnID == R.id.rbLoudEarpiece) {
            apmViewModel.setAecMobileMode0(false);
            apmViewModel.setAecMobileMode1(false);
            apmViewModel.setAecMobileMode2(true);
            apmViewModel.setAecMobileMode3(false);
            apmViewModel.setAecMobileMode4(false);
        }
        if (radioBtnID == R.id.rbSpeakerphone) {
            apmViewModel.setAecMobileMode0(false);
            apmViewModel.setAecMobileMode1(false);
            apmViewModel.setAecMobileMode2(false);
            apmViewModel.setAecMobileMode3(true);
            apmViewModel.setAecMobileMode4(false);
        }

        if (radioBtnID == R.id.rbLoudSpeakerphone) {
            apmViewModel.setAecMobileMode0(false);
            apmViewModel.setAecMobileMode1(false);
            apmViewModel.setAecMobileMode2(false);
            apmViewModel.setAecMobileMode3(false);
            apmViewModel.setAecMobileMode4(true);
        }

        if (radioBtnID == R.id.rbLow) {
            apmViewModel.setNsMode0(true);
            apmViewModel.setNsMode1(false);
            apmViewModel.setNsMode2(false);
            apmViewModel.setNsMode3(false);
        } else if (radioBtnID == R.id.rbModerate) {
            apmViewModel.setNsMode0(false);
            apmViewModel.setNsMode1(true);
            apmViewModel.setNsMode2(false);
            apmViewModel.setNsMode3(false);
        } else if (radioBtnID == R.id.rbHigh) {
            apmViewModel.setNsMode0(false);
            apmViewModel.setNsMode1(false);
            apmViewModel.setNsMode2(true);
            apmViewModel.setNsMode3(false);
        } else if (radioBtnID == R.id.rbVeryHigh) {
            apmViewModel.setNsMode0(false);
            apmViewModel.setNsMode1(false);
            apmViewModel.setNsMode2(false);
            apmViewModel.setNsMode3(true);
        }

        if (radioBtnID == R.id.rbAdaptiveAnalog) {
            apmViewModel.setAgcMode0(true);
            apmViewModel.setAgcMode1(false);
            apmViewModel.setAgcMode2(false);
        } else if (radioBtnID == R.id.rbAdaptiveDigital) {
            apmViewModel.setAgcMode0(false);
            apmViewModel.setAgcMode1(true);
            apmViewModel.setAgcMode2(false);
        } else if (radioBtnID == R.id.rbFixedDigital) {
            apmViewModel.setAgcMode0(false);
            apmViewModel.setAgcMode1(false);
            apmViewModel.setAgcMode2(true);
        }


        Log.d("abcd", "aa : " + apmViewModel.getAgcMode0() + "  ad:   " + apmViewModel.getAgcMode1() + "   fd:   " + apmViewModel.getAgcMode2());

    }


    public void AecBufferDelayView() {
        if (apmViewModel.getDelayAgnostic()) {
            aecBufferDelayView.setVisibility(View.GONE);
        } else {
            aecBufferDelayView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        audioProcessing.onDestroy();

    }


    @Override
    public void sendCount(int count) {

        tvSendCount.setText(" Send Count: " + count);
    }

    @Override
    public void receivedCount(int count) {
        tvReceivedCount.setText("Receive Count:  " + count);
    }
}
