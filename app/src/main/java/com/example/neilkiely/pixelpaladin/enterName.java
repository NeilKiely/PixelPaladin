package com.example.neilkiely.pixelpaladin;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import static android.view.View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;


public class enterName extends Activity{

    String playerName;
    EditText editname;
    DataBaseHelper MyDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(uiOptions);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.enter_name);
        MyDB = new DataBaseHelper(this);
        editname = (EditText)findViewById(R.id.textfield);
    }

    public void sendMessage(View view) {
        playerName = editname.getText().toString();
        editname.setText(playerName);
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra("PLAYER_NAME", playerName);
        startActivity(intent);
    }

    public void viewAll(View view) {
        Cursor res = MyDB.getAllData();
        StringBuffer buffer = new StringBuffer();

        if(res.getCount() == 0){
            showMessage("Error", "No Data Found");
            return;
        }

        while (res.moveToNext()) {
            buffer.append("Player: " + res.getString(0) + "\n");
            buffer.append("Kills: " + res.getInt(1) + "\n\n");
        }
        //show all data
        showMessage("Scores", buffer.toString());
    }

    public void showMessage(String title, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();
    }

    public  void addData(String name, int Score){
        MyDB.insertData(name, Score);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    public void endTask(View view) {
        finish();
    }
}
