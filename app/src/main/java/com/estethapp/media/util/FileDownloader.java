package com.estethapp.media.util;

import android.util.Log;

import com.estethapp.media.web.WebURL;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by Irfan Ali on 3/21/2017.
 */

public class FileDownloader {

    public void downloadFile(String fileUrl){
        try {
            int count;
            String fileName = fileUrl;
            fileUrl = WebURL.URL+"audiofiles/"+fileName;
            URL url = new URL(fileUrl);
            URLConnection conection = url.openConnection();
            conection.connect();
            // getting file length
            int lenghtOfFile = conection.getContentLength();

            // input stream to read file - with 8k buffer
            InputStream input = new BufferedInputStream(url.openStream(), 8192);

            // Output stream to write file
            OutputStream output = new FileOutputStream(Util.getRecvAudioFilesDirectory()+"/"+fileName);

            byte data[] = new byte[1024];

            long total = 0;

            while ((count = input.read(data)) != -1) {
                // writing data to file
                output.write(data, 0, count);
            }

            // flushing output
            output.flush();

            // closing streams
            output.close();
            input.close();

        } catch (Exception e) {
            Log.e("Error: ", e.getMessage());
        }
    }
}
