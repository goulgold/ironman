package qiming.guo.ironman;

import android.app.Activity;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.opengl.GLSurfaceView;
import android.opengl.GLSurfaceView.Renderer;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.widget.LinearLayout;
import android.widget.MediaController;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class GLSurfaceViewActivity extends Activity {
	
	private static final String TAG = "GLSurfaceView";
	
	private GLSurfaceView mSurfaceView;
	private MediaPlayer mMediaPlayer;
    MediaController media_Controller;
    int width = 680;
    int height=  480;


    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LinearLayout linearLayout = new LinearLayout(this);
		mSurfaceView = new GLSurfaceView(this);
		linearLayout.addView(mSurfaceView, 600, 400);
		setContentView(linearLayout);
        media_Controller = new MediaController(this);


        mSurfaceView.setRenderer(new Renderer() {

			@Override
			public void onSurfaceCreated(GL10 gl, EGLConfig config) {
				Log.e(TAG, "onSurfaceCreated");
			}

			@Override
			public void onSurfaceChanged(GL10 gl, int width, int height) {
				Log.e(TAG, "onSurfaceChanged");
			}

			@Override
			public void onDrawFrame(GL10 gl) {
				Log.e(TAG, "onDrawFrame");
			}
		});

		mSurfaceView.getHolder().addCallback(new Callback() {

			@Override
			public void surfaceDestroyed(SurfaceHolder holder) {
				mMediaPlayer.release();
			}

			@Override
			public void surfaceCreated(SurfaceHolder holder) {
				playback();
			}

			@Override
			public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	private void playback() {
		mMediaPlayer = new MediaPlayer();
		try {
			mMediaPlayer.setDataSource(Environment.getExternalStorageDirectory().getPath() + "/ironman/test.mp4");
			mMediaPlayer.setDisplay(mSurfaceView.getHolder());
			mMediaPlayer.prepare();
			mMediaPlayer.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

    private void capute_openGL(GL10 gl) {
        int screenshotSize = this.width * this.height;
        ByteBuffer bb = ByteBuffer.allocateDirect(screenshotSize * 4);
        bb.order(ByteOrder.nativeOrder());
        gl.glReadPixels(0, 0, width, height, GL10.GL_RGBA, GL10.GL_UNSIGNED_BYTE, bb);
        int pixelsBuffer[] = new int[screenshotSize];
        bb.asIntBuffer().get(pixelsBuffer);
        bb = null;

        for (int i = 0; i < screenshotSize; ++i) {
            // The alpha and green channels' positions are preserved while the red and blue are swapped
            pixelsBuffer[i] = ((pixelsBuffer[i] & 0xff00ff00)) | ((pixelsBuffer[i] & 0x000000ff) << 16) | ((pixelsBuffer[i] & 0x00ff0000) >> 16);
        }

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixelsBuffer, screenshotSize-width, -width, 0, 0, width, height);
    }

}
