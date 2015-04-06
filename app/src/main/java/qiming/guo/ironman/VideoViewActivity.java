package qiming.guo.ironman;

import java.util.ResourceBundle.Control;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.SurfaceView;
import android.widget.MediaController;
import android.widget.VideoView;

public class VideoViewActivity extends Activity {
    VideoView video_player_view;
    DisplayMetrics dm;
    SurfaceView sur_View;
    MediaController media_Controller;
    private String file_PATH;


    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        file_PATH = getIntent().getStringExtra("FILE");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_view);
        getInit();
    }

    public void getInit() {
        video_player_view = (VideoView) findViewById(R.id.video_player_view);
        media_Controller = new MediaController(this);
        dm = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(dm);
        int height = dm.heightPixels;
        int width = dm.widthPixels;
        video_player_view.setMinimumWidth(width);
        video_player_view.setMinimumHeight(height);
        video_player_view.setMediaController(media_Controller);
        video_player_view.setVideoPath(file_PATH);
        video_player_view.start();
    }
}

