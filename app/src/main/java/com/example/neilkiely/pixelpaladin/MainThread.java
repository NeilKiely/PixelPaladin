package com.example.neilkiely.pixelpaladin;


import android.graphics.Canvas;
import android.util.Log;
import android.view.SurfaceHolder;

//ACTUAL GAME LOOP
public class MainThread extends Thread {
    private static final String TAG = MainThread.class.getSimpleName();

    private SurfaceHolder surfaceHolder;
    private GamePanel gamePanel;

    public MainThread(SurfaceHolder surfaceHolder, GamePanel gamePanel) {
        super();
        this.surfaceHolder = surfaceHolder;
        this.gamePanel = gamePanel;
    }

    // flag to hold game state
    private boolean running;
    private boolean paused = false;
    private boolean victory = false;

    public void setRunning(boolean running) {
        this.running = running;
    }


    // desired fps
    private final static int 	MAX_FPS = 60;
    // maximum number of frames to be skipped
    private final static int	MAX_FRAME_SKIPS = 5;
    // the frame period
    private final static int	FRAME_PERIOD = 1000 / MAX_FPS;


    @Override
    public void run() {
        Canvas canvas;
        Log.d(TAG, "Starting game loop");

        long beginTime;		// the time when the cycle begun
        long timeDiff;		// the time it took for the cycle to execute
        int sleepTime;		// ms to sleep (<0 if we're behind)
        int framesSkipped;	// number of frames being skipped

        sleepTime = 0;

        while (running)
        {
            canvas = null;
            // try locking the canvas for exclusive pixel editing on the surface
            try
            {
                canvas = this.surfaceHolder.lockCanvas();
                synchronized (surfaceHolder)
                {
                    beginTime = System.currentTimeMillis();
                    framesSkipped = 0;
                    // update game state
                    if(!paused) this.gamePanel.update();
                    // draws the canvas on the panel
                    this.gamePanel.render(canvas);
                    timeDiff = System.currentTimeMillis() - beginTime;
                    sleepTime = (int)(FRAME_PERIOD - timeDiff);

                    if (sleepTime > 0) {
                        // if sleepTime > 0 we're OK
                        try
                        {
                            // send the thread to sleep for a short period
                            // very useful for battery saving
                            Thread.sleep(sleepTime);
                        }
                        catch (InterruptedException e) {}
                    }

                    while (sleepTime < 0 && framesSkipped < MAX_FRAME_SKIPS)
                    {
                        // need to catch up
                        // update without rendering
                        if(!paused)this.gamePanel.update();
                        // add frame period to check if in next frame
                        sleepTime += FRAME_PERIOD;
                        framesSkipped++;
                    }

                }
            }
            finally
            {
                // in case of an exception the surface is not left in
                // an inconsistent state
                if (canvas != null)
                {
                    surfaceHolder.unlockCanvasAndPost(canvas);
                }
            }
        }
    }

    public void onPause(){
        if(!paused) paused = true;
        else paused = false;
    }

    public boolean getPaused(){return paused;}

    public void setVictory(boolean victory){
        this.victory = victory;
    }

}