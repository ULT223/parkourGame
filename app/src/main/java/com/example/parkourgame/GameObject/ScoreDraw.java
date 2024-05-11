package com.example.parkourgame.GameObject;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;

public class ScoreDraw {
    private Bitmap[] numBitmap;
    private Bitmap score;

    public ScoreDraw(Bitmap[] numBitmap, Bitmap score) {
        this.numBitmap = numBitmap;
        this.score = score;
    }

    public void drawNums(Canvas canvas,int nums, Point point){ //传入总分数并绘制
        String numsString = String.valueOf(nums);
        int digitWidth = numBitmap[0].getWidth(); // 假设所有数字图片的宽度相同
        int startX = point.x;

        // 从右向左逐个绘制数字
        for (int i = numsString.length() - 1; i >= 0; i--) {
            int digit = Character.getNumericValue(numsString.charAt(i));
            Bitmap digitBitmap = numBitmap[digit];
            canvas.drawBitmap(digitBitmap, startX - digitWidth * (numsString.length() - i - 1), point.y, null);
        }
    }

    public void drawScore(Canvas canvas,Point point){//绘制"得分"二字
        canvas.drawBitmap(score,point.x,point.y,null);
    }

}
