package com.example.neilkiely.pixelpaladin;

import android.graphics.Bitmap;
import android.graphics.Canvas;



public class Player{

    private Bitmap bitmap; // the actual bitmap
    private int x;      // the players X coordinate
    private int y;      // the players Y coordinate
    private boolean touched;
    private Speed speed = new Speed();

    public Player(Bitmap bitmap, int x, int y) {
        this.bitmap = bitmap;
        this.x = x;
        this.y = y;
    }


    public Bitmap getBitmap() {
        return bitmap;
    }
    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }
    public int getX() {
        return x;
    }
    public void setX(int x) {
        this.x = x;
    }
    public int getY() {
        return y;
    }
    public void setY(int y) {
        this.y = y;
    }


    public boolean isTouched() {
        return touched;
    }

    public void setTouched(boolean touched) {
        this.touched = touched;
    }

    public void draw(Canvas canvas) {
        canvas.drawBitmap(bitmap, x - (bitmap.getWidth() / 2), y - (bitmap.getHeight() / 2), null);
    }

    public void handleActionDown(int eventX, int eventY) {
        //divided it by 2 so as to get the center image rather than top right corner
        //setting the players boolean touched to true or false;
        if (eventX >= (x - bitmap.getWidth() / 2) && (eventX <= (x + bitmap.getWidth()/2)))
        {
            if (eventY >= (y - bitmap.getHeight() / 2) && (y <= (y + bitmap.getHeight() / 2)))
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

    }

    public void update() {
        if (!touched) {
            x += (speed.getXv() * speed.getxDirection());
            y += (speed.getYv() * speed.getyDirection());
        }
    }

    public Speed getSpeed(){ return speed; }
}