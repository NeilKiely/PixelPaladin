package com.example.neilkiely.pixelpaladin;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.View;


import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroupOverlay;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import static android.view.View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;

public class MainActivity extends AppCompatActivity{
    GameView gameView;
    View mainView;
    MediaPlayer mediaPlayer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        mainView = new View(this);
        gameView = new GameView(this);

        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(uiOptions);

        //setContentView(R.layout.activity_main);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mediaPlayer = MediaPlayer.create(this, R.raw.title);
        mediaPlayer.start();
        mediaPlayer.setLooping(true);


        //parameters which the gameview is drawn, not scale, only size
        //Fix so it scans screen, not just uses (1080,1100)
        //THIS THING WAS THE MOST ANNOYING THING EVER!
        ViewGroup.LayoutParams params = new RelativeLayout.LayoutParams(1080,1100);
        addContentView(gameView, params);
    }


    @Override
    protected void onResume() {
        super.onResume();
        mediaPlayer.start();
        // Tell the gameView resume method to execute
        gameView.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mediaPlayer.pause();
        // Tell the gameView pause method to execute
        gameView.pause();
    }



    public void sendMessage(View view) {
        Intent intent = new Intent(this, enterName.class);
        startActivity(intent);
    }

    public void endTask(View view) {
        finish();
    }
}

