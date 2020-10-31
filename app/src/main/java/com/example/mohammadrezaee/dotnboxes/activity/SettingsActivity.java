package com.example.mohammadrezaee.dotnboxes.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.example.mohammadrezaee.dotnboxes.R;
import com.example.mohammadrezaee.dotnboxes.Settings;


public class SettingsActivity extends AppCompatActivity {


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_settings);

    Switch chk_highPerformance = (Switch)findViewById(R.id.chk_highPerformance);
    Switch chk_enableMusic = (Switch)findViewById(R.id.chk_enableMusic);
    Switch chk_enableSfx = (Switch)findViewById(R.id.chk_enableSfx);

    chk_highPerformance.setChecked(Settings.isHighPerformance());
    chk_enableMusic.setChecked(Settings.isEnableMusic());
    chk_enableSfx.setChecked(Settings.isEnableSfx());


    chk_highPerformance.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        Settings.getSharedPrefrencesEditor().putBoolean("chk_highPerformance",isChecked).apply();
      }
    });

    chk_enableMusic.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        Settings.getSharedPrefrencesEditor().putBoolean("chk_enableMusic",isChecked).apply();
      }
    });

    chk_enableSfx.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        Settings.getSharedPrefrencesEditor().putBoolean("chk_enableSfx",isChecked).apply();
      }
    });
  }


}
