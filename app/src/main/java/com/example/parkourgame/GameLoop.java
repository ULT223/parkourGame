package com.example.parkourgame;

import android.view.MotionEvent;
import android.view.SurfaceHolder;

public class GameLoop extends Thread{
    private Game game;
    private double dTime;//调用一次绘制所用的时间，用于计算帧率
    private long countTimes;//计算屏幕刷新次数
    private long start;
    private long end;



    public GameLoop(Game game) {
        this.game = game;
        countTimes = 0;
        start = 0;
        end = 0;
    }

    @Override
    public void run() {
        while (game.isRunning()){
            //更新
            game.update();
            if (countTimes%20 == 0 || countTimes == 0)
                start = System.currentTimeMillis();
            //开始绘制
            countTimes = game.drawFrame();
            if (countTimes%10 == 0 && countTimes%20 != 0)
            {
                end = System.currentTimeMillis();
                //每隔一段时间更新刷新率
                dTime = (double)(end - start);
                game.setFPS(1000/dTime*10);
            }
        }
    }



}
