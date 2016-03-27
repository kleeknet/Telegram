package org.telegram.kleegram;

import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

public class JsonParser {

    static InputStream is = null;
    static JSONObject jObj = null;
    static String json = "";


    int statusCode = 0;

    public JsonParser() {

    }

    public JSONObject makeHttpRequest(String url, String method, String jsonObj) {

        try {

            // check for request method
            if (method == "POST") {
                HttpParams httpParams = new BasicHttpParams();
                int timeoutConnection = 7000;
                int socketTimeoutConnection = 12000;
                HttpConnectionParams.setConnectionTimeout(httpParams,
                        timeoutConnection);
                HttpConnectionParams.setSoTimeout(httpParams,
                        socketTimeoutConnection);
                DefaultHttpClient httpClient = new DefaultHttpClient();
                httpClient.setParams(httpParams);


                HttpPost httpPost = new HttpPost(url);
                httpPost.setHeader("Content-Type", "application/json;charset=utf-8");
                HttpEntity entity = new StringEntity(jsonObj, "UTF8");
                httpPost.setEntity(entity);
                try {
                    HttpResponse httpResponse = httpClient.execute(httpPost);
                    HttpEntity httpEntity = httpResponse.getEntity();
                    StatusLine statusLine = httpResponse.getStatusLine();
                    statusCode = statusLine.getStatusCode();
                    is = httpEntity.getContent();

                    if (statusCode != 200) {
                        return null;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }


            }

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    is, "utf-8"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();
            json = sb.toString();
        } catch (Exception e) {
            Log.e("Buffer Error", "Error converting result " + e.toString());
        }

        // try parse the string to a JSON object
        try {
            jObj = new JSONObject(json);
        } catch (JSONException e) {
            Log.e("JSON Parser", "Error parsing data " + e.toString());
        }

        //			String string = jObj.getString("result");
        return jObj;


    }
}