package com.example.parkourgame.GameObject;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;

import com.example.parkourgame.GameProperty;

import java.util.Vector;

/**
 * 玩家类
 */
public class Player {

    private Vector<Bitmap> player;//玩家动作帧图片集合
    private Vector<Bitmap> playerRun;//玩家跑步图片帧集合
    private Vector<Bitmap> playerSlide;//玩家下滑图片帧
    private int slideStartTime; // 记录下滑状态开始的帧数
    private int state;//玩家状态
    public static final int PLAYER_STATE_RUN = 1;//玩家状态 跑步
    public static final int PLAYER_STATE_SLIDE = 2;//玩家状态 下滑
    public static final int PLAYER_STATE_JUMP = 3;//玩家状态,跳跃
    private Point point;
    private Point startPoint;
    private int frame;//图片索引，绘制第几帧图片
    private int level;//等级
    private int levelUpCost;//升级花销
    private float HP; //目前血量
    private float maxHP;//最大血量
    private float HPlen;//绘制血条的长度
    private int acceleration;//加速度 向下为正，向上为负
    private int speed;//速度 向下为正，向上为负

    public Player(Point p,Vector<Bitmap> playerRun,Vector<Bitmap> playerSlide) {
        this.playerRun = playerRun;
        this.playerSlide = playerSlide;
        player = playerRun;
        point = p;
        startPoint = new Point(p.x,p.y);
        state = PLAYER_STATE_RUN;
        slideStartTime = 0;
        frame = 0;
        acceleration = 0;
        speed = 0;
        level = 0;
        levelUpCost = (level+1)*60;
        maxHP = 10 + level * 2;
        HP = 10 + level * 2;//初始生命值设置为10
        HPlen = 200;//血条长度
    }


    /**
     * 绘制玩家
     * @param canvas
     * @param paint
     */
    public void draw(Canvas canvas, Paint paint){
        if (state == PLAYER_STATE_RUN || state == PLAYER_STATE_JUMP){
            if (player != playerRun){
                frame = 0;
                player = playerRun;
            }
            canvas.drawBitmap(player.get(frame),point.x,point.y,paint);//绘制第frame帧玩家动作
        }else if (state == PLAYER_STATE_SLIDE){
            if (player != playerSlide){
                frame = 0;
                player = playerSlide;
            }
            canvas.drawBitmap(player.get(frame),point.x,point.y+80,paint);//绘制第frame帧玩家动作
        }
    }

    /**
     * 玩家血条绘制
     */
    public void drawHealthBar(Canvas canvas){
        Paint paint = new Paint();
        // 绘制血条的背景
        RectF backgroundRect = new RectF(point.x, point.y - 50, point.x + HPlen,  point.y );
        paint.setColor(Color.GRAY);
        canvas.drawRect(backgroundRect, paint);

        // 绘制血条的填充
        RectF fillRect = new RectF(point.x,point.y - 50, point.x + HP* HPlen/maxHP , point.y );
        paint.setColor(Color.RED);
        canvas.drawRect(fillRect, paint);
    }

    /**
     * 玩家图片帧索引增加的方法
     */
    public void framePlus(){
        if (state == PLAYER_STATE_RUN ){
            frame += 1;
            if (frame >= player.size())
                frame = 0;
        } else if (state == PLAYER_STATE_SLIDE) {
            if (frame < player.size() - 1){
                frame += 1;
            }
        }
    }

    /**
     * 设置玩家状态
     * @return
     */
    public void setState(int state) {
        this.state = state;
    }

    public Rect getRect(){
        return new Rect(point.x,point.y,point.x+player.get(frame).getWidth(),point.y+player.get(frame).getHeight());
    }

    public void setPoint(Point point) {
        this.point = point;
    }

    public void setStartPoint(Point startPoint) {
        this.startPoint = startPoint;
    }

    public void physical(){ //玩家y轴物理逻辑
        point.y += speed;//位移等于速度乘时间，每隔一秒调用一次即可
        speed += acceleration;
    }


    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public void setAcceleration(int acceleration) {
        this.acceleration = acceleration;
    }

    public int getSpeed() {
        return speed;
    }

    public int getState() {
        return state;
    }

    public void setHP(float HP) {
        this.HP = HP;
    }

    public float getHP() {
        return HP;
    }

    public float getHPlen() {
        return HPlen;
    }

    public float getMaxHP() {
        return maxHP;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
        levelUpCost = (level+1)*60;
        maxHP = 10 + level*2;
        HP = 10 + level*2;
    }

    public void setSlideStartTime(int slideStartTime) {
        this.slideStartTime = slideStartTime;
    }

    public long getSlideStartTime() {
        return slideStartTime;
    }

    public int getHeight() {
        return playerRun.get(0).getHeight();
    }

    public int getLevelUpCost() {
        return levelUpCost;
    }

    //复原位置
    public void reset(){
       setPoint(new Point(startPoint.x,startPoint.y));
    }

}
