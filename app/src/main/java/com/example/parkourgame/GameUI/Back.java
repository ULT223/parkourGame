package com.example.parkourgame.GameUI;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;

public class Back {
    private Bitmap back;
    private Point point;

    public Back(Bitmap back, Point point) {
        this.back = back;
        this.point = point;
    }

    public void draw(Canvas canvas, Paint paint){
        canvas.drawBitmap(back,point.x,point.y,paint);
    }

    public Rect getRect(){
        return new Rect(point.x,point.y,point.x+back.getWidth(),point.y+back.getHeight());
    }
}
