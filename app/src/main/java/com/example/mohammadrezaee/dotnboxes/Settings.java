package com.example.mohammadrezaee.dotnboxes;

import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

public class Settings {

  public static SharedPreferences.Editor getSharedPrefrencesEditor(){
    return G.context.getSharedPreferences("options",MODE_PRIVATE).edit();
  }

  public static SharedPreferences getSharedPrefrences(){
    return G.context.getSharedPreferences("options",MODE_PRIVATE);
  }

  public static boolean isHighPerformance(){
    return G.getSharedPrefrences().getBoolean("chk_highPerformance",false);
  }

  public static boolean isEnableMusic(){
    return G.getSharedPrefrences().getBoolean("chk_enableMusic",false);
  }

  public static boolean isEnableSfx(){
    return G.getSharedPrefrences().getBoolean("chk_enableSfx",false);
  }

  public static int getCols(){
    return getSharedPrefrences().getInt("gridCols",4);
  }

  public static void setCols(int value){
    getSharedPrefrencesEditor().putInt("gridCols",value).apply();
  }

  public static int getRows(){
    return getSharedPrefrences().getInt("gridRows",4);
  }

  public static void setRows(int value){
    getSharedPrefrencesEditor().putInt("gridRows",value).apply();
  }
}
