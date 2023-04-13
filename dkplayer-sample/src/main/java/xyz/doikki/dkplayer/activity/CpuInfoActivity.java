package xyz.doikki.dkplayer.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import android.os.Build;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import xyz.doikki.videoplayer.util.PlayerUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * CPU信息
 */
public class CpuInfoActivity extends BaseActivity {

    private static final String TAG = "CpuInfoActivity";
    private TextView mCpuInfo;
    private Window window;
    private Display display;
    private WindowManager windowManager;

    public static void start(Context context) {
        context.startActivity(new Intent(context, CpuInfoActivity.class));
    }

    @Override
    protected View getContentView() {
        ScrollView scrollView = new ScrollView(this);
        scrollView.setPadding(PlayerUtils.dp2px(this, 20), 0, 0, 0);
        mCpuInfo = new TextView(this);
        mCpuInfo.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        mCpuInfo.setTextColor(Color.BLACK);
        scrollView.addView(mCpuInfo);
        return scrollView;
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    protected void initView() {
        super.initView();
        StringBuilder sb = new StringBuilder();
        sb.append("\n\n");
        windowManager = (WindowManager)this.getSystemService(WindowManager.class);
        display = windowManager.getDefaultDisplay();

        sb.append("===================\n");
        sb.append(Build.MANUFACTURER).append(" ").append(Build.MODEL).append("\n");
        sb.append("===================\n\n");

        sb.append("===== CPU =====\n\n");

        String str;
        try {
            FileReader fr = new FileReader("/proc/cpuinfo");
            BufferedReader br = new BufferedReader(fr);
            while ((str = br.readLine()) != null) {
                sb.append(str).append("\n");
            }
            br.close();
        } catch (IOException e) {
            //ignore
        }

        sb.append("\n");

        sb.append("===== ABI =====\n\n");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            String[] abis = Build.SUPPORTED_ABIS;
            for (int i = 0; i < abis.length; i++) {
                sb.append("CPU ABI").append(i).append(":").append(abis[i]).append("\n");
            }
        }

        sb.append("\n");
        sb.append("===== Codecs =====\n\n");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            int numCodecs = MediaCodecList.getCodecCount();
            for (int i = 0; i < numCodecs; i++) {
                MediaCodecInfo codecInfo = MediaCodecList.getCodecInfoAt(i);
                String[] types = codecInfo.getSupportedTypes();
                for (String type : types) {
                    sb.append(type).append("\n");
                    sb.append(codecInfo.getName()).append("\n\n");
                }
            }
        }

        mCpuInfo.setText(sb.toString());
        window = getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Log.d(TAG,"ymh---,setPreferMinimalPostProcessing");
            Log.d(TAG,"ymh---,isMinimalPostProcessingSupported:  "+ display.isMinimalPostProcessingSupported());
            window.setPreferMinimalPostProcessing(true);
        }
    }
}
