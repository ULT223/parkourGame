package com.example.parkourgame.GameUI;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;

public class Restart {
    private Bitmap restart;
    private Point point;

    public Restart(Bitmap restart, Point point) {
        this.restart = restart;
        this.point = point;
    }

    public void drawStart(Canvas canvas, Paint paint){
        canvas.drawBitmap(restart,point.x - restart.getWidth()/2,point.y-restart.getHeight()/2,paint);
    }

    public Rect getRect(){
        return new Rect(point.x,point.y,point.x+restart.getWidth(),point.y+restart.getHeight());
    }
}
