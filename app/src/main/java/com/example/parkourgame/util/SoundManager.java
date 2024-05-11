package com.example.parkourgame.util;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.SoundPool;

import com.example.parkourgame.R;

public class SoundManager {
    // SoundPool 对象用于播放短音效
    private SoundPool soundPool;
    // MediaPlayer 对象用于播放背景音乐
    private MediaPlayer bgmPlayer;
    private Context context;

    // 音效资源ID
    public static final int SOUND_BUTTON = 1;
    public static final int SOUND_HIT = 2;
    public static final int SOUND_JUMP = 3;
    public static final int SOUND_LOSE = 4;
    public static final int SOUND_START = 5;
    public static final int SOUND_SUCCESS = 6;

    // 在构造函数中初始化 SoundPool 和 MediaPlayer
    public SoundManager(Context context) {
        this.context = context;
        // 初始化 SoundPool
        soundPool = new SoundPool.Builder().setMaxStreams(10).build();
        // 初始化 MediaPlayer
        bgmPlayer = MediaPlayer.create(context, R.raw.background_music); // 这里的 R.raw.background_music 是你的背景音乐文件
        bgmPlayer.setLooping(true); // 设置循环播放
        bgmPlayer.setVolume(0.5f, 0.5f); // 设置音量

        // 加载音效文件并获取对应的音效资源ID
        soundPool.load(context, R.raw.button, 1);     // 按钮音效
        soundPool.load(context, R.raw.hitten, 1);     // 击中音效
        soundPool.load(context, R.raw.jump, 1);       // 跳跃音效
        soundPool.load(context, R.raw.lose, 1);       // 失败音效
        soundPool.load(context, R.raw.start, 1);      // 开始音效
        soundPool.load(context, R.raw.success, 1);    // 成功音效
    }

    // 播放短音效
    public void playSound(int soundID) {
        soundPool.play(soundID, 1, 1, 1, 0, 1);
    }

    // 开始播放背景音乐
    public void startBGM() {
        if (bgmPlayer != null && !bgmPlayer.isPlaying()) {
            bgmPlayer.start();
        }
    }

    // 停止播放背景音乐
    public void stopBGM() {
        if (bgmPlayer != null && bgmPlayer.isPlaying()) {
            bgmPlayer.pause();
        }
    }

    // 释放资源
    public void release() {
        if (soundPool != null) {
            soundPool.release();
            soundPool = null;
        }
        if (bgmPlayer != null) {
            bgmPlayer.release();
            bgmPlayer = null;
        }
    }
}
