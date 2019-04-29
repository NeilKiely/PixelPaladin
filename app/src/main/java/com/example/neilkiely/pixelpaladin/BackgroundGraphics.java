package com.example.neilkiely.pixelpaladin;


import android.graphics.Bitmap;
import android.graphics.Canvas;

public class BackgroundGraphics {

    private Bitmap bitmap1; // the background
    private Bitmap bitmap2; // the controller
    private int x;      // the players X coordinate
    private int y;      // the players Y coordinate
    private int winWidth = 0;
    private int winHeight = 0;

    public BackgroundGraphics(Bitmap bitmap1, Bitmap bitmap2, int x, int y, int winWidth, int winHeight){
        this.bitmap1 = bitmap1;
        this.bitmap2 = bitmap2;
        this.x = x;
        this.y = y;
        this.winWidth = winWidth;
        this.winHeight = winHeight;
    }

    public void draw(Canvas canvas) {
        canvas.drawBitmap(bitmap1, x, (float)(y - (bitmap1.getHeight() / 2.3)), null);
        canvas.drawBitmap(bitmap2, x, y , null );
    }

}
