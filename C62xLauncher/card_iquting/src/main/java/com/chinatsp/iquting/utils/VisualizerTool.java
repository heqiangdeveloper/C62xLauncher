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
    public static final int MSG_FFT = 100;
    private static int[] fftResult = new int[8];
    private static int mCount;
    private static int frequencyCount;
    private static int samplerateRange;
    private static float[] frequenceIndex = new float[8];
    private static final float[] PRE_DEFHZ = {
            40f, 70f, 100f, 200f, 600f, 1000f, 1400f, 1800f
    };

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
     * 初始化律动对象
     *
     * @param audioSessionId
     */
    public VisualizerTool(int audioSessionId/*,Visualizer.OnDataCaptureListener dataCaptureListener*/) {
        Log.d(TAG, "init VisualizerTool");
        try {
            visualizer = new Visualizer(audioSessionId);
            int captureSize = Visualizer.getCaptureSizeRange()[1];
            int captureRate = Visualizer.getMaxCaptureRate() * 3 / 4;
            //byte[] fftdata = new byte[captureSize];
            samplerateRange = visualizer.getSamplingRate() / 2000;
            frequencyCount = visualizer.getCaptureSize() / 2;

            visualizer.setCaptureSize(captureSize);
            Visualizer.OnDataCaptureListener dataCaptureListener = new Visualizer.OnDataCaptureListener() {
                @Override
                public void onWaveFormDataCapture(Visualizer visualizer, final byte[] waveform, int samplingRate) {
                    //byte[] results = Arrays.copyOfRange(waveform, 0, 8);
                }

                @Override
                public void onFftDataCapture(Visualizer visualizer, final byte[] fft, int samplingRate) {
                    if (!mHandler.hasMessages(MSG_FFT)) {
                        byte[] fftdata = Arrays.copyOfRange(fft, 0, captureSize);
                        for (int i = 0; i < frequenceIndex.length; i++) {//512
                            if (samplerateRange != 0) {
                                frequenceIndex[i] = (PRE_DEFHZ[i] / samplerateRange) * frequencyCount;
                            }
                        }
                        for (int k = 0; k < PRE_DEFHZ.length; k++) {
                            int index = (int) frequenceIndex[k] * 2;
                            byte rfv = fftdata[index];
                            byte ifv = fftdata[index + 1];
                            fftResult[k] = (int) (Math.hypot(rfv, ifv) /*/ MAX_NN*/);
                        }
                        mHandler.sendEmptyMessageDelayed(MSG_FFT, 100);
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
        Log.d(TAG, "releaseVisualizer");
        if (visualizer != null) {
            visualizer.release();
            visualizer = null;
        }
    }


}
