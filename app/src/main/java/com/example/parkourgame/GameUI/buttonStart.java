package com.example.parkourgame.GameUI;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;

/**
 * 开始按钮
 */
public class buttonStart {
    private Bitmap start;
    private Bitmap startPressed;
    private boolean isPressed;
    private Point point;

    public buttonStart(Bitmap start, Bitmap startPressed, Point point) {
        this.start = start;
        this.startPressed = startPressed;
        this.point = point;
        this.isPressed = false;
    }

    public int getBtnWidth() {
        return start.getWidth();
    }

    public int getBtnHeight(){
        return start.getHeight();
    }

    public void drawStart(Canvas canvas, Paint paint){
        if (isPressed)
            canvas.drawBitmap(startPressed,point.x - startPressed.getWidth()/2,point.y-startPressed.getHeight()/2,paint);
        else
            canvas.drawBitmap(start,point.x-start.getWidth()/2,point.y-start.getHeight()/2,paint);

    }

    //设置按钮状态，是否被按下
    public void setPressed(boolean pressed) {
        isPressed = pressed;
    }

    public boolean getPressed(){
        return isPressed;
    }

    //获取按钮区域
    public Rect getRect(){
        return new Rect(point.x,point.y,point.x+start.getWidth(),point.y+start.getHeight());
    }

}
