package qiming.guo.ironman;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.text.format.Time;
import android.util.Log;
import android.widget.Toast;

public class DimmingService extends Service {

    public static final String TimeTAG = "qiming.guo.Service";

    private Timer mTimer = null;
    private Time mTime = new Time();
    private Context ctx;
    private Handler mHandler = new Handler();
    public static final int INTERVAL = 1 * 200;
    private String[] dimmingArray;

    public DimmingService() {
    }

    @Override
    public void onCreate() {
        Log.d(TimeTAG, "Created");
        ctx = this;
        mTimer = new Timer();
        super.onCreate();
        startService();



    }

    public void startService() {
        mTimer.scheduleAtFixedRate(new mainTask(), 0, INTERVAL);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private class mainTask extends TimerTask {


        @Override
        public void run() {
            Log.d("sender", "Time Updated.");
            mTime.setToNow();
            Integer nowTime = mTime.second;
            sendMessage(nowTime);
        }

    }

    private void sendMessage(Integer nowTime) {
        Intent intent = new Intent("UpdateTime");
        intent.putExtra("newtime", nowTime);
        Log.d(TimeTAG,"send message:" + nowTime);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

    }
}
