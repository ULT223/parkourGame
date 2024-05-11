package com.example.parkourgame.GameObject;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;

import java.util.Vector;

public class Coin {
    private Vector<Bitmap> coin;
    private Point point;
    private int frame;

    public Coin(Vector<Bitmap> coin,Point point){
        this.coin = coin;
        this.point = point;
        frame = 0;
    }

    public void framePlus(){
        frame++;
        if (frame >= coin.size()){
            frame = 0;
        }
    }

    public Rect getRect(){
        return new Rect(point.x,point.y,point.x+coin.get(frame).getWidth(),point.y+coin.get(frame).getHeight());
    }

    public void drawCoin(Canvas canvas, Paint paint){
        canvas.drawBitmap(coin.get(frame),point.x,point.y,paint);
    }

    public Point getPoint() {
        return point;
    }

    public void setPoint(Point point) {
        this.point = point;
    }
}
