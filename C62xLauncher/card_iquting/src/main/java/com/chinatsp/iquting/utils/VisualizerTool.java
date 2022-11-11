package com.chinatsp.iquting.utils;

import android.media.audiofx.Visualizer;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.chinatsp.carservice.AppCarService;

import java.util.Arrays;

import launcher.base.service.AppServiceManager;

//音乐律动功能
public class VisualizerTool {
    private static final String TAG = "VisualizerTool";
    private Visualizer visualizer;
    private static final int[] FREQUENCY_BAND_LIMITS = {
            20, 63, 200, 630, 2000, 3150, 6300, 20000
    };
    public static final int MSG_FFT = 100;
    private static int[] fftResult;
    private static int mCount;

    private final Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            AppCarService carService = (AppCarService) AppServiceManager.getService(AppServiceManager.SERVICE_CAR);
            if (msg.what == MSG_FFT) {//isAlcMusicOn  律动开关  台架测试注释    isPlaying() 播放状态
                if (fftResult != null && isPlaying && carService.isAlcMusicOn()) {
                    carService.setMusicFrequencyData(fftResult);
                    mCount = 0;
                }
                //暂停时发送全0数组通知mcu
                if (!isPlaying && mCount == 0) {
                    mCount++;
                    carService.setMusicFrequencyData(new int[8]);
                }
            }
        }
    };

    private boolean isPlaying;

    public void setPlayStatus(boolean isPlay) {
        this.isPlaying = isPlay;
    }


    /*
     * 计算发送数组
     *
     * @param fft
     *
     * @return
     */
    private int[] calcLuminance(byte[] fft) {
        float max = 0;
        int currentFftPosition = 0;
        int currentFrequencyBandLimitIndex = 0;
        int[] result = new int[8];
        for (int i = 0; i < fft.length; i++) {
            int nextLimitAtPosition = Math.abs(fft[i]) * 2 +
                    (int) Math.floor(FREQUENCY_BAND_LIMITS[currentFrequencyBandLimitIndex] / 20_000.0f * fft.length);
            //Log.i(TAG, "nextLimitAtPosition =" + nextLimitAtPosition);
            // 汉明窗口修正
            int m = FREQUENCY_BAND_LIMITS.length >> 1;
            float windowed =
                    (float) (nextLimitAtPosition * (0.54f + 0.46f * Math.cos((currentFrequencyBandLimitIndex - m) * Math.PI / (m + 1))));

            //Log.i("calcLuminance", "calcLuminance =" + windowed);
            result[i] = (int) nextLimitAtPosition;
            //Log.d(TAG, "currentFftPosition =" + currentFftPosition + ",max =" + max);
            currentFrequencyBandLimitIndex++;
        }
        return result;
    }

    /*
     * 初始化律动对象
     *
     * @param audioSessionId
     */
    public VisualizerTool(int audioSessionId/*,Visualizer.OnDataCaptureListener dataCaptureListener*/) {
        Log.d(TAG,"init VisualizerTool");
        try {
            visualizer = new Visualizer(audioSessionId);
            int captureSize = Visualizer.getCaptureSizeRange()[1];
            int captureRate = Visualizer.getMaxCaptureRate() * 3 / 4;
            //Log.d(TAG, "精度: :: " + captureSize);
            //Log.d(TAG, "刷新频率: :: " + captureRate);

            visualizer.setCaptureSize(captureSize);
            Visualizer.OnDataCaptureListener dataCaptureListener = new Visualizer.OnDataCaptureListener() {
                @Override
                public void onWaveFormDataCapture(Visualizer visualizer, final byte[] waveform, int samplingRate) {
                    //byte[] results = Arrays.copyOfRange(waveform, 0, 8);
                }

                @Override
                public void onFftDataCapture(Visualizer visualizer, final byte[] fft, int samplingRate) {
                    byte[] results = Arrays.copyOfRange(fft, 0, 8);
                    fftResult = calcLuminance(results);
                    if (!mHandler.hasMessages(MSG_FFT)) {
                        mHandler.sendEmptyMessageDelayed(MSG_FFT, 1000);
                    }
                }
            };
            visualizer.setDataCaptureListener(dataCaptureListener, captureRate, true, true);
            visualizer.setScalingMode(Visualizer.SCALING_MODE_NORMALIZED);
            visualizer.setEnabled(true);
        } catch (Exception e) {
            Log.i(TAG, e.toString());
        }
    }

    /*
     * 释放律动对象
     *
     */
    public void releaseVisualizer() {
        Log.d(TAG,"releaseVisualizer");
        if (visualizer != null) {
            visualizer.release();
            visualizer = null;
        }
    }


}
