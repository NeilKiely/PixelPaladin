package com.example.neilkiely.pixelpaladin;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.MotionEvent;
import android.view.View;



public class JoyStick{

    //background/foreground.
    private Bitmap bitmapFg;
    private float x;
    private float y;
    private float ogX;
    private float ogY;
    private boolean touched;


    public JoyStick(Bitmap bitmapFg, float x, float y){
        this.bitmapFg = bitmapFg;
        this.x = x;
        this.y = y;
        this.ogX = x;
        this.ogY = y;
    }

    public float getX() {
        return x;
    }
    public void setX(int x) {
        this.x = x;
    }
    public float getY() {
        return y;
    }
    public void setY(int y) {
        this.y = y;
    }

    public float getogX() {
        return ogX;
    }

    public float getogY() {
        return ogY;
    }

    public boolean isTouched() {
        return touched;
    }

    public void setTouched(boolean touched) {
        this.touched = touched;
    }

    public void draw(Canvas canvas) {
        canvas.drawBitmap(bitmapFg, x - (bitmapFg.getWidth() / 2), y - (bitmapFg.getHeight() / 2), null);
    }

    public boolean handleActionDown(int eventX, int eventY) {
        //divided it by 2 so as to get the center image rather than top right corner
        //setting the joysticks boolean touched to true or false;
        if (eventX >= (x - bitmapFg.getWidth() / 2) && (eventX <= (x + bitmapFg.getWidth()/2)))
        {
            if (eventY >= (y - bitmapFg.getHeight() / 2) && (y <= (y + bitmapFg.getHeight() / 2)))
            {
                // droid touched
                setTouched(true);
            }
            else
            {
                setTouched(false);
            }
        }
        else
        {
            setTouched(false);
        }
        return touched;
    }

    public void checkPosition(int eventX, int eventY){
        float displacement = (float) Math.sqrt(Math.pow(eventX - ogX, 2) + Math.pow(eventY - ogY, 2));
        float ratio = 100 / displacement;
        x = ogX + (eventX - ogX) * ratio;
        y = ogY + (eventY - ogY) * ratio;
    }

    public float[] checkDirection(float eventX, float eventY){
        float[] array = new float[2];
        array[0] = eventX - ogX;
        array[1] = eventY - ogY;
        return array;
    }

    public void update() {

    }


}
