package qiming.guo.ironman;

import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.Environment;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

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


    public DimmingCal (String arg) throws IOException, ParseException {
        this.VideoID = arg;
        file_PATH = dir_PATH + VideoID + ".txt";
        dimmingfile = new File(file_PATH);

        if (!dimmingfile.exists()) {
            this.dimmingfileOp = DimmingAlgorithm(VideoID);
        }
        else {
            this.dimmingfileOp = new DimmingFileOperator(dimmingfile);
        }

    }

    private DimmingFileOperator DimmingAlgorithm (String VideoID) throws IOException, ParseException {
        //:TODO dimming algorithm
        videofile = new File(dir_PATH + VideoID + ".mp4");
        MediaMetadataRetriever thisvideo = new MediaMetadataRetriever();
        thisvideo.setDataSource(videofile.toString());
        Integer duration = Integer.parseInt(thisvideo.extractMetadata(thisvideo.METADATA_KEY_DURATION));
        duration = duration / 1000;
        JSONObject obj = new JSONObject();
        obj.put("TeamName", "Ironman");
        obj.put("VideoID", VideoID);

        JSONArray list = new JSONArray();

        for (Integer i = 0; i < duration; i++) {
            String thisline;
            Random ran = new Random();
            thisline = i.toString() + "|" + ran.nextInt(100);
            list.add(thisline);
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


}
