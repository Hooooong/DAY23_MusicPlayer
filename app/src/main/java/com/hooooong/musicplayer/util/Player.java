package com.hooooong.musicplayer.util;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;

import com.hooooong.musicplayer.data.Const;

/**
 * Created by Android Hong on 2017-10-12.
 */

public class Player {

    private MediaPlayer mediaPlayer;
    private boolean loop = true;

    private int status = Const.STAT_STOP;
    private int current = -1;

    // Singleton
    private static Player player;
    public static Player getInstance(){
        if(player == null){
            player = new Player();
        }
        return player;
    }
    private Player(){}

    // 음원 세팅
    public void set(Context context, int current, Uri musicUri){
        if(mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        this.current = current;

        mediaPlayer = MediaPlayer.create(context, musicUri);
        mediaPlayer.setLooping(loop);
    }

    public void start(){
        if(mediaPlayer != null) {
            mediaPlayer.start();
            status = Const.STAT_PLAY;
        }
    }

    public void pause(){
        if(mediaPlayer != null) {
            mediaPlayer.pause();
            status = Const.STAT_PAUSE;
        }
    }

    public void stop(){
        if(mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
            status = Const.STAT_STOP;
        }
    }

    public int getStatus(){
        return status;
    }

    public boolean isPlay(){
        if(mediaPlayer != null){
            return  mediaPlayer.isPlaying();
        }
        return false;
    }

    public int getCurrentPosition(){
        if(mediaPlayer != null){
            return mediaPlayer.getCurrentPosition();
        }
        return 0;
    }

    public int getDuration(){
        if(mediaPlayer != null){
            return mediaPlayer.getDuration();
        }
        return 0;
    }

    public int getCurrent(){
        return current;
    }

}
