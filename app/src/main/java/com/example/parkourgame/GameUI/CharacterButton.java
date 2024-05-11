package com.example.parkourgame.GameUI;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;

/**
 * 角色按钮类
 */
public class CharacterButton {
    private Bitmap characterButton;
    private Point point;

    public CharacterButton(Bitmap bitmap,Point point){
        this.characterButton = bitmap;
        this.point = point;
    }

    public void draw(Canvas canvas, Paint paint){
        canvas.drawBitmap(characterButton,point.x,point.y,paint);
    }

    public Rect getRect(){
        return new Rect(point.x,point.y,point.x+characterButton.getWidth(),point.y+characterButton.getHeight());
    }
}
