package com.hooooong.musicplayer.util;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.hooooong.musicplayer.data.Const;
import com.hooooong.musicplayer.data.model.Music;

public class PlayerService extends Service {
    Player player = null;
    Music music = null;
    SeekBarThread seekBarThread = null;
    int current = -1;
    public PlayerService() {
    }

    // Context 를 구하려면 onCreate 에서 getBaseContext() 를 하는것이 좋다.
    // 생성자에서는 제거를 할 수 없기 떄문에
    @Override
    public void onCreate() {
        super.onCreate();
        player = Player.getInstance();
        seekBarThread = SeekBarThread.getInstance();
        seekBarThread.start();
        music =  Music.getInstance();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent != null){
            String action = intent.getAction();
            switch (action){
                case Const.ACTION_SET:
                    current = intent.getIntExtra(Const.KEY_POSITION, -1);
                    playerSet();
                    break;
                case Const.ACTION_START:
                    playerStart();
                    break;
                case Const.ACTION_PAUSE:
                    playerPause();
                    break;
                case Const.ACTION_STOP:
                    playerStop();
                    break;
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        if(player != null){
            player = null;
        }
        if(seekBarThread != null){
            seekBarThread.setStop();
            seekBarThread = null;
        }
        super.onDestroy();
    }

    private void playerSet(){
        if(current > -1){
            player.set(getBaseContext(), current ,music.getItemList().get(current).musicUri);
        }
    }

    private void playerStart(){
        player.start();
    }

    private void playerPause(){
        player.pause();
    }

    private void playerStop() {
        player.stop();
    }

}
