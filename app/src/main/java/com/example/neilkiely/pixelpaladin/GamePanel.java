package com.example.neilkiely.pixelpaladin;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.EditText;
import android.widget.Toast;


import java.util.Random;


public class GamePanel extends SurfaceView implements
        SurfaceHolder.Callback {


    String playerName;
    DataBaseHelper MyDB;

    //All Declarations
    private MainThread thread;
    private Player player;
    private Enemy[] enemies = new Enemy[30];
    private Bullet[] bullets = new Bullet[15];
    private JoyStick leftStick;
    private JoyStick rightStick;
    private BackgroundGraphics background;

    //Keep track of spawned enemy for array, and player score
    private int enemyCount = 0;
    private int enemyKills = 0;
    private int bulletCount = 0;
    private int enemyMultiplier = 1;
    private int playerLives = 3;

    private boolean canShoot = true;
    private boolean over = false;

    //timer so bullets only file every * seconds
    private CountDownTimer bulletTimer;
    //timer so enemies spawn every * seconds, enemy number = enemy * enemyMultiplier
    private CountDownTimer enemyTimer;

    //First enemy for testing
    //private Enemy enemy1;

    //All bitmaps needed for the game
    private Bitmap playerPic    = BitmapFactory.decodeResource(getResources(), R.drawable.player);
    private Bitmap enemyPic     = BitmapFactory.decodeResource(getResources(), R.drawable.enemy);
    private Bitmap bulletPic    = BitmapFactory.decodeResource(getResources(), R.drawable.shot);
    private Bitmap joyStickPic  = BitmapFactory.decodeResource(getResources(), R.drawable.image_button);
    private Bitmap bg           = BitmapFactory.decodeResource(getResources(), R.drawable.background);
    private Bitmap ctrl         = BitmapFactory.decodeResource(getResources(), R.drawable.controllernew);

    //Sound effects
    SoundPool sounds;
    int laser;
    int explosion;
    int playerExplosion;

    public GamePanel(Context context) {
        super(context);

        //This line sets the current class (GamePanel) as the handler for the events happening on the actual surface.
        getHolder().addCallback(this);

        //fetching the text entered in the text box in the previous activity
        playerName = ((Activity) getContext()).getIntent().getStringExtra("PLAYER_NAME");
        //MyDB = new DataBaseHelper(getContext());
       // MyDB = ((Activity) getContext()).getIntent().getParcelableExtra("DATABASE_NAME");
        //getting dimensions of screen
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) getContext()).getWindowManager()
                .getDefaultDisplay()
                .getMetrics(displayMetrics);
        final int winHeight = displayMetrics.heightPixels;
        final int winWidth = displayMetrics.widthPixels;
        bg = Bitmap.createScaledBitmap(bg,winWidth, winHeight, true);
        ctrl = Bitmap.createScaledBitmap(ctrl,winWidth, winHeight, true);
        background = new BackgroundGraphics(bg, ctrl, 0, 0, winWidth, winHeight);

        //create enemy for testing
        //enemy1 = new Enemy(BitmapFactory.decodeResource(getResources(), R.drawable.enemy), 50, 50);
        player = new Player(playerPic, (winWidth/2), (winHeight/3));
        leftStick = new JoyStick(joyStickPic, (float) (winWidth*.215),(float)(winHeight*.77) );
        rightStick = new JoyStick(joyStickPic, (float) (winWidth*.795),(float)(winHeight*.77));
        thread = new MainThread(getHolder(), this);
        //spawnEnemy(winWidth, winHeight);
       enemyTimer = new CountDownTimer(2000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                if(!thread.getPaused()) {
                    spawnEnemy(winWidth, winHeight);
                }
                enemyTimer.start();
            }
        }.start();
       bulletTimer = new CountDownTimer(1000/4, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                canShoot = true;
                bulletTimer.start();
            }
        }.start();
        // makes the gamepanel focusable so that it can handle events

        //Sound Effects
        sounds = new SoundPool(10, AudioManager.STREAM_MUSIC,0);
        laser = sounds.load(context, R.raw.laser, 1);
        explosion = sounds.load(context, R.raw.explosion, 1);
        playerExplosion = sounds.load(context, R.raw.playerex, 1);

        //BackGround Music
        MediaPlayer mediaPlayer = MediaPlayer.create(context, R.raw.action);
        mediaPlayer.start();
        mediaPlayer.setLooping(true);


        setFocusable(true);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        thread.setRunning(true);
        thread.start();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder){
        boolean retry = true;
        while (retry) {
            try {
                thread.join();
                retry = false;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    //Old method used to detect inputs,
    //removed because OnTouchEvent only takes one input
    //needed imageviews for two Joysticks but had trouble putting imageViews on canvas

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //if (event != null)touchEvent(event);
        int action = event.getAction();
        float x = event.getX();
        float y = event.getY();


        if (action == event.ACTION_DOWN) {
            //Check if touching leftStick
            leftStick.handleActionDown((int) x, (int) y);
            //Check if touching rightStick
            rightStick.handleActionDown((int) x, (int) y);

            if(event.getY() > getHeight() * .9 && !over) thread.onPause();
        }
            //LEFT STICK FOR MAKING CHARACTER MOVE
            if (action == event.ACTION_MOVE && !thread.getPaused()) {
                if (leftStick.isTouched()) {
                    leftStick.checkPosition((int) x, (int) y);
                    float[] array = leftStick.checkDirection(leftStick.getX(), leftStick.getY());
                    player.getSpeed().setXv(array[0] / 14);
                    player.getSpeed().setYv(array[1] / 14);
                }
            }
            //Reset LeftStick
            if (action == event.ACTION_UP) {
                if (leftStick.isTouched()) {
                    leftStick.setTouched(false);
                    leftStick.setX((int) leftStick.getogX());
                    leftStick.setY((int) leftStick.getogY());
                }
            }
            //RIGHT STICK FOR DIRECTING BULLETS
            if (action == event.ACTION_MOVE && !thread.getPaused()) {
                if (rightStick.isTouched()) {
                    rightStick.checkPosition((int) x, (int) y);
                    float[] array = rightStick.checkDirection(rightStick.getX(), rightStick.getY());
                    if(canShoot) shootBullet(array[0], array[1]);
                }
            }
            //reset RightStick
            if (action == event.ACTION_UP) {
                if (rightStick.isTouched()) {
                    rightStick.setTouched(false);
                    rightStick.setX((int) rightStick.getogX());
                    rightStick.setY((int) rightStick.getogY());
                }
            }
        return true;
    }


    //RULE OF THUMB FOR MAKING CHARACTERS
    //1. CREATE
    //2. UPDATE
    //3. DRAW


    //triggered at every execution of the main loop inside the thread
    protected void render(Canvas canvas) {
      //  canvas.drawColor(Color.BLACK);
        background.draw(canvas);
        //rightJoyStick.draw(canvas);
        player.draw(canvas);
        leftStick.draw(canvas);
        rightStick.draw(canvas);
        //For loop to draw all of the enemies
        //enemy1.draw(canvas);
        for (int n = 0; n < enemyCount; n++) {
            if (enemies[n].getDrawable())
                enemies[n].draw(canvas);
        }
        for(int b = 0; b < bulletCount; b++){
            if(!(bullets[b].getY() > getHeight()/2) && bullets[b].getDrawable())
            bullets[b].draw(canvas);
        }

        //Update the "HUD"
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setTextSize(40);
        canvas.drawText("Enemy Kills: " + String.valueOf(enemyKills),
                (float) (getWidth() * .2), (float) (getHeight() * .55), paint);
        canvas.drawText("Lives: " + String.valueOf(playerLives),
                (float) (getWidth() * .6), (float) (getHeight() * .55), paint);


        if(playerLives == 0) {
            canvas.drawColor(Color.BLACK);
            canvas.drawText("Congratulations! " + playerName+ " You killed " + String.valueOf(enemyKills) + " enemies!",
                    (float) (getWidth() * .15), (float) (getHeight() * .3), paint);
        }
        //destroyDrawingCache();
    }

    public void update() {
        // check collision with right wall if heading right
        if (player.getX() + player.getBitmap().getWidth() / 2 >= getWidth()) {
            player.getSpeed().setXv(-1);
        }
        // check collision with left wall if heading left
        if (player.getX() - player.getBitmap().getWidth() / 2 <= 0) {
            player.getSpeed().setXv(1);
        }
        // check collision with bottom wall if heading down
        //had to change "getHeight()" to "getHeight()/2" for only half the screen
        if (player.getY() + player.getBitmap().getHeight() / 2 >= getHeight()/1.8) {
            player.getSpeed().setYv(-1);
        }
        // check collision with top wall if heading up
        if (player.getY() - player.getBitmap().getHeight() / 2 <= 0) {
            player.getSpeed().setYv(1);
        }

        //Check Collision for all enemies & bullets
        for (int f = 0; f < enemyCount; f++) {
            if (bulletCount != 0) {
                for (int g = 0; g < bulletCount; g++) {
                    if (enemies[f].getX() - enemies[f].getBitmap().getWidth()/2 < bullets[g].getX()
                     && enemies[f].getX() + enemies[f].getBitmap().getWidth()/2 > bullets[g].getX()
                     && enemies[f].getY() - enemies[f].getBitmap().getHeight()/2 < bullets[g].getY()
                     && enemies[f].getY() + enemies[f].getBitmap().getHeight()/2 > bullets[g].getY()
                     && enemies[f].getDrawable() && bullets[g].getDrawable()) {
                        killEnemy(enemies[f], bullets[g]);
                    }
                }
            }
        }

        for(int y = 0; y < enemyCount; y++){
            if (enemies[y].getX() - enemies[y].getBitmap().getWidth()/2 < player.getX()
                    && enemies[y].getX() + enemies[y].getBitmap().getWidth()/2 > player.getX()
                    && enemies[y].getY() - enemies[y].getBitmap().getHeight()/2 < player.getY()
                    && enemies[y].getY() + enemies[y].getBitmap().getHeight()/2 > player.getY()
                    && enemies[y].getDrawable()){
                killPlayer();
            }
        }

        //loop to count through enemies and update them
        //enemy1.update(player.getX(), player.getY());
        for(int n = 0; n < enemyCount; n++){
            enemies[n].update(player.getX(), player.getY());
        }

        for(int b = 0; b < bulletCount; b++){
            bullets[b].update();
        }
        //Update player after enemies
        player.update();
    }

    public void spawnEnemy(int winWidth, int winHeight) {
        Random rand = new Random();
        int x;
        int y;
        int wall = 0;
        int count = enemyCount;
        for (int n = enemyCount; n < count + enemyMultiplier; n++) {
            wall = rand.nextInt(4);
            if          (wall == 0){
                x = 0;
                y = rand.nextInt((int) (winHeight / 1.8));
            }
            else if     (wall == 1){
                x = winWidth;
                y = rand.nextInt((int) (winHeight / 1.8));
            }
            else if     (wall == 2){
                y = 0;
                x = rand.nextInt(winWidth);
            }
            else                   {
                y = (int) (winHeight / 1.8);
                x = rand.nextInt(winWidth);
            }
            enemies[n] = new Enemy(enemyPic, x, y);
            enemyCount++;
        }
        if(enemyCount % 10 == 0) enemyMultiplier++;
        if (enemyCount > 29) enemyCount = 0;
    }

    //Need to use this method to kill the enemy and the bullet that hit it
    public void killEnemy(Enemy enemy, Bullet bullet){
        sounds.play(explosion, 1.0f, 1.0f, 0, 0, 1.5f);
        enemyKills++;
        enemy.setDrawable(false);
        bullet.setDrawable(false);
    }

    public void shootBullet(float xv, float yv){
        sounds.play(laser, 1.0f, 1.0f, 0, 0, 1.5f);
        bullets[bulletCount] = new Bullet(bulletPic, player.getX(), player.getY(), xv, yv);
        bulletCount++;
        if (bulletCount > 14) bulletCount = 1;
        canShoot = false;
    }

    public void killPlayer(){
        sounds.play(playerExplosion, 1.0f, 1.0f, 0, 0, 1.5f);
        playerLives--;
        if(playerLives > 0) reset();
        //put game over here
        else {
            thread.onPause();
            thread.setVictory(true);
            over = true;
           // MyDB.insertData(playerName, enemyKills);
        }
    }

    public void reset(){
        player.setX( (getWidth()/2));
        player.setY( (getHeight()/3));
        player.getSpeed().setXv(0);
        player.getSpeed().setYv(0);
        for(int i = 0; i < enemyCount; i++)
            enemies[i].setDrawable(false);
        enemyMultiplier = 1;
        canShoot = true;
    }

}