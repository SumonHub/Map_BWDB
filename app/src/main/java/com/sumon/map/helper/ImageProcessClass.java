package com.sumon.map.helper;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by SumOn on 1/6/2018.
 */

public class ImageProcessClass{
    private boolean check = true;
    public String ImageHttpRequest(String requestURL, HashMap<String, String> PData) {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            URL url;
            HttpURLConnection httpURLConnectionObject ;
            OutputStream OutPutStream;
            BufferedWriter bufferedWriterObject ;
            BufferedReader bufferedReaderObject ;
            int RC ;

            url = new URL(requestURL);
            httpURLConnectionObject = (HttpURLConnection) url.openConnection();
            httpURLConnectionObject.setReadTimeout(5000);
            httpURLConnectionObject.setConnectTimeout(5000);
            httpURLConnectionObject.setRequestMethod("POST");
            httpURLConnectionObject.setDoInput(true);
            httpURLConnectionObject.setDoOutput(true);
            OutPutStream = httpURLConnectionObject.getOutputStream();
            bufferedWriterObject = new BufferedWriter(
                    new OutputStreamWriter(OutPutStream, "UTF-8"));
            bufferedWriterObject.write(bufferedWriterDataFN(PData));
            bufferedWriterObject.flush();
            bufferedWriterObject.close();
            OutPutStream.close();
            RC = httpURLConnectionObject.getResponseCode();

            if (RC == HttpsURLConnection.HTTP_OK) {
                bufferedReaderObject = new BufferedReader(new InputStreamReader(httpURLConnectionObject.getInputStream()));
                stringBuilder = new StringBuilder();
                String RC2;
                while ((RC2 = bufferedReaderObject.readLine()) != null){
                    stringBuilder.append(RC2);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

    private String bufferedWriterDataFN(HashMap<String, String> HashMapParams) throws UnsupportedEncodingException {
        StringBuilder stringBuilderObject;
        stringBuilderObject = new StringBuilder();
        for (Map.Entry<String, String> KEY : HashMapParams.entrySet()) {
            if (check)
                check = false;
            else
                stringBuilderObject.append("&");
            stringBuilderObject.append(URLEncoder.encode(KEY.getKey(), "UTF-8"));
            stringBuilderObject.append("=");
            stringBuilderObject.append(URLEncoder.encode(KEY.getValue(), "UTF-8"));
        }
        return stringBuilderObject.toString();
    }

}
