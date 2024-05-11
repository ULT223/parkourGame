package com.example.parkourgame.GameObject;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;

/**
 * 敌人类
 */
public class Enemy {
    private Bitmap enemy;
    private Point point;

    public Enemy(Bitmap enemy, Point p){
        this.enemy = enemy;
        this.point = p;
    }

    public void draw(Canvas canvas, Paint paint) {
        canvas.drawBitmap(enemy, point.x, point.y, paint);
    }

    public Rect getRect(){
        return new Rect(point.x,point.y,point.x+enemy.getWidth(), point.y+enemy.getHeight());
    }

    public Point getPoint() {
        return point;
    }

}
