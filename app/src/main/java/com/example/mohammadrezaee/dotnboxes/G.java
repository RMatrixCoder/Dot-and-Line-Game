package com.example.mohammadrezaee.dotnboxes;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.os.Handler;
import android.util.DisplayMetrics;

import java.io.File;


public class G extends Application {

  public static final String SD_CARD = Environment.getExternalStorageDirectory().getAbsolutePath();
  public static final String HOME_DIR = SD_CARD + "/Matrix";
  public static final String APP_DIR = HOME_DIR + "/dotNboxes";
  public static Context context;
  public static DisplayMetrics dp;
  public static Handler handler;
  public static boolean hasWriteAccess = false;

  @Override
  public void onCreate() {
    super.onCreate();
    context = getApplicationContext();
    dp = getResources().getDisplayMetrics();
    handler = new Handler();
    createDirectories();
  }

  public static void createDirectories(){
    if (hasWriteAccess){
      File file = new File(APP_DIR);
      file.mkdirs();
    }
  }

  public static SharedPreferences.Editor getSharedPrefrencesEditor(){
    return G.context.getSharedPreferences("options",MODE_PRIVATE).edit();
  }

  public static SharedPreferences getSharedPrefrences(){
    return G.context.getSharedPreferences("options",MODE_PRIVATE);
  }
}
