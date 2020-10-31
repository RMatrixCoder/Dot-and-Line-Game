package com.example.mohammadrezaee.dotnboxes.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.mohammadrezaee.dotnboxes.DialogOption;
import com.example.mohammadrezaee.dotnboxes.G;
import com.example.mohammadrezaee.dotnboxes.GameView;
import com.example.mohammadrezaee.dotnboxes.R;

import static com.example.mohammadrezaee.dotnboxes.R.id.gameView;


public class GameActivity extends AppCompatActivity {

  GameView gameview ;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_game);

    requestWritePermission();
    ImageButton btn_reset = (ImageButton) findViewById(R.id.img_reset);
    ImageButton btn_config = (ImageButton) findViewById(R.id.img_config);
    gameview = (GameView)findViewById(gameView);

    final boolean isMultiPlayer = getIntent().getExtras().getBoolean("isMultiPlayer");
    final boolean isMustResume = getIntent().getExtras().getBoolean("isMustResume");

    btn_reset.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        gameview.resetGame(isMultiPlayer);
      }
    });

    btn_config.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        DialogOption dialog = new DialogOption(GameActivity.this);
        dialog.show();
      }
    });

    if (isMustResume){
      gameview.loadGame();
    }else {
      gameview.resetGame(isMultiPlayer);
    }
  }

  public GameView getGameview(){
    return gameview;
  }
  private void requestWritePermission() {
    boolean hasPermission = (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
    G.hasWriteAccess = hasPermission;
    G.createDirectories();
    if (!hasPermission) {
      ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 123);
    }
  }


  @Override
  public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    switch (requestCode) {
      case 123: {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
          G.hasWriteAccess = true;
          G.createDirectories();
        } else {
          Toast.makeText(this, "Write to external storage required for loading & saving game", Toast.LENGTH_LONG).show();
        }
      }
    }
  }
  @Override
  protected void onPause() {
    super.onPause();
    gameview.saveGame();
  }
}
