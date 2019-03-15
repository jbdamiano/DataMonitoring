package com.jbdev.datamonitoring.utils;

import android.annotation.SuppressLint;
import android.widget.Toast;

import com.jbdev.datamonitoring.views.MainActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Logger {

  private static Logger instance = null;

  @SuppressLint("SdCardPath")
  private Logger() {


  }

  @SuppressLint({"ShowToast", "SimpleDateFormat"})
  public static void dump(String value) {
    if (instance == null) {
      instance = new Logger();
    }

    try {

      @SuppressLint("SimpleDateFormat") SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
      Calendar c = Calendar.getInstance();
      String formattedDate = df.format(c.getTime());
      @SuppressLint("SdCardPath") File file = new File("/sdcard/logger" + formattedDate + ".txt");

      FileOutputStream fOut = new FileOutputStream(file, true);

      OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);

      df = new SimpleDateFormat("yyyy-MM dd_HH:mm:ss");
      c = Calendar.getInstance();
      formattedDate = df.format(c.getTime());
      myOutWriter.append(formattedDate).append(": ").append(value).append("\n");
      myOutWriter.flush();
      fOut.flush();
      fOut.close();
      myOutWriter.close();
    } catch (Exception e) {
      Toast.makeText(MainActivity.getInstance(), "Error exporting", Toast.LENGTH_LONG);
    }
  }
}
