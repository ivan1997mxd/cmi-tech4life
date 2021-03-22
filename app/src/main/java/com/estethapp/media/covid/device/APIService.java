package com.estethapp.media.covid.device;


import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;

import javax.net.ssl.HttpsURLConnection;

public class APIService
{
    private static final String CreateCaseEndpoint = "createCaseVital";

    public static void Save(APIData data, Callback<String> callback)
    {
        try
        {
            Send(EncodeQuery(GetEndpoint(CreateCaseEndpoint), data.GetData()), callback);
        }
        catch (MalformedURLException e)
        {
            e.printStackTrace();
        }
    }

    private static void Send(URL url, Callback<String> callback)
    {
        new Thread(new SendTask(url, callback)).start();
    }

    private static String GetEndpoint(String endpoint)
    {
        return "https://mdconsults.org/MDCAPI/public/" + endpoint;
    }

    private static URL EncodeQuery(String url, HashMap<String, String> components) throws MalformedURLException
    {
        ArrayList<String> parameters = new ArrayList<>();

        for (String key : components.keySet())
        {
            try
            {
                parameters.add(key + "=" + URLEncoder.encode(components.get(key),"UTF-8"));
            }
            catch (UnsupportedEncodingException e)
            {
                e.printStackTrace();
            }
        }

        return new URL(url + "?" + String.join("&", parameters));
    }

    private static class SendTask implements Runnable
    {
        private URL url;
        private String data;
        private Callback<String> callback;
        private String response = "";

        public SendTask(URL url, Callback<String> callback)
        {
            this.url = url;
            this.data = "";
            this.callback = callback;
        }

        public SendTask(URL url, String data, Callback<String> callback)
        {
            this.url = url;
            this.data = data;
            this.callback = callback;
        }

        @Override
        public void run()
        {
            try
            {
                Log.i("APIService","send to server");
                HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                conn.setReadTimeout(15000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                if (!data.isEmpty())
                {
                    Log.i("APIService","Write into Buffer");
                    OutputStream os = conn.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, StandardCharsets.UTF_8));
                    writer.write(data);
                    writer.flush();
                    writer.close();
                    os.close();
                }
                int responseCode = conn.getResponseCode();

                if (responseCode == HttpsURLConnection.HTTP_OK) {
                    String line;
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    while ((line = br.readLine()) != null) {
                        response += line;
                    }
                } else {
                    response = "";
                }
                Log.i("Response", response);
                callback.run(response);
//                callback.run(conn.getResponseCode());

            }
            catch (Exception ex)
            {
                callback.run("");
//                callback.run(-1);
            }
        }
    }
}