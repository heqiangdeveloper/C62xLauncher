package com.chinatsp.vehicle.settings;

import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.rightware.kanzi.KanziNativeLibrary;
import com.rightware.kanzi.KanziView;
import com.rightware.kanzi.androiddatasource.AndroidDataSourceManager;
import com.rightware.kanzi.androiddatasource.AndroidNotifyListener;
import com.rightware.kanzi.androiddatasource.AssetCopyer;
import com.rightware.kanzi.androiddatasource.DataSourceKanziController;
import com.rightware.kanzi.androiddatasource.SharedData;
import com.rightware.kanzi.androiddatasource.kzDataTypeBool;
import com.rightware.kanzi.androiddatasource.kzDataTypeInt;
import com.rightware.kanzi.androiddatasource.kzDataTypeReal;
import com.rightware.kanzi.androiddatasource.kzDataTypeString;


public class KanziActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private KanziView mKanziView = null;
    private boolean isKanziInitFinish = false;
    private AndroidDataSourceManager mDataSourceManager;
    private String mApkFolderPath;
    // public kzDataTypeString CarState = new kzDataTypeString("Car.State");
    public kzDataTypeInt CarSpeed = new kzDataTypeInt("Car.Speed");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        //init kanzi
        initKanziSurfaceView();

        //init View
        initView();
    }

    private void initKanziSurfaceView() {
        //init KanziView
        mKanziView = findViewById(R.id.kanzicontent);
        mKanziView.registerLifecycle(getLifecycle());

        // Force the screen to stay on when this app is on front (no need to clear).
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        //load AndroidDataSource.jar
        initAndroidDataSource();
    }

    private void initAndroidDataSource() {
        mDataSourceManager = new AndroidDataSourceManager("AndroidDataSourceManager");
        SharedData.get().addManager(mDataSourceManager);
        mApkFolderPath = KanziActivity.this.getExternalFilesDir(null).getAbsolutePath();
        mDataSourceManager.setXMLPath(mApkFolderPath + "/");
        //每个项目中的(*.xml)文件都各不相同，在加载时请根据main目录下——assets目录下的(.xml)文件为标准
        AssetCopyer.copyAssetsToDst(KanziActivity.this, "datacontract.xml", mApkFolderPath + "/datacontract.xml");

        //添加Listener来监听kanzi端的数据变化
        mDataSourceManager.addAndroidNotifyListener(kanziNotifyListener);
    }

    static {
        //是为了解决AndroidDataSource 时序问题
        System.loadLibrary("kanzi");
    }

    /**
     * 接收kanzi端传来的数据。具体的参数需要Android工程师与kanzi工程师通过接口文档定义。
     *
     * @param name key
     * @param type 数据类型
     * @param value value
     */
    private AndroidNotifyListener kanziNotifyListener = new AndroidNotifyListener() {
        @Override
        public void notifyDataChanged(String name, int type, String value) {
            super.notifyDataChanged(name, type, value);
            Log.d(TAG, "notifyDataChanged() name:" + name + " type:" + type + " value:" + value);
            if (name.equals("KanziInitFinish")) {
                if (Integer.parseInt(value) == 1) {
                    Log.d(TAG, "KanziInitFinish" + value);
                    isKanziInitFinish = true;
                }
            }
        }
    };

    /**
     * Android control kanzi by (int)value.
     *
     * @param obj   obj
     * @param value value
     */
    public void sendKzControlDataInt(kzDataTypeInt obj, int value) {
        KanziNativeLibrary.submitTask((Runnable) () -> {
            if (mDataSourceManager != null && isKanziInitFinish) {
                DataSourceKanziController controller = mDataSourceManager.getKanziController();
                controller.setDataObjectValue(obj, value);
            }
        });
    }

    /**
     * Android control kanzi by (boolean)value.
     *
     * @param obj   obj
     * @param value value
     */
    public void sendKzControlDataBool(kzDataTypeBool obj, boolean value) {
        KanziNativeLibrary.submitTask((Runnable) () -> {
            if (mDataSourceManager != null && isKanziInitFinish) {
                DataSourceKanziController controller = mDataSourceManager.getKanziController();
                controller.setDataObjectValue(obj, value);
                Log.d(TAG, "sendKzControlDataBool: success!");
            }
        });
    }

    /**
     * Android control kanzi by (String)value.
     *
     * @param obj   obj
     * @param value value
     */
    public void sendKzControlDataString(kzDataTypeString obj, String value) {
        KanziNativeLibrary.submitTask((Runnable) () -> {
            if (mDataSourceManager != null && isKanziInitFinish) {
                DataSourceKanziController controller = mDataSourceManager.getKanziController();
                controller.setDataObjectValue(obj, value);
                Log.d(TAG, "sendKzControlDataString: success!");
            }
        });
    }

    /**
     * Android control kanzi by (double)value.
     *
     * @param obj   obj
     * @param value value
     */
    public void sendKzControlDataReal(kzDataTypeReal obj, double value) {
        KanziNativeLibrary.submitTask((Runnable) () -> {
            if (mDataSourceManager != null && isKanziInitFinish) {
                DataSourceKanziController controller = mDataSourceManager.getKanziController();
                controller.setDataObjectValue(obj, value);
                Log.d(TAG, "sendKzControlDataReal: success!");
            }
        });
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mKanziView.setOrientation(newConfig.orientation);
    }

    private void initView() {
        findViewById(R.id.setSpeed).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendKzControlDataInt(CarSpeed, 50);
            }
        });
    }

}
