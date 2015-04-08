package qiming.guo.ironman;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Qiming on 4/6/15.
 */
public class DimmingFileOperator {

    private File dimmingfile;
    private String VideoID;
    private String TeamName;
    private List<String> dimmingsch = new ArrayList<String>();
    FileReader reader;
    JSONParser parser;
    JSONObject jsonObject;

    public DimmingFileOperator(File arg) throws IOException, ParseException {

        this.dimmingfile = arg;
        reader = new FileReader(dimmingfile);
        parser = new JSONParser();
        Object obj = parser.parse(reader);
        jsonObject = (JSONObject) obj;
        TeamName = (String) jsonObject.get("TeamName");

    }

    public List<String> getDimmingScheme() throws IOException, ParseException {

        JSONArray dimmingList = (JSONArray) jsonObject.get("DimmingScheme");
        Iterator<String> iterator = dimmingList.iterator();
        while (iterator.hasNext()) {
            dimmingsch.add(iterator.next());
        }
        return dimmingsch;

    }

}
