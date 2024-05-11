package com.example.parkourgame.GameObject;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.Log;

import java.util.Vector;

public class Ground {
    private static final String TAG = "Ground";
    private Vector<Bitmap> ground;
    private Point point;
    private int index;

    public Ground(Vector<Bitmap> ground, Point point) {
        this.ground = ground;
        this.point = point;
        index = 0;
    }

    public void draw(Canvas canvas, Paint paint){
        //Log.d(TAG,"绘制地面图片"+index+"坐标:"+this.point);
        canvas.drawBitmap(ground.get(index),this.point.x,this.point.y,paint);
    }

    public void draw(Canvas canvas, Paint paint,Point p){
        canvas.drawBitmap(ground.get(index),p.x,p.y,paint);
    }


    public void draw(Canvas canvas, Paint paint,int index){
        canvas.drawBitmap(ground.get(index),point.x,point.y,paint);
        Log.d(TAG,"绘制地面图片"+index);
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public void setPoint(Point point) {
        this.point = point;
    }

    public int getWidth(int index){
        return ground.get(index).getWidth();
    }

    public int getWidth(){
        return ground.get(index).getWidth();
    }
    public int getHeight(int index){
        return ground.get(index).getHeight();
    }

    public int getHeight(){
        return ground.get(index).getHeight();
    }

    public Point getPoint() {
        return point;
    }

    public Rect getRect(){
        return new Rect(point.x,point.y,point.x+getWidth(),point.y+getHeight());
    }


}
