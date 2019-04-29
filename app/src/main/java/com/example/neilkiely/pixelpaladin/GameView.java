package com.example.neilkiely.pixelpaladin;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;

//THIS CLASS IS USED TO JUST MAKE THE GIF OF THE TITLE SCREEN
public class GameView extends SurfaceView implements Runnable {

    // This is our thread
    Thread gameThread = null;
    // This is new. We need a SurfaceHolder
    // When we use Paint and Canvas in a thread
    // We will see it in action in the draw method soon.
    SurfaceHolder ourHolder;
    // A boolean which we will set and unset
    // when the game is running- or not.
    volatile boolean playing = true;
    // A Canvas and a Paint object
    Canvas canvas;
    Paint paint;
    // This variable tracks the game frame rate
    long fps;
    // This is used to help calculate the fps
    private long timeThisFrame;

    // Declare an object of type Bitmap
    Bitmap title;

    // Bob starts off not moving
    boolean isMoving = true;

    // He can walk at 150 pixels per second
    //float walkSpeedPerSecond = 250;

    // Starts 40 pixels from the left
    float titleXPosition = 40;

    // These next two values can be anything you like
    // As long as the ratio doesn't distort the sprite too much
    private int frameWidth = 1000;
    private int frameHeight = 1000;

    // How many frames are there on the sprite sheet?
    private int frameCount = 3;

    // Start at the first frame
    private int currentFrame = 0;

    // What time was it when we last changed frames
    private long lastFrameChangeTime = 0;

    // How long should each frame last
    private int frameLengthInMilliseconds = 100;

    // A rectangle to define an area of the
    // sprite sheet that represents 1 frame
    private Rect frameToDraw = new Rect(
            0,
            0,
            frameWidth,
            frameHeight);

    // A rect that defines an area of the screen
    // on which to draw
    RectF whereToDraw = new RectF();



    // When the we initialize (call new()) on gameView
    // This special constructor method runs
    public GameView(Context context) {
        // The next line of code asks the
        // SurfaceView class to set up our object.
        // How kind.
        super(context);



        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) getContext()).getWindowManager()
                .getDefaultDisplay()
                .getMetrics(displayMetrics);
        final int winHeight = displayMetrics.heightPixels;
        final int winWidth = displayMetrics.widthPixels;

        titleXPosition = 0;
        whereToDraw = new RectF(
                titleXPosition, 0,
                titleXPosition + winWidth,
                winHeight/2);

        // Initialize ourHolder and paint objects
        ourHolder = getHolder();
        paint = new Paint();

        // Load Bob from his .png file
        title = BitmapFactory.decodeResource(this.getResources(), R.drawable.pixelpaladinmenu);
        // Scale the bitmap to the correct size
        // We need to do this because Android automatically
        // scales bitmaps based on screen density

        title = Bitmap.createScaledBitmap(title,
                frameWidth * frameCount,
                frameHeight,
                false);

    }

    @Override
    public void run() {

        while (playing) {
            // Capture the current time in milliseconds in startFrameTime
            long startFrameTime = System.currentTimeMillis();

            // Draw the frame
            draw();

            // Calculate the fps this frame
            // We can then use the result to
            // time animations and more.
            timeThisFrame = System.currentTimeMillis() - startFrameTime;
            if (timeThisFrame >= 1) {
                fps = 1000 / timeThisFrame;
            }
        }
    }

    public void getCurrentFrame() {

        long time = System.currentTimeMillis();
        if (isMoving) {// Only animate if bob is moving
            if (time > lastFrameChangeTime + frameLengthInMilliseconds) {
                lastFrameChangeTime = time;
                currentFrame++;
                if (currentFrame >= frameCount) {
                    currentFrame = 0;
                }
            }
        }
        //update the left and right values of the source of
        //the next frame on the spritesheet
        frameToDraw.left = currentFrame * frameWidth;
        frameToDraw.right = frameToDraw.left + frameWidth;

    }

    // Draw the newly updated scene
    public void draw() {
        // Make sure our drawing surface is valid or we crash
        if (ourHolder.getSurface().isValid()) {
            // Lock the canvas ready to draw
            canvas = ourHolder.lockCanvas();
            // Draw the background color
            // canvas.drawColor(Color.argb(255, 20, 20, 20));
            // Choose the brush color for drawing
            // paint.setColor(Color.argb(255, 249, 129, 0));
            // Make the text a bit bigger
            paint.setTextSize(45);
            // Display the current fps on the screen
            //canvas.drawText("FPS:" + fps, 20, 40, paint);
            // Draw bob at bobXPosition, 200 pixels
            //canvas.drawBitmap(bitmapBob, bobXPosition, 200, paint);
            whereToDraw.set((int) titleXPosition,
                    0,
                    (int) titleXPosition + getWidth(),
                    getHeight());

            getCurrentFrame();
            canvas.drawBitmap(title,
                    frameToDraw,
                    whereToDraw, paint);

            // Draw everything to the screen
            ourHolder.unlockCanvasAndPost(canvas);
        }
    }

    // If SimpleGameEngine Activity is paused/stopped
    // shutdown our thread.
    public void pause() {
        playing = false;
        try {
            gameThread.join();
        } catch (InterruptedException e) {
            Log.e("Error:", "joining thread");
        }

    }

    // If SimpleGameEngine Activity is started theb
    // start our thread.
    public void resume() {
        playing = true;
        gameThread = new Thread(this);
        gameThread.start();
    }

}
