package qiming.guo.ironman;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.jcodec.common.SeekableByteChannel;
import org.jcodec.common.model.Picture;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import org.jcodec.api.FrameGrab.MediaInfo;
import org.jcodec.api.JCodecException;
import org.jcodec.api.FrameGrab;
import org.jcodec.api.SequenceEncoder;
import org.jcodec.common.FileChannelWrapper;
import org.jcodec.common.NIOUtils;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;
import java.util.HashMap;
import java.util.Random;

import javax.microedition.khronos.opengles.GL10;
/**
 * Created by Qiming on 4/7/15.
 */
public class DimmingCal {

    private String VideoID;
    private DimmingFileOperator dimmingfileOp;
    private final static String dir_PATH = Environment.getExternalStorageDirectory() + "/ironman/";
    private String file_PATH;
    private File dimmingfile;
    private File videofile;
    Handler myhandler;
    long lasttime;
    long nowtime;
    long elapsetime;
    int width = 680;
    int height = 480;
    private Context mContext;
    private String TeamName = "ironman";



    public DimmingCal (String arg, Handler myhandler, Context context) throws IOException, ParseException, JCodecException {
        this.VideoID = arg;
        this.myhandler = myhandler;
        file_PATH = dir_PATH + VideoID + ".txt";
        dimmingfile = new File(file_PATH);
        mContext=context;


        if (!dimmingfile.exists()) {
            this.dimmingfileOp = DimmingAlgorithm(VideoID);
        }
        else {
            this.dimmingfileOp = new DimmingFileOperator(dimmingfile);
        }

    }

    private DimmingFileOperator DimmingAlgorithm (String VideoID) throws IOException, ParseException, JCodecException {
        videofile = new File(dir_PATH + VideoID + ".mp4");
//        FileInputStream inputStream = new FileInputStream(videofile);
//        fmmr.setDataSource(inputStream.getFD());
        MediaMetadataRetriever thisvideo = new MediaMetadataRetriever();
        thisvideo.setDataSource(videofile.toString());
        Long duration = Long.parseLong(thisvideo.extractMetadata(thisvideo.METADATA_KEY_DURATION));

        // unit of duration: millisecond.
        duration = duration / 1;
        JSONObject obj = new JSONObject();
        obj.put("TeamName", "Ironman");
        obj.put("VideoID", VideoID);

        JSONArray list = new JSONArray();
        String thisline;
        Bitmap thisframe;
        int[][] data;
//        Metadata thismeta = fmmr.getMetadata();

        // TODO: Dimming Algorithm
        // current time: millisecond.
        for (Long currenttime = new Long(0); currenttime < duration; currenttime = currenttime + 1000) {
            lasttime = System.nanoTime();
            thisframe = thisvideo.getFrameAtTime(currenttime * 1000, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
//            thisframe = fmmr.getFrameAtTime((currenttime + 1) * 1000, FFmpegMediaMetadataRetriever.OPTION_CLOSEST);
            timing();
            thisline = currenttime.toString() + "|" + getDimmingofFrame(thisframe).toString();
            timing();
            list.add(thisline);
            Message msgObj = myhandler.obtainMessage();
            Bundle b= new Bundle();
            b.putString("message", thisline);
            msgObj.setData(b);
            myhandler.sendMessage(msgObj);
        }

        obj.put("DimmingScheme", list);

        try {

            FileWriter file = new FileWriter(file_PATH);
            file.write(obj.toJSONString());
            file.flush();
            file.close();


        } catch (IOException e) {
            e.printStackTrace();
        }

        return new DimmingFileOperator(dimmingfile);

    }

    // TODO: get the suitable dimming value of a specific frame
    private Integer getDimmingofFrame (Bitmap frame) {
        int height = frame.getHeight();
        int width = frame.getWidth();
        int num = 0;
        float average = 0;
        int red;
        int green;
        int blue;
        int color;
        for (int i = 0; i < height; i+=300) {
            for (int j = 0; i < width; i += 300) {
                color = frame.getPixel(i,j);
                blue = color & 0xFF;
                red = color >> 8 & 0xFF;
                green = color >> 16 & 0xFF;
                average += (float) (0.2126 * red + 0.7152 * green + 0.0722 * blue);
                num++;
            }
        }
        average = average / num;
        average = average * 100 / (255 * 2) + 50;
        return (int) (average);
    }

    private void timing() {
        nowtime = System.nanoTime();
        elapsetime = nowtime - lasttime;
        lasttime = System.nanoTime();
        Log.d("Timing",String.valueOf(elapsetime / 1000000 ) + "ms" );

    }



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




}
