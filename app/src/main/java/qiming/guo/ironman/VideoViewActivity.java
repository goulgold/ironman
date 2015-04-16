package qiming.guo.ironman;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle.Control;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.MediaController;
import android.widget.VideoView;

import com.thoughtworks.xstream.alias.ClassMapper;

import org.json.simple.parser.ParseException;

public class VideoViewActivity extends Activity {
    VideoView video_player_view;
    DisplayMetrics dm;
    SurfaceView sur_View;
    MediaController media_Controller;
    private final static String dir_PATH = Environment.getExternalStorageDirectory() + "/ironman/";
    public static final String TimeTAG = "qiming.guo.Service";
    private EditText DimmingInfo;
    private DimmingFileOperator thisdm;
    private String video_PATH;
    private String dimm_PATH;
    private String VideoID;
    List<String> dimming;
    HashMap<Integer, Integer> dimmingmap = new HashMap<Integer, Integer>();



    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {

        // DEBUG CODE
        VideoID = getIntent().getStringExtra("VideoID");
        video_PATH = dir_PATH + VideoID + ".mp4";
        dimm_PATH = dir_PATH + VideoID + ".txt";
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_view);

        DimmingInfo = (EditText)findViewById(R.id.editText3);

        getInit();

        InputDimmingFile.run();

        StartDimmingSevice();




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
        video_player_view.setVideoPath(video_PATH);
        video_player_view.start();
    }

    private void StartDimmingSevice() {
        LocalBroadcastManager.getInstance(VideoViewActivity.this).registerReceiver(mMessageReceiver,
                new IntentFilter("UpdateTime"));
        Intent mIntent = new Intent(VideoViewActivity.this, DimmingService.class);
        startService(mIntent);

    }

    private void Dimming(float time) {

        Integer currentPosition = video_player_view.getCurrentPosition();
        Integer Duration = video_player_view.getDuration();
        Integer currentSec = currentPosition / 1000;
        Integer dimmingValue;
        Integer searchValue = dimmingmap.get(currentSec * 1000);
        if (searchValue != null) {
            dimmingValue = searchValue;
        }
        else {
            dimmingValue = 100;
        }
        Log.d("Position",currentPosition.toString() +"|" + dimmingValue.toString());
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        float thisValue = (float) dimmingValue / 100;
        lp.screenBrightness = thisValue;
        getWindow().setAttributes(lp);
        String currentstring = Double.toString( (double)currentPosition / 1000);
        DimmingInfo.setText("time:" + currentstring + "backlight:" + dimmingValue.toString());
    }

    private BroadcastReceiver mMessageReceiver   = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            // Get extra data included in the Intent
            Integer message = intent.getIntExtra("newtime",0);
            Log.d(TimeTAG, "Got message: " + message);
            Dimming((float) message);
        }
    };

    Runnable InputDimmingFile = new Runnable() {
        @Override
        public void run() {



            try {
                thisdm = new DimmingFileOperator(new File(dimm_PATH));
                // DEBUG CODE
                //thisdm = new DimmingFileOperator(new File(dimm_PATH));
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }

            try {
                dimming = thisdm.getDimmingScheme();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }

            for (String line : dimming) {
                String[] temp = line.split("\\|");
                dimmingmap.put( Integer.parseInt(temp[0]), Integer.parseInt(temp[1]) );
            }

        }


    };
}

