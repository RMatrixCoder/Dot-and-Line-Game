package com.example.mohammadrezaee.dotnboxes;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.example.mohammadrezaee.dotnboxes.activity.GameActivity;


public class DialogOption extends Dialog {

  GameActivity activity;
  public DialogOption(@NonNull GameActivity activity) {
    super(activity);
    this.activity = activity;
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    requestWindowFeature(Window.FEATURE_NO_TITLE);
    setContentView(R.layout.dialog_option);

    Button btn_submit = (Button)findViewById(R.id.btn_submit);
    final EditText edt_cols = (EditText)findViewById(R.id.edt_cols);
    final EditText edt_rows = (EditText)findViewById(R.id.edt_rows);

    btn_submit.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        int cols = Integer.parseInt(edt_cols.getText().toString());
        int rows = Integer.parseInt(edt_rows.getText().toString());

        Settings.setCols(cols);
        Settings.setRows(rows);
        activity.getGameview().resetGame();

        dismiss();
      }
    });
  }
}
