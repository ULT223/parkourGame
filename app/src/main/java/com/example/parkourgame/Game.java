package com.example.parkourgame;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.icu.text.DecimalFormat;
import android.os.Environment;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.parkourgame.GameObject.Coin;
import com.example.parkourgame.GameObject.Ground;
import com.example.parkourgame.GameObject.Player;
import com.example.parkourgame.GameObject.Enemy;
import com.example.parkourgame.GameObject.ScoreDraw;
import com.example.parkourgame.GameUI.Back;
import com.example.parkourgame.GameUI.CharacterButton;
import com.example.parkourgame.GameUI.GameBG;
import com.example.parkourgame.GameUI.LevelUp;
import com.example.parkourgame.GameUI.Restart;
import com.example.parkourgame.GameUI.buttonStart;
import com.example.parkourgame.GameUI.playerLevel;
import com.example.parkourgame.util.BitmapUtils;
import com.example.parkourgame.util.SoundManager;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Vector;

public class Game extends SurfaceView implements SurfaceHolder.Callback{

    private static final String TAG  = "Game";//tag 打印日志用


    /*
        上下文
     */
    private Context context;

    /*
        控制surfaceView绘制
     */
    private SurfaceHolder surfaceHolder;
    private Canvas canvas;
    private Paint paint;
    private Paint FPSpaint;//FPS绘制画笔
    private int gameState;//游戏状态
    private Resources res;//资源上下文
    private GameLoop gameLoop;
    private GestureDetector gestureDetector;

    private boolean isRunning;//游戏运行标志
    private boolean isSaved;//是否保存


    /*
        游戏物体
     */
    private Player player;//玩家
    private Vector<Ground> groundGroup;//地面组
    private Vector<Coin> coinGroup;//金币组
    private Vector<Bitmap> coinBitmap;//金币位图
    private Vector<Enemy> enemiesOnGround;//地面上的怪物
    private Vector<Enemy> enemiesFly;//飞行的怪物


    /*
        UI
     */
    private GameBG gameBG;//游戏背景
    private buttonStart buttonStart;
    private Back back;//返回键
    private Restart restart;//重新开始按钮
    private ScoreDraw scoreDraw;//绘制得分
    private CharacterButton characterButton;//角色按钮
    private playerLevel playerLevel;//玩家等级 字
    private LevelUp levelup;//升级按钮


    /*
        屏幕信息
     */
    private int screenW;
    private int screenH;
    private double FPS;//刷新率
    private double FPSSum;//用于计算平均刷新率
    private int AverageFPS;//平均刷新率
    private int countFPS;
    private boolean AverageFPSFLAG;//平均刷新率是否被计算
    private int countTimes;//屏幕刷新次数
    private DecimalFormat FPSFormat;//刷新率显示格式

    /*
        游戏数据
     */
    private int LastGroundX;//地面最右坐标
    private int speed;
    private int score;
    private int totalScore;
    private int maxEnemyNum;//现存最大敌人数量
    private int minDistance;//敌人间隔最短距离

    /*
        音频控制
     */
    private SoundManager soundManager;


    public Game(Context context) {
        super(context);
        this.context = context;
        surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);
        gestureDetector = new GestureDetector(context, new MyGestureListener());//手势监听器
        gameState = GameProperty.GAME_START;
        soundManager = new SoundManager(context);//添加音频管理器
        totalScore = 0;
    }

    /**
     * 初始化成员变量
     */
    private void init(){
        res = getResources();
        countTimes = 0;
        gameBG = new GameBG(BitmapFactory.decodeResource(res,GameProperty.GAME_BG2_5),BitmapFactory.decodeResource(res,GameProperty.GAME_BG2_1));
        //UI
        buttonStart = new buttonStart(BitmapFactory.decodeResource(res,GameProperty.GAME_START_BTN),BitmapFactory.decodeResource(res,GameProperty.GAME_START_BTN_PRESSED),new Point(screenW/2,screenH/2));
        back = new Back(BitmapFactory.decodeResource(res,GameProperty.GAME_BACK),new Point(50,50));
        restart = new Restart(BitmapFactory.decodeResource(res,GameProperty.GAME_RESTART),new Point(screenW/2,screenH/2));
        characterButton = new CharacterButton(BitmapFactory.decodeResource(res,GameProperty.GAME_CHARACTER_BUTTON),new Point(screenW/15,3*screenH/4));
        playerLevel = new playerLevel(BitmapFactory.decodeResource(res,GameProperty.GAME_PLAYER_LEVEL),new Point(screenW/2 - 200,screenH/2 - 100));
        levelup = new LevelUp(BitmapFactory.decodeResource(res,GameProperty.GAME_PLAYER_LEVEL_UP),new Point(playerLevel.getRect().left,playerLevel.getRect().bottom + 100));

        Log.d(TAG,"宽高为:"+buttonStart.getBtnWidth() + "," + buttonStart.getBtnHeight());
        FPSpaint = new Paint();
        FPSpaint.setTextSize(30);
        FPSpaint.setColor(Color.RED);
        FPSFormat = new DecimalFormat("#.##");
        speed = 10;
        AverageFPS = 60;
        countFPS = 0;
        AverageFPSFLAG = false;
        FPSSum = 0;
        paint = new Paint();
        //初始化玩家
        Vector<Bitmap> plrun = new Vector<Bitmap>();
        Vector<Bitmap> plSilde = new Vector<Bitmap>();
        Log.d(TAG,"图片大小:"+BitmapFactory.decodeResource(res,GameProperty.GAME_PLAYER).getWidth()+","+BitmapFactory.decodeResource(res,GameProperty.GAME_PLAYER).getHeight());
        plrun.add(BitmapFactory.decodeResource(res,GameProperty.GAME_PLAYER_RUN1));
        plrun.add(BitmapFactory.decodeResource(res,GameProperty.GAME_PLAYER_RUN2));
        plrun.add(BitmapFactory.decodeResource(res,GameProperty.GAME_PLAYER_RUN3));
        plrun.add(BitmapFactory.decodeResource(res,GameProperty.GAME_PLAYER_RUN4));
        plrun.add(BitmapFactory.decodeResource(res,GameProperty.GAME_PLAYER_RUN5));
        plrun.add(BitmapFactory.decodeResource(res,GameProperty.GAME_PLAYER_RUN6));
        plrun.add(BitmapFactory.decodeResource(res,GameProperty.GAME_PLAYER_RUN7));
        plrun.add(BitmapFactory.decodeResource(res,GameProperty.GAME_PLAYER_RUN8));
        plrun.add(BitmapFactory.decodeResource(res,GameProperty.GAME_PLAYER_RUN9));
        plrun.add(BitmapFactory.decodeResource(res,GameProperty.GAME_PLAYER_RUN10));
        plrun.add(BitmapFactory.decodeResource(res,GameProperty.GAME_PLAYER_RUN11));
        plrun.add(BitmapFactory.decodeResource(res,GameProperty.GAME_PLAYER_RUN12));
        plSilde.add(BitmapFactory.decodeResource(res,GameProperty.GAME_PLAYER_SLIDE1));
        plSilde.add(BitmapFactory.decodeResource(res,GameProperty.GAME_PLAYER_SLIDE2));
        player = new Player(new Point(screenW/6,screenH*3/4),plrun,plSilde);//screen*3/4是根线，是玩家的底部，也是 地面的顶部+40
        Rect plRect = player.getRect();
        player.setPoint(new Point(plRect.left-plRect.right+plRect.left,plRect.top+plRect.top-plRect.bottom));
        player.setStartPoint(new Point(plRect.left-plRect.right+plRect.left,plRect.top+plRect.top-plRect.bottom));
        Log.d(TAG,"玩家坐标:"+"("+player.getRect().left+","+player.getRect().top+")");
        //初始化地面
        groundGroup = new Vector<Ground>();
        Vector<Bitmap> gr = new Vector<Bitmap>();
        gr.add(BitmapFactory.decodeResource(res,GameProperty.GAME_GROUND1));
        gr.add(BitmapFactory.decodeResource(res,GameProperty.GAME_GROUND2));
        gr.add(BitmapFactory.decodeResource(res,GameProperty.GAME_GROUND3));
        Ground ground = new Ground(gr,new Point(0,3*screenH/4 - 40));
        groundGroup.add(ground);
        Point p = new Point(ground.getWidth()-5,3*screenH/4 - 40);
        for (int i = 0; i < 22; i++) {//添加21个中间地面
            Ground g = new Ground(gr,new Point(p.x,p.y));;
            g.setIndex(1);
            groundGroup.add(g);
            p.x += g.getWidth()-5;
            Log.d(TAG,"坐标:"+g.getPoint());
        }
        LastGroundX = groundGroup.lastElement().getPoint().x+groundGroup.lastElement().getWidth();//最后一块地面的坐标

        //初始化金币组，添加金币
        //初始化金币
        coinGroup = new Vector<Coin>();
        coinBitmap = new Vector<>();
        int coinHeight = BitmapFactory.decodeResource(res, GameProperty.GAME_COIN).getHeight();
        int coinWidth = BitmapFactory.decodeResource(res,GameProperty.GAME_COIN).getWidth();
        for (int i = 0;i < 3 ; i++){
            for (int j = 0; j < 3; j++) {
                Bitmap bitmap = BitmapUtils.cropBitmap(BitmapFactory.decodeResource(res,GameProperty.GAME_COIN),i*coinWidth/3,j*coinHeight/5,coinWidth/3,coinHeight/5);
                coinBitmap.add(bitmap);
            }
        }
        coinBitmap.add( BitmapUtils.cropBitmap(BitmapFactory.decodeResource(res,GameProperty.GAME_COIN),0,3*coinHeight/5,coinWidth/3,coinHeight/5));
        coinBitmap.add( BitmapUtils.cropBitmap(BitmapFactory.decodeResource(res,GameProperty.GAME_COIN),coinWidth/3,3*coinHeight/5,coinWidth/3,coinHeight/5));
        coinBitmap.add( BitmapUtils.cropBitmap(BitmapFactory.decodeResource(res,GameProperty.GAME_COIN),0,4*coinHeight/5,coinWidth/3,coinHeight/5));
        coinGroup.add(new Coin(coinBitmap,new Point(5*screenW/6,3*screenH/5)));

        score = 0;
        Log.d(TAG,"player的rect:"+player.getRect());
        Log.d(TAG,"ground0的rect:"+groundGroup.get(0).getRect());

        //初始化敌人
        enemiesOnGround = new Vector<>();
        enemiesFly = new Vector<>();
        maxEnemyNum = 5;
        minDistance = screenW/5;

        //得分绘制类
        Bitmap[] numsBitmap = new Bitmap[10];
        numsBitmap[0] = BitmapFactory.decodeResource(res,GameProperty.ZERO);
        numsBitmap[1] = BitmapFactory.decodeResource(res,GameProperty.ONE);
        numsBitmap[2] = BitmapFactory.decodeResource(res,GameProperty.TWO);
        numsBitmap[3] = BitmapFactory.decodeResource(res,GameProperty.THREE);
        numsBitmap[4] = BitmapFactory.decodeResource(res,GameProperty.FOUR);
        numsBitmap[5] = BitmapFactory.decodeResource(res,GameProperty.FIVE);
        numsBitmap[6] = BitmapFactory.decodeResource(res,GameProperty.SIX);
        numsBitmap[7] = BitmapFactory.decodeResource(res,GameProperty.SEVEN);
        numsBitmap[8] = BitmapFactory.decodeResource(res,GameProperty.EIGHT);
        numsBitmap[9] = BitmapFactory.decodeResource(res,GameProperty.NINE);

        Bitmap scoreBitmap = BitmapFactory.decodeResource(res,GameProperty.SCORE);//得分
        scoreDraw = new ScoreDraw(numsBitmap,scoreBitmap);

        //读取数据
        Map<String, Integer> map = readData();
        if (map != null){
            player.setLevel(map.get("level"));
            totalScore = map.get("totalScore");
            Log.d(TAG,"读取数据,level:"+ map.get("level") +",totalScore:"+ map.get("totalScore"));
        }

        //游戏运行
        isRunning = true;
        isSaved = false;
    }

    /**
     * 地面新增元素方法
     */
    private void groundGroupAdd(Point point,int index){
        //初始化地面
        Vector<Bitmap> gr = new Vector<Bitmap>();
        gr.add(BitmapFactory.decodeResource(res,GameProperty.GAME_GROUND1));
        gr.add(BitmapFactory.decodeResource(res,GameProperty.GAME_GROUND2));
        gr.add(BitmapFactory.decodeResource(res,GameProperty.GAME_GROUND3));
        Ground ground = new Ground(gr,point);
        ground.setIndex(index);
        groundGroup.add(ground);
    }


    @Override
    public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {
        screenW = getWidth();
        screenH = getHeight();
        init();
        gameLoop = new GameLoop(this);
        gameLoop.start();
        Log.d(TAG,"屏幕宽和高为:" + screenW + ","+screenH);
    }

    /**
     * 生成敌人
     */
    private void generateEnemy(){
        Random random = new Random();
        Point point = new Point();
        Enemy enemy;
        int maxX = getMaxX(enemiesFly,enemiesOnGround);
        if (maxX == 0)
            point.x = random.nextInt(screenW)+ screenW;
        else if (maxX > screenW) {
            point.x = random.nextInt(screenW)+ maxX + minDistance;
        } else {
            point.x = random.nextInt(screenW)+ (enemiesOnGround.size() + enemiesFly.size())*minDistance + screenW;
        }
        switch (random.nextInt(1000) % 2){
            case 0:
                //生成地面敌人
                enemy = new Enemy(BitmapFactory.decodeResource(res,GameProperty.GAME_ENEMY),point);
                point.y = 3*screenH/4 - enemy.getRect().height();
                enemiesOnGround.add(enemy);
                break;
            case 1:
                //生成天上敌人
                enemy = new Enemy(BitmapFactory.decodeResource(res,GameProperty.GAME_FLY_ENEMY),point);
                point.y =3*screenH/4 - enemy.getRect().height() - player.getHeight() + 10;
                Log.d(TAG,"天上敌人位置:"+point);
                enemiesFly.add(enemy);
                break;
        }
    }

    private int getMaxX(List<Enemy> enemyFly, List<Enemy> enemyOnGround) {
        int maxX = Integer.MIN_VALUE;

        // 遍历飞行敌人列表，找到最大 x 值
        if (enemyFly != null) {
            for (Enemy enemy : enemyFly) {
                int enemyX = enemy.getPoint().x;
                if (enemyX > maxX) {
                    maxX = enemyX;
                }
            }
        }

        // 遍历地面敌人列表，找到最大 x 值
        if (enemyOnGround != null) {
            for (Enemy enemy : enemyOnGround) {
                int enemyX = enemy.getPoint().x;
                if (enemyX > maxX) {
                    maxX = enemyX;
                }
            }
        }

        // 如果列表为空，返回 Integer.MIN_VALUE
        if (maxX == Integer.MIN_VALUE) {
            maxX = 0;
        }

        return maxX;
    }


    /**
     * 生成金币
     */
    private void generateCoin(){
        int bottom = 3*screenH/4 - coinBitmap.get(0).getHeight();
        int top = bottom - 320;//320为玩家跳跃的最高距离(自己计算的)。金币在这个高度范围内生成，宽度范围则为screenW ~ screenW * 2
        int rangeY = bottom-top;
        Random random = new Random();
        int y = random.nextInt(rangeY) + top;
        int x = random.nextInt(screenW) + screenW;
        Coin coin = new Coin(coinBitmap,new Point(x,y));
        coinGroup.add(coin);//添加金币
    }

    /**
     * 计算游戏平均帧率
     */
    private void calculateAverageFps(){
        //先计算平均帧率,一次即可
        if (countTimes % 20 == 0 && FPS != 0){
            FPSSum += FPS;
            countFPS++;
        }
        if (countFPS == 60){
            AverageFPS = (int)FPSSum/60;
            AverageFPSFLAG = true;
            Log.d(TAG,"FPSSum:"+FPSSum);
            Log.d(TAG,"AverageFPS:" + AverageFPS);
            FPSSum = 0;
            countFPS = 0;//计算完后重置
        }
    }

    /**
     * 碰撞检测
     */
    private void collisionDetect(){
        //金币后移且碰撞检测
        Iterator<Coin> coinIterator = coinGroup.iterator();
        while (coinIterator.hasNext()){
            Coin next = coinIterator.next();
            Point point = next.getPoint();
            if (point.x < -50){
                coinIterator.remove();
            }else {
                //碰撞检测
                Rect playerRect = player.getRect();
                Rect coinRect = next.getRect();
                if (playerRect.intersect(coinRect)){
                    coinIterator.remove();
                    score++;
                    continue;
                }
                point.x -= speed;
            }
        }

        //敌人后移且碰撞检测
        Iterator<Enemy> iteratorEnemyGround = enemiesOnGround.iterator();//地面上的敌人
        while (iteratorEnemyGround.hasNext()){
            Enemy enemy = iteratorEnemyGround.next();
            Point point = enemy.getPoint();
            Rect playerRect = player.getRect();
            Rect enemyRect = enemy.getRect();
            if (point.x + enemyRect.width() < 0){//已移动到屏幕外则移除
                iteratorEnemyGround.remove();
                continue;
            }else {
                //碰撞检测
                if (playerRect.intersect(enemyRect)){
                    float HP = player.getHP();
                    HP--;//碰撞，玩家生命值损失1
                    soundManager.playSound(SoundManager.SOUND_HIT);
                    player.setHP(HP);
                    iteratorEnemyGround.remove();
                    continue;
                }
                point.x -= speed;
            }
        }

        Iterator<Enemy> iteratorEnemyFly = enemiesFly.iterator();//地面上的敌人
        while (iteratorEnemyFly.hasNext()){
            Enemy enemy = iteratorEnemyFly.next();
            Point point = enemy.getPoint();
            Rect playerRect = player.getRect();
            Rect enemyRect = enemy.getRect();
            if (point.x + enemyRect.width() < 0){//已移动到屏幕外则移除
                iteratorEnemyFly.remove();
                continue;
            }else {
                //碰撞检测
                if (player.getState()!=Player.PLAYER_STATE_SLIDE && playerRect.intersect(enemyRect)){
                    float HP = player.getHP();
                    HP--;//碰撞，玩家生命值损失1
                    soundManager.playSound(SoundManager.SOUND_HIT);
                    player.setHP(HP);
                    iteratorEnemyFly.remove();
                    continue;
                }
                point.x -= speed;
                //Log.d(TAG,"玩家血量:"+player.getHP());
            }
        }
    }


    /**
     *  更新游戏数据(更新model)
     */
    public void update() {
        switch (gameState){
            case GameProperty.GAME_START:

                break;
            case GameProperty.GAME_ING:
                if (!AverageFPSFLAG)
                    calculateAverageFps();
                if (player.getState() == Player.PLAYER_STATE_SLIDE){//若为下滑状态
                    //Log.d(TAG,"玩家为下滑状态");
                    if (countTimes % (player.getSlideStartTime()+AverageFPS) == 0){
                        Log.d(TAG,"玩家从下滑状态变为跑步状态");
                        Log.d(TAG,"玩家坐标:"+"("+player.getRect().left+","+player.getRect().top+")");
                        player.setState(Player.PLAYER_STATE_RUN);
                        player.reset();
                    }
                }
                //每隔四帧改变玩家帧，金币帧
                if (countTimes%4 == 0){
                    player.framePlus();
                    Iterator<Coin> coinIterator = coinGroup.iterator();
                    while (coinIterator.hasNext()){
                        coinIterator.next().framePlus();
                    }
                }

                //每隔一秒生成金币
                if (countTimes%AverageFPS == 0){
                    generateCoin();
                }
                //每隔5秒生成敌人
                if (countTimes%5*AverageFPS == 0 && enemiesFly.size() + enemiesOnGround.size() < maxEnemyNum){
                    generateEnemy();
                }
                if (countTimes%(AverageFPS/20) == 0){
                    player.physical();
                    if (groundGroup.get(0).getRect().top - player.getRect().top > player.getHeight()){
                        //若玩家离地面超过40px则调用重力方法
                        player.setAcceleration(10);
                        Log.d(TAG,"调用玩家重力方法");
                    }else if (player.getSpeed() != 0){
                        player.setAcceleration(0);
                        player.setSpeed(0);
                        player.reset();//复原位置
                        Log.d(TAG,"玩家跳跃并复原位置");
                        Log.d(TAG,"玩家由跳跃状态变为跑步状态");
                        if (player.getState() == Player.PLAYER_STATE_JUMP)
                            player.setState(Player.PLAYER_STATE_RUN);

                    }
                }

                //地面后移，形成动态效果
                Iterator<Ground> groundIterator = groundGroup.iterator();
                while (groundIterator.hasNext()){
                    Ground ground = groundIterator.next();
                    if (ground.getPoint().x < -140) //移除超出屏幕的地面，因为地面1图片宽为140，坐标为-140时超出屏幕左侧
                        groundIterator.remove();
                    Point p = ground.getPoint();
                    p.x -= speed; //移动
                    ground.setPoint(p);
                }
                //一边后移一边新增地面
                if (!groundGroup.isEmpty() && groundGroup.lastElement().getPoint().x+groundGroup.lastElement().getWidth() < LastGroundX){
                    //若{最后元素的坐标+图片宽}小于 3024+140 则新增，3024为初始时最后图片的坐标，同时图片宽度为140故如此添加,仅限我的手机，故我用LastGroundX记录末尾坐标
                    groundGroupAdd(new Point(LastGroundX-5-speed,screenH*3/4 - 40),1);//添加起始坐标为3164-10 是为了控制地面间没有间隔,本来-5就能控制间隔，但是刚好往后移动了一个speed位置，故要再减一个speed
                    //Log.d(TAG,"最后地面的坐标"+groundGroup.lastElement().getPoint());
                }
                //碰撞检测
                collisionDetect();
                if (player.getHP() <= 0){
                    //若hp归零，游戏状态变为失败
                    soundManager.playSound(SoundManager.SOUND_LOSE);
                    gameState = GameProperty.GAME_LOSE;
                    soundManager.stopBGM();
                }
                if (score == 60){//拿到60分游戏胜利
                    soundManager.playSound(SoundManager.SOUND_SUCCESS);
                    gameState = GameProperty.GAME_WIN;
                    soundManager.stopBGM();
                }
                break;
            case GameProperty.GAME_WIN:
                if (!isSaved){//保存数据
                    writeData();
                    isSaved = true;
                    readData();
                }
                break;
            case GameProperty.GAME_LOSE:

                break;
        }
    }

    /*
     *  绘制游戏帧
     */
    public int drawFrame() {
        canvas = surfaceHolder.lockCanvas();
        canvas.drawColor(Color.WHITE);
        switch (gameState){
            case GameProperty.GAME_START:
                gameBG.setBackground1(BitmapFactory.decodeResource(res,GameProperty.GAME_BG2_5));
                gameBG.drawOutOfGame(canvas,paint);
                buttonStart.drawStart(canvas,paint);
                characterButton.draw(canvas,paint);
                break;
            case GameProperty.GAME_CHARACTER:
                gameBG.drawOutOfGame(canvas,paint);
                back.draw(canvas,paint);
                playerLevel.draw(canvas,paint);
                scoreDraw.drawNums(canvas,player.getLevel(),new Point(screenW/2+50,screenH/2-90));
                levelup.draw(canvas,paint);
                scoreDraw.drawScore(canvas,new Point(screenW/2-100,screenH/6));
                scoreDraw.drawNums(canvas,totalScore,new Point(screenW/2+100,screenH/6));
                break;
            case GameProperty.GAME_ING:
                gameBG.drawInGame(canvas,paint);//绘制背景
                back.draw(canvas,paint);
                //绘制地面
                //Log.d(TAG,"GroundGroup大小:"+groundGroup.size());
                Iterator<Ground> iterator = groundGroup.iterator();
                while (iterator.hasNext()){
                    Ground next = iterator.next();
                    next.draw(canvas,paint);
                }
                //绘制金币
                Iterator<Coin> coinIterator = coinGroup.iterator();
                while (coinIterator.hasNext()){
                    coinIterator.next().drawCoin(canvas,paint);
                }
                //绘制玩家
                player.draw(canvas,paint);
                //绘制敌人
                Iterator<Enemy> enemyIterator1 = enemiesOnGround.iterator();
                while (enemyIterator1.hasNext()){
                    Enemy enemy = enemyIterator1.next();
                    enemy.draw(canvas,paint);
                }

                Iterator<Enemy> enemyIterator2 = enemiesFly.iterator();
                while (enemyIterator2.hasNext()){
                    Enemy enemy = enemyIterator2.next();
                    enemy.draw(canvas,paint);
                }
                Paint sPaint = new Paint();
                sPaint.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD_ITALIC));
                canvas.drawText("Score:"+score,screenW-200,100,FPSpaint);
                player.drawHealthBar(canvas);
                break;

            case GameProperty.GAME_WIN:
                gameBG.setBackground1(BitmapFactory.decodeResource(res,GameProperty.GAME_BG2_3));
                gameBG.drawOutOfGame(canvas,paint);
                back.draw(canvas,paint);
                scoreDraw.drawScore(canvas,new Point(screenW/2-100,screenH/2-100));
                scoreDraw.drawNums(canvas,totalScore,new Point(screenW/2+100,screenH/2-100));
                break;

            case GameProperty.GAME_LOSE:
                gameBG.setBackground1(BitmapFactory.decodeResource(res,GameProperty.GAME_BG2_4));
                gameBG.drawOutOfGame(canvas,paint);
                back.draw(canvas,paint);
                restart.drawStart(canvas,paint);
                break;
        }
        //绘制FPS
        canvas.drawText("FPS:"+FPSFormat.format(FPS),screenW-200,50,FPSpaint);

        countTimes++;//屏幕刷新一次，次数加一
        if (countTimes == Integer.MAX_VALUE){//重置
            countTimes=0;
        }
        surfaceHolder.unlockCanvasAndPost(canvas);
        return countTimes;
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) {
        soundManager.release();
    }

    /**
     * 保存数据
     */
    private void writeData(){
        try {
            if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())){//检查外部存储是否可写
                // 获取外部存储的根目录
                File directory = context.getExternalFilesDir(null);
                if (directory != null) {

                    // 在子目录中创建名为 "data" 的文件
                    File file = new File(directory, "data");
                    FileOutputStream fos = new FileOutputStream(file);
                    ObjectOutputStream oos = new ObjectOutputStream(fos);

                    Map<String, Integer> map = new HashMap<>();
                    map.put("totalScore", totalScore+score); // 总分放入文件中保存
                    map.put("level", player.getLevel());   // 玩家等级,保存

                    oos.writeObject(map); // 写入数据
                    oos.close();
                    fos.close();
                    Log.d(TAG,"totalScore:"+totalScore+"\nlevel:"+player.getLevel() + "已保存！");
                    Log.d(TAG, "Data saved to: " + file.getAbsolutePath());
                }
            } else {
                Log.e(TAG, "External storage not writable");
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 读取数据
     */
    private Map<String, Integer> readData() {
        Map<String, Integer> dataMap = null;
        try {
            if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) { // 检查外部存储是否可读
                File directory = context.getExternalFilesDir(null);
                if (directory != null) {
                    File file = new File(directory, "data");
                    if (file.exists()) {
                        FileInputStream fis = new FileInputStream(file);
                        ObjectInputStream ois = new ObjectInputStream(fis);
                        dataMap = (Map<String, Integer>) ois.readObject();
                        ois.close();
                        fis.close();
                        Log.d(TAG, "Data loaded from: " + file.getAbsolutePath());
                    } else {
                        Log.e(TAG, "Data file not found");
                    }
                }
            } else {
                Log.e(TAG, "External storage not readable");
            }
        } catch (FileNotFoundException e) {
            Log.e(TAG, "File not found", e);
        } catch (IOException e) {
            Log.e(TAG, "Error reading file", e);
        } catch (ClassNotFoundException e) {
            Log.e(TAG, "Class not found", e);
        } catch (Exception e) {
            Log.e(TAG, "Unexpected error", e);
        }
        return dataMap;
    }


    /**
     *   监听事件
     *
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int)event.getX();
        int y = (int)event.getY();
        Log.d(TAG,"触摸事件触发，位置:("+x+","+y+"),"+"事件:" + event.getAction());
        switch (gameState){
            case GameProperty.GAME_START:
                Rect buttonRect = buttonStart.getRect();
                Rect characterRect = characterButton.getRect();
                if (buttonStart.getRect().contains(x,y) && event.getAction() != MotionEvent.ACTION_UP){//在按钮区域内
                    buttonStart.setPressed(true);
                    Log.d(TAG,"按下按钮");
                }else if (buttonStart.getRect().contains(x,y) && event.getAction() == MotionEvent.ACTION_UP){
                    buttonStart.setPressed(false);
                    soundManager.playSound(SoundManager.SOUND_BUTTON);
                    Log.d(TAG,"按下按钮，进入游戏");
                    soundManager.playSound(SoundManager.SOUND_START);
                    soundManager.startBGM();
                    init();
                    gameState = GameProperty.GAME_ING;
                }
                if (characterRect.contains(x,y)){
                    gameState = GameProperty.GAME_CHARACTER;
                    soundManager.playSound(SoundManager.SOUND_BUTTON);
                }
                break;
            case GameProperty.GAME_ING:
                if (back.getRect().contains(x,y)){
                    soundManager.stopBGM();
                    gameState = GameProperty.GAME_START;
                }
                gestureDetector.onTouchEvent(event);
                break;

            case GameProperty.GAME_WIN:
                Rect restartRect = restart.getRect();
                Rect backRect = back.getRect();
                if (backRect.contains(x,y)){
                    soundManager.playSound(SoundManager.SOUND_BUTTON);
                    gameState = GameProperty.GAME_START;
                }
                soundManager.stopBGM();//停止音乐
                break;

            case GameProperty.GAME_LOSE:
                restartRect = restart.getRect();
                backRect = back.getRect();
                if (restartRect.contains(x,y)){
                    soundManager.playSound(SoundManager.SOUND_BUTTON);
                    init();
                    gameState = GameProperty.GAME_ING;
                    soundManager.startBGM();//播放音乐
                }
                if (backRect.contains(x,y)){
                    soundManager.playSound(SoundManager.SOUND_BUTTON);
                    gameState = GameProperty.GAME_START;
                }
                soundManager.stopBGM();
                break;
            case GameProperty.GAME_CHARACTER:
                backRect = back.getRect();
                Rect levelupRect = levelup.getRect();
                if (backRect.contains(x,y)){
                    soundManager.playSound(SoundManager.SOUND_BUTTON);
                    gameState = GameProperty.GAME_START;
                }
                if (levelupRect.contains(x,y) && event.getAction() == MotionEvent.ACTION_DOWN){
                    if (player.getLevelUpCost() > totalScore)
                        Toast.makeText(context,"总分不足以支付升级",Toast.LENGTH_SHORT);
                    else{
                        totalScore -= player.getLevelUpCost();
                        player.setLevel(player.getLevel()+1);
                        writeData();
                    }
                }
                break;
        }
        return  super.onTouchEvent(event);
    }

    /**
     *
     * 手势监听器
     * 
     */
    private class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
        private static final int SWIPE_MIN_DISTANCE = 120;
        private static final int SWIPE_THRESHOLD_VELOCITY = 200;

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            try {
                float diffY = e2.getY() - e1.getY();
                if (Math.abs(diffY) > SWIPE_MIN_DISTANCE && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
                    if (diffY > 0) {
                        // 下滑事件触发时的操作
                        onSwipeBottom();
                    } else {
                        // 上滑事件触发时的操作
                        onSwipeTop();
                    }
                    return true;
                }
            } catch (Exception e) {
                // 捕获异常
            }
            return false;
        }

        private void onSwipeTop() {
            //仅在player状态为run时响应事件
            if (player.getState() == Player.PLAYER_STATE_RUN){
                // 处理上滑事件的逻辑
                Log.d(TAG,"触发上滑事件");
                player.setSpeed(-80);
                soundManager.playSound(SoundManager.SOUND_JUMP);
                player.setState(Player.PLAYER_STATE_JUMP);
                Log.d(TAG,"设置玩家速度:"+ player.getSpeed());
            }
        }

        private void onSwipeBottom() {
            // 处理下滑事件的逻辑
            //仅在player状态为run时响应事件
            if (player.getState() == Player.PLAYER_STATE_RUN){
                // 处理上滑事件的逻辑
                Log.d(TAG,"触发下滑事件");
                Log.d(TAG,"玩家坐标:"+"("+player.getRect().left+","+player.getRect().top+")");
                player.setState(Player.PLAYER_STATE_SLIDE);
                player.setSlideStartTime(countTimes);
            }
        }
    }
    /**
     * 设置fps
     * @param FPS
     */
    public void setFPS(double FPS) {
        this.FPS = FPS;
    }

    /**
     * 判断游戏是否仍在运行
     * @return
     */
    public boolean isRunning() {
        return isRunning;
    }


}
