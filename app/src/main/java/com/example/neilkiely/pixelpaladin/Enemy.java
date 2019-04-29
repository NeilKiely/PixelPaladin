package com.example.neilkiely.pixelpaladin;

import android.graphics.Bitmap;
import android.graphics.Canvas;



public class Enemy {

    private Bitmap bitmap; // the actual bitmap
    private int x;      // the enemies X coordinate
    private int y;      // the enemies Y coordinate
    private Speed speed = new Speed(12,12);
    private boolean drawable = true;
    private boolean canMove = false;

    public Enemy (Bitmap bitmap, int x, int y){
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
    public void setDrawable(boolean drawable){
        this.drawable = drawable;
    }
    public void setCanMove(boolean canMove){this.canMove = canMove;}

    public void draw(Canvas canvas) {
        canvas.drawBitmap(bitmap, x - (bitmap.getWidth() / 2), y - (bitmap.getHeight() / 2), null);
    }

    public void update(int x1, int y1) {
        //Just a Test, enemies dissapear when they reach player for some reason...sometimes?
        float xDiff = (float)(x1 - x);
        float yDiff = (float)(y1 - y);
        double distance = Math.sqrt(xDiff*xDiff + yDiff*yDiff);
        double multiplier = 4 / distance;

        double velocityX = xDiff * multiplier;
        double velocityY = yDiff * multiplier;

        this.getSpeed().setXv((float) velocityX);
        this.getSpeed().setYv((float) velocityY);

        x += (speed.getXv() * speed.getxDirection());
        y += (speed.getYv() * speed.getyDirection());

    }

    public Speed getSpeed(){return speed;}

    public boolean getDrawable(){return drawable;}

    public boolean getCanMove(){return canMove;}


}
