package kjsce.stuart;

import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import cz.msebera.android.httpclient.util.ByteArrayBuffer;

public class DownloadFile extends AsyncTask<String, Void, Void> {

    protected Void doInBackground(String... strings) {
        String fileUrl = strings[0];
        String fileName = strings[1];
        File dir = new File (Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),"Stuart");
        if(!dir.mkdirs()) {
            Log.d("DOWNLOAD", "Error in creating directory");
        }
        File file = new File(dir, fileName);
        try{
            URL url = new URL(fileUrl);
            URLConnection urlConnection = url.openConnection();
            InputStream is = urlConnection.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is);
            ByteArrayBuffer baf = new ByteArrayBuffer(5000);
            int current = 0;
            while ((current = bis.read()) != -1) {
                baf.append((byte) current);
            }
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(baf.toByteArray());
            fos.flush();
            fos.close();
        } catch (IOException e){
            Log.d("DOWNLOAD", "Error in downloading file: "+e.getMessage());
        }
        return null;
    }

}
