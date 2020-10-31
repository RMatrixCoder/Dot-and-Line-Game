package com.example.mohammadrezaee.dotnboxes.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.example.mohammadrezaee.dotnboxes.GameView;
import com.example.mohammadrezaee.dotnboxes.R;


public class MainActivity extends AppCompatActivity {

  GameView gameview ;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    Button btn_mustResume = (Button) findViewById(R.id.btn_resume);
    Button btn_singlePlayer = (Button) findViewById(R.id.btn_singlePlayer);
    Button btn_multiPlyer = (Button) findViewById(R.id.btn_multiPlyer);
    Button btn_option = (Button) findViewById(R.id.btn_option);
    Button btn_about = (Button) findViewById(R.id.btn_about);

    btn_mustResume.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        resumeGame();
      }
    });

    btn_singlePlayer.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        startGame(false);
      }
    });

    btn_multiPlyer.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        startGame(true);
      }
    });

    btn_option.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Intent intent = new Intent(MainActivity.this,SettingsActivity.class);
        MainActivity.this.startActivity(intent);
      }
    });

    btn_about.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Intent intent = new Intent(MainActivity.this,AboutActivity.class);
        MainActivity.this.startActivity(intent);
      }
    });

  }

  private void startGame(boolean isMultiPlayer){
    Intent intent = new Intent(MainActivity.this,GameActivity.class);
    intent.putExtra("isMultiPlayer",isMultiPlayer);
    MainActivity.this.startActivity(intent);
  }

  private void resumeGame(){
    Intent intent = new Intent(MainActivity.this,GameActivity.class);
    intent.putExtra("isMustResume",true);
    MainActivity.this.startActivity(intent);
  }
}
