package qiming.guo.ironman;

import java.util.ResourceBundle.Control;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.MediaController;
import android.widget.VideoView;

public class VideoViewActivity extends Activity {
    VideoView video_player_view;
    DisplayMetrics dm;
    SurfaceView sur_View;
    MediaController media_Controller;
    private String file_PATH;
    public static final String TimeTAG = "qiming.guo.Service";
    private EditText DimmingInfo;


    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {


        file_PATH = getIntent().getStringExtra("FILE");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_view);

        DimmingInfo = (EditText)findViewById(R.id.editText3);

        StartDimming();

        getInit();
    }

    public void getInit() {
        video_player_view = (VideoView) findViewById(R.id.video_player_view);
        media_Controller = new MediaController(this);
        dm = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(dm);
        int height = dm.heightPixels;
        int width = dm.widthPixels;
        // DEBUG CODE
//        video_player_view.setMinimumWidth(width);
//        video_player_view.setMinimumHeight(height);
        video_player_view.setMediaController(media_Controller);
        video_player_view.setVideoPath(file_PATH);
        video_player_view.start();
    }

    private void StartDimming() {
        LocalBroadcastManager.getInstance(VideoViewActivity.this).registerReceiver(mMessageReceiver,
                new IntentFilter("UpdateTime"));
        startService(new Intent(VideoViewActivity.this, DimmingService.class));
    }

    private void Dimming(float dimmingValue) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        float thisValue = dimmingValue * lp.BRIGHTNESS_OVERRIDE_FULL;
        lp.screenBrightness = dimmingValue;
        getWindow().setAttributes(lp);
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            // Get extra data included in the Intent
            Integer message = intent.getIntExtra("newtime",0);
            Log.d(TimeTAG, "Got message: " + message);
            Dimming((float) message);
            DimmingInfo.setText(message.toString());
        }
    };
}

