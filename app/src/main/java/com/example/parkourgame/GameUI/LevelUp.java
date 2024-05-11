package com.example.parkourgame.GameUI;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;

/**
 * 升级按钮
 */
public class LevelUp {
    private Bitmap levelUp;
    private Point point;

    public LevelUp(Bitmap bitmap,Point point){
        this.levelUp = bitmap;
        this.point=point;
    }

    public void draw(Canvas canvas, Paint paint){
        canvas.drawBitmap(levelUp,point.x,point.y,paint);
    }

    public Rect getRect(){
        return new Rect(point.x,point.y,point.x+levelUp.getWidth(),point.y+levelUp.getHeight());
    }
}
