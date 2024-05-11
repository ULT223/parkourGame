package com.example.parkourgame.GameUI;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;

/**
 * 玩家等级ui显示
 */
public class playerLevel {
    private Bitmap playerLevel;
    private Point point;

    public playerLevel(Bitmap bitmap,Point point){
        this.playerLevel = bitmap;
        this.point = point;
    }

    public void draw(Canvas canvas, Paint paint){
        canvas.drawBitmap(playerLevel,point.x,point.y,paint);
    }

    public Rect getRect(){
        return new Rect(point.x,point.y,point.x+playerLevel.getWidth(),point.y+playerLevel.getHeight());
    }
}
