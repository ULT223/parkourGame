package com.example.parkourgame.GameUI;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

/**
    游戏背景:
    两张背景
    1.游戏中
    2.游戏外界面
 */
public class GameBG {

    private Bitmap background1;
    private Bitmap background2;

    public GameBG(Bitmap bgOutOfGame,Bitmap bgInGame){
        background1 = bgOutOfGame;
        background2 = bgInGame;
    }

    /*
        改场景
     */
    public void setBackground2(Bitmap background2) {
        this.background2 = background2;
    }

    /*
        改场景1
     */
    public void setBackground1(Bitmap background1) {
        this.background1 = background1;
    }

    /*
       在游戏外时绘制背景
    */
    public void drawOutOfGame(Canvas canvas, Paint paint){
        canvas.drawBitmap(background1,0,0,paint);
    }

    /*
        游戏中时绘制背景
     */
    public void drawInGame(Canvas canvas,Paint paint){
        canvas.drawBitmap(background2,0,0,paint);
    }

}
