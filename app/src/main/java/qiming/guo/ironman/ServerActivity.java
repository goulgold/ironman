/*+----------------------------------------------------------------------
  ||  Class [ECE 2160 Embedded System II ]
  ||         Author:  [Dr. Yiguang Gong]
  ||                  [PhD. Candidate Xiang Chen]
  ||        Purpose:  [Server Connection Demo]
  ||    Description:  [MainActivity: we demonstrated two basic functions:
  ||                   Upload an .txt file into the course server with SOAP protocol,
  ||                   Download an file with HTTP/XML protocol into the smartphone.
  ||                   Only one activity is written in this project with two buttons.]
  ||   Notification:  [You can just compile and use it with your smartphone.
  ||                   It also works with AVD, just make sure the AVD's storage I/O permission.]
  ||        Contact:  [If you have any question, please contact xic33@pitt.edu.]
  ++-----------------------------------------------------------------------*/

package com.ece2160.server;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import org.kobjects.base64.Base64;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.util.HashMap;
// Please include ksoap library in the project. We used SOAP/XML message to encode and upload/download the scheme.
// ksoap library is located in the libs folder.
// For ksoap references, please link to http://kobjects.org/ksoap2/index.html
// For SOAP references, please check: http://en.wikipedia.org/wiki/SOAP

public class ServerActivity extends Activity {
    private Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server);
        mContext=this;
        Button btn1=(Button)findViewById(R.id.btn1);
        Button btn2=(Button)findViewById(R.id.btn2);

        btn1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                testUpload("chm114.txt");
            }
        });

        btn2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                testDownload(TeamName,VideoURL);
            }
        });
    }
    // Two buttons are listed here representing two basic functions: testUpload & testDownload.


    private static String TeamName="chm";   //change team name
    private static String VideoName="vn1";
    private static String VideoURL="vl1";
    private static String VideoLgh="vh1";
    private static String OptStepLgh="oh1";
    private static String OptDimLv="ov1";
    // Here is the data structure defined in the scheme file.
    // For details, please check the data structure and encode/decode process in the instruction file.


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
    // For the detail of Hash Map, please refer http://binarynerd.com/java-tutorials/advanced-java/using-java-hashmaps.html
    // Since the download stream is written in XML, there is a parser to eliminate the "><" separator in the XML file.

}
