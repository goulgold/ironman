package qiming.guo.ironman;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

import org.json.simple.parser.ParseException;
import org.kobjects.base64.Base64;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import java.io.File;
import java.net.URL;
import java.util.List;

import qiming.guo.ironman.axet.vget.VGet;

public class PlayerActivity extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener {

    private YouTubePlayerView playerView;
    private SeekBar bar;
    private Context mContext;
    private IntentFilter receiveFilter;

    public static final String TimeTAG = "qiming.guo.Service";
    private static String TeamName="chm";   //change team name
    private String VideoName="vn1";
    private String VideoURL="vl1";
    private static String VideoLgh="vh1";
    private static String OptStepLgh="oh1";
    private static String OptDimLv="ov1";
    private EditText DimmValue;
    private EditText VideoStatus;



    private String pre_url = "http://www.youtube.com/watch?v=";
    private String url = "http://www.youtube.com/watch?v=avP5d16wEp0";
    private final static String files_PATH = Environment.getExternalStorageDirectory() + "/ironman/";

    VGet vGet;
    private DimmingCal dimmingCal;

    final Handler myhandler = new Handler();

    private void downloadYoutube (final String url, final String files_PATH) {



        Runnable downloadVideo = new Runnable() {
            @Override
            public void run() {
                try {
                    myhandler.post(new Runnable() {
                        @Override
                        public void run() {
                            VideoStatus.setText("Initializing..");
                        }
                    });

                    vGet = new VGet(new URL(url), new File(files_PATH));
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                // DEBUG CODE
                vGet.download();

                myhandler.post(new Runnable() {
                    @Override
                    public void run() {
                        VideoStatus.setText("Downloaded.");
                    }
                });

                try {
                    // DEBUG
                    dimmingCal = new DimmingCal(vGet.getVideo().getVideoID());
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                myhandler.post(new Runnable() {
                    @Override
                    public void run() {
                        VideoStatus.setText("DimmingFile Created.");
                    }
                });

/*                Message msg3 = new Message();
                msg3.what=1;
                data.putString("info", "DimmingCal Completed.");
                msg3.setData(data);
                myhandler.sendMessage(msg3);*/

            }
        };

        new Thread(downloadVideo).start();


    }


    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        VideoURL = getIntent().getStringExtra("VIDEO_ID");
        VideoName = getIntent().getStringExtra("VIDEO_TITLE");

        setContentView(R.layout.activity_player);

        playerView = (YouTubePlayerView)findViewById(R.id.player_view);
        bar = (SeekBar)findViewById(R.id.seekBar);
        playerView.initialize(YoutubeConnector.KEY, this);
        mContext=this;
        Button btn1=(Button)findViewById(R.id.upload);
        Button btn2=(Button)findViewById(R.id.download);
        Button btn3=(Button)findViewById(R.id.dimming);
        Button btn4=(Button)findViewById(R.id.openbtn);
        VideoStatus = (EditText) findViewById(R.id.editText2);


        downloadYoutube(pre_url + VideoURL,files_PATH);




        bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float max = seekBar.getMax();
                Dimming((float) (progress/max));

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                testUpload("chm114.txt");
            }
        });

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                testDownload(TeamName,VideoURL);
            }
        });
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

            }
        });
        btn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(PlayerActivity.this, VideoViewActivity.class);
                // DEBUG CODE
                intent.putExtra("VideoID", vGet.getVideo().getVideoID());
//                intent.putExtra("FILE",vGet.getTarget().toString());
                startActivity(intent);

            }
        });


    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider,
                                        YouTubeInitializationResult result) {
        Toast.makeText(this, getString(R.string.failed), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player,
                                        boolean restored) {
        if(!restored){
            player.cueVideo(getIntent().getStringExtra("VIDEO_ID"));
        }
    }



    private void testUpload(String fileName){
        try{
            FileInputStream fis = new FileInputStream(getRemoteData_WebService.files_PATH+fileName);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int count = 0;
            while((count = fis.read(buffer)) >= 0){
                baos.write(buffer, 0, count);
            }
            String uploadBuffer = new String(Base64.encode(baos.toByteArray()));  //Base64 encode
            uploadfile(uploadBuffer);
            fis.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    // Here is the testUpload function, which encode the upload stream from local scheme file.
    /* FileInputStream opens the local scheme file you generated:
     * getRemoteData_WebService.files_PATH+fileName: in the files_PATH, it is defined as the getExternalStorageDirectory(), which is the root directory of the Android smartphone; fileName is defined as "t1.txt" in this demo.
     * After open the scheme file, a buffer to be uploaded is constructed. The buffer file is defined in byte[1024] string format.
     * If your scheme file exceed the buffer size of byte[1024], please let me know.
     * To utilize the data format with the network protocal, uploadBuffer is encoded by the method Base64, which is from the ksoap2 library.
     */


    private void uploadfile(String fileBytes)
    {
        Handler myHandler = new Handler() {
            public void handleMessage(Message msg) {
                if (msg.what == 1) {
                    String resStr=msg.getData().getString("jgstr").toString();
                    new AlertDialog.Builder(mContext).setTitle("Upload Information").setMessage(resStr)
                            .setPositiveButton("OK", null).show();
                }
            }
        };
        try {
            new getRemoteData_WebService(mContext, "UploadSchemedb", new String[] {
                    "TeamName","VideoName" ,"VideoURL","VideoLgh","OptStepLgh","OptDimLv","fileBase64Datas"},
                    new String[] {TeamName,VideoName,VideoURL,VideoLgh,OptStepLgh,OptDimLv,fileBytes},
                    myHandler);
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }
    // Here is the uploadfile, which is integrated in testUpload and implement the practical SOAP upload work in the network communication.
    /* A Handler is implemented before the try to prevent ANR. This handler open another thread for getRemoteData_WebService and share the same message queue with the getRemoteData_WebService. When the getRemoteData_WebService is finished, the Handler would handle the message defined in getRemoteData_WebService and check if the web service is connected and the scheme file has been uploaded. Until then, the handler would update the UI and generate a alert dialog to notify if the upload is successful ("true") or not ("Error: cannot access Internet").
     * For more details of handler, please check: http://developer.android.com/reference/android/os/Handler.html
     * The getRemoteData_WebService method is from the getRemoteData_WebService.java constructor:
     * public getRemoteData_WebService(Context mContext, String methodname,String[] paraName,String[] paraValue,final Handler netHandler)
     * String methodname is "UploadSchemedb" is the password to the server side to call the server side function.
     * String[] paraName is "new String[] {"TeamName","VideoName" ,"VideoURL","VideoLgh","OptStepLgh","OptDimLv","fileBase64Datas"}, which is the data structure heads, and you don't need to change them.
     * String[] paraValue is defined above as private static String TeamName="tn1", so on and so forth. In your program, you need to define them by your self.
     * fileBytes is inputed by the testUpload method. It's the scheme file you generated.
     */


    private void testDownload(String TeamName1,String VideoURL1){
        Handler myHandler = new Handler() {
            public void handleMessage(Message msg) {
                if (msg.what == 1) {
                    String resStr=msg.getData().getString("jgstr").toString();
                    HashMap<String, String> resmap=tranStr(resStr);
                    String OptPath=resmap.get("OptPath");
                    new AlertDialog.Builder(mContext).setTitle("Download Information").setMessage(resStr)
                            .setPositiveButton("OK", null).show();
                    if(OptPath!=null && OptPath.length()>1) {
                        Handler myHandler2 = new Handler() {
                            public void handleMessage(Message msg) {
                                if (msg.what == 1) {
                                    String resStr=msg.getData().getString("jgstr").toString();
                                    new AlertDialog.Builder(mContext).setTitle("download file information").setMessage(resStr)
                                            .setPositiveButton("OK", null).show();
                                }
                            }
                        };
                        String url="http://"+getRemoteData_WebService.serverIP+OptPath.substring(1);
                        new getRemoteData_WebService(mContext, url, myHandler2);
                    }
                }
            }
        };
        try {
            new getRemoteData_WebService(mContext, "downloadSchemedb", new String[] {
                    "TeamName","VideoURL" }, new String[] {TeamName1,VideoURL1},
                    myHandler);

        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }
    // To download the scheme file. In the server side, the file is encoded by XML file, and this XML file is downloaded by the URL link.
    // In the present stage, the download function is indexed by team name and video URL. To make the test more convenient, each team can only search and index the scheme with their own team name, which means, each team will have their own test pool rather than sharing together.
    // In the very beginning, the code will pass "downloadSchemedb" is the password to the server side to have it index if there is scheme with specific TeamName and VideoURL. When you are making your own code, you need to make more logic functions to this part.
    // If there is the scheme, which you uploaded before, the code will enter the first Handler.
    // HashMap<String, String> resmap=tranStr(resStr): On the server side, after the XML file is pushed into the message queue by the server function, a tranStr function is composed below to decode the XML into a paraName-paraValue hash table.
    // String OptPath=resmap.get("OptPath"): the scheme file is stored in specific directory with "OptPath", And the OptPath is indexed from the hash map. Then the scheme address is composed as: String url="http://"+getRemoteData_WebService.serverIP+OptPath.substring(1);
    // Then the scheme is downloaded as:  new getRemoteData_WebService(mContext, url, myHandler2);


    private HashMap<String, String> tranStr(String ss){
        HashMap<String, String> res=new HashMap<String, String>();
        String[] sg=ss.split("><");

        for (int i = 0; i < sg.length; i++) {
            String fieldname=sg[i].substring(0, sg[i].indexOf(">"));
            if(fieldname.substring(0, 1).equalsIgnoreCase("<"))fieldname=fieldname.substring(1);
            String fieldvalue=sg[i].substring(sg[i].indexOf(">")+1, sg[i].indexOf("</"));
            if(fieldname!=null && fieldname.length()>0 && fieldvalue!=null && fieldvalue.length()>0)
            {
                res.put(fieldname, fieldvalue);
            }
        }
        return res;
    }


    private void Dimming(float dimmingValue) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        float thisValue = dimmingValue * lp.BRIGHTNESS_OVERRIDE_FULL;
        lp.screenBrightness = dimmingValue;
        getWindow().setAttributes(lp);
    }



}