//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.jbdev.datamonitoring.utils;

import android.os.StrictMode;
import android.os.Build.VERSION;
import android.os.StrictMode.ThreadPolicy.Builder;
import android.util.Log;

import com.jbdev.datamonitoring.views.MainActivity;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;
import org.json.JSONException;
import org.json.JSONObject;

public class ServerInterface {
    public static String host = "jbblog.fam-damiano.org:8080/";
    public static final long serialVersionUID = 8523480836461399232L;
    public static String token = "";

    public ServerInterface() {
        super();
    }



    public static void closeQuietly(Closeable var0) {

        if (var0 != null) {
            try {
                var0.close();
            } catch (IOException var2) {
            }
        }

    }

    public static void connect(String email, String passwd) throws JSONException, IOException {
        JSONObject json = new JSONObject();
        json.put("email", email);
        json.put("password", passwd);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("https://");
        stringBuilder.append(host);
        stringBuilder.append("api/users/login");
        email = post(stringBuilder.toString(), json.toString());
        JSONObject emailJson = new JSONObject(email);
        if (emailJson.getString("status").equals("OK")) {
            String token = emailJson.getJSONObject("user").getString("token");
            StringBuilder builder = new StringBuilder();
            builder.append("Token ");
            builder.append(token);
            token = builder.toString();
            MainActivity.getInstance().startServerService();
        } else {
            throw new IOException();
        }

    }

    public static String post(String url, String data) throws IOException {

            if (VERSION.SDK_INT > 8) {
                StrictMode.setThreadPolicy((new Builder()).permitAll().build());
            }
            HttpsURLConnection httpsURLConnection = (HttpsURLConnection)(new URL(url)).openConnection();
            httpsURLConnection.setRequestMethod("POST");
            if (!token.isEmpty()) {
                httpsURLConnection.setRequestProperty("Authorization", token);
            }

            httpsURLConnection.setRequestProperty("Content-Type", "application/json");
            httpsURLConnection.setRequestProperty("Accept", "*/*");
            sendData(httpsURLConnection, data);
            Integer responseCode = httpsURLConnection.getResponseCode();
            if (responseCode > 199 && responseCode < 300) {
                return read(httpsURLConnection.getInputStream());
            } else {
                Log.d("ICI", read(httpsURLConnection.getErrorStream()));
                throw new IOException();
            }

    }

    private static String read(InputStream param0) throws IOException {
        BufferedReader br =
                new BufferedReader(new InputStreamReader(param0));

        String input = "";
        String tmp;

        while ((tmp = br.readLine()) != null){
            input += tmp;
        }

        br.close();

        Log.d("TASK recv", input);
        return input;

    }

    public static void sendData(Double latitude, Double longitude, String state, String operator,
                                String imsi, Integer recording, String gps, Float speed,
                                String timestamp, String reason, String networkType, Integer id,
                                Long currentId) throws JSONException, IOException {
        JSONObject json = new JSONObject();
        json.put("timestamp", timestamp);
        json.put("reason", reason);
        json.put("networkType", networkType);
        json.put("latitude", latitude);
        json.put("longitude", longitude);
        json.put("state", state);
        json.put("operator", operator);
        json.put("imsi", imsi);
        json.put("recording", recording);
        json.put("gps operator", gps);
        json.put("speed", speed);
        json.put("id", id);
        json.put("currentId", currentId);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("https://");
        stringBuilder.append(host);
        stringBuilder.append("api/users/addEntry");
        post(stringBuilder.toString(), json.toString());

    }

    public static void sendData(HttpsURLConnection connection, String data) throws IOException {
        // $FF: Couldn't be decompiled
        connection.setDoOutput(true);

        OutputStreamWriter out = new OutputStreamWriter(
                connection.getOutputStream());
        out.write(data);
        out.close();

    }
}
