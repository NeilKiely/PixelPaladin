package com.example.neilkiely.pixelpaladin;

import android.graphics.Bitmap;
import android.graphics.Canvas;


public class Bullet {
    private Bitmap bitmap;
    private int x;
    private int y;
    private Speed speed = new Speed();
    private boolean drawable = true;

    public Bullet(Bitmap bitmap, int x, int y, float xv, float yv){
        this.bitmap = bitmap;
        this.x = x;
        this.y = y;
        this.getSpeed().setXv(xv);
        this.getSpeed().setYv(yv);
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

    public void draw(Canvas canvas){
        canvas.drawBitmap(bitmap, x - (bitmap.getWidth() / 2), y - (bitmap.getHeight() / 2), null);
    }

    public void update(){
        x += (speed.getXv()/2 * speed.getxDirection());
        y += (speed.getYv()/2 * speed.getyDirection());
    }

    public Speed getSpeed(){return  speed;}
    public boolean getDrawable(){return drawable;}
}
