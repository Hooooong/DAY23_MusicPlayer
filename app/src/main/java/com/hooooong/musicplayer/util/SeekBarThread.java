package com.hooooong.musicplayer.util;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Android Hong on 2017-10-12.
 */

public class SeekBarThread extends Thread{

    private static SeekBarThread seekBarThread;
    private boolean runFlag = true;

    // Singleton Pattern
    private SeekBarThread(){

    }

    public static SeekBarThread getInstance(){
        if(seekBarThread == null){
            seekBarThread = new SeekBarThread();
        }
        return seekBarThread;
    }

    // 동기화 지원하는 Collection
    // run() 함수의 For 문에서 iObserverList 를 읽고 있으면
    // 대기하고 있다가 읽기가 끝나면 add(), remove() 를 실행하여
    // 충돌을 방지해준다.
    private List<IObserver> iObserverList = new CopyOnWriteArrayList<>();

    public void addObserver(IObserver iObserver){
        iObserverList.add(iObserver);
    }

    public void removeObserver(IObserver iObserver){
        iObserverList.remove(iObserver);
    }

    @Override
    public void run() {
        while(runFlag){
            for(IObserver iObserver : iObserverList){
                iObserver.setProgress();
            }

            try {
                sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void setStop(){
        runFlag = false;
    }

    // Observer Pattern
    public interface IObserver{
        void setProgress();
    }
}
