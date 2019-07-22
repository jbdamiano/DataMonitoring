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

    public static void connect(String var0, String var1) throws JSONException, IOException {
        JSONObject var6 = new JSONObject();
            var6.put("email", var0);
            var6.put("password", var1);
            StringBuilder var3 = new StringBuilder();
            var3.append("https://");
            var3.append(host);
            var3.append("api/users/login");
            var0 = post(var3.toString(), var6.toString());
            JSONObject var4 = new JSONObject(var0);
            if (var4.getString("status").equals("OK")) {
                String var7 = var4.getJSONObject("user").getString("token");
                StringBuilder var5 = new StringBuilder();
                var5.append("Token ");
                var5.append(var7);
                token = var5.toString();
                Log.d("TASK", "start Service");
                MainActivity.getInstance().startServerService();
                Log.d("ServiceInterface", var0);
            } else {
                throw new IOException();
            }

    }

    public static String post(String var0, String var1) throws IOException {

            if (VERSION.SDK_INT > 8) {
                StrictMode.setThreadPolicy((new Builder()).permitAll().build());
            }

            HttpsURLConnection var3 = (HttpsURLConnection)(new URL(var0)).openConnection();
            var3.setRequestMethod("POST");
            if (!token.isEmpty()) {
                var3.setRequestProperty("Authorization", token);
            }

            var3.setRequestProperty("Content-Type", "application/json");
            var3.setRequestProperty("Accept", "*/*");
            sendData(var3, var1);
            Integer var4 = var3.getResponseCode();
            if (var4 > 199 && var4 < 300) {
                return read(var3.getInputStream());
            } else {
                Log.d("ICI", read(var3.getErrorStream()));
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

    public static void sendData(Double var0, Double var1, String var2, String var3, String var4, Integer var5, String var6, Float var7, String var8, String var9, String var10, Integer var11, Long var12) throws JSONException, IOException {
         JSONObject var15 = new JSONObject();
            var15.put("timestamp", var8);
            var15.put("reason", var9);
            var15.put("networkType", var10);
            var15.put("latitude", var0);
            var15.put("longitude", var1);
            var15.put("state", var2);
            var15.put("operator", var3);
            var15.put("imsi", var4);
            var15.put("recording", var5);
            var15.put("gps operator", var6);
            var15.put("speed", var7);
            var15.put("id", var11);
            var15.put("currentId", var12);
            StringBuilder var14 = new StringBuilder();
            var14.append("https://");
            var14.append(host);
            var14.append("api/users/addEntry");
            post(var14.toString(), var15.toString());

    }

    public static void sendData(HttpsURLConnection connection, String param1) throws IOException {

        Log.d("TASK Send", param1);
        // $FF: Couldn't be decompiled
        connection.setDoOutput(true);

        OutputStreamWriter out = new OutputStreamWriter(
                connection.getOutputStream());
        out.write(param1);
        out.close();

    }
}
