package com.hooooong.musicplayer.view.player;

import android.Manifest;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hooooong.musicplayer.R;
import com.hooooong.musicplayer.data.Const;
import com.hooooong.musicplayer.data.model.Music;
import com.hooooong.musicplayer.util.PlayerService;
import com.hooooong.musicplayer.view.BaseActivity;
import com.hooooong.musicplayer.view.player.adapter.PlayerPageAdapter;

import jp.wasabeef.glide.transformations.BlurTransformation;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

public class PlayerActivity extends BaseActivity implements View.OnClickListener {

    private MediaPlayer mediaPlayer;
    private Music music;
    private int current = -1;

    private Toolbar toolbar;
    private ViewPager viewPager;
    private ImageView imgAlbum;
    private RelativeLayout controller;
    private SeekBar seekBar;
    private TextView textCurrentTime;
    private TextView textDuration;
    private TextView textTitle;
    private TextView textArtist;
    private ImageView imgPlay;
    private ImageView imgFf;
    private ImageView imgRew;
    private ImageView imgNext;
    private ImageView imgPrev;

    private int playButtonState = Const.STAT_PLAY;
    Thread seekBarThread = null;

    public PlayerActivity() {
        super(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE});
    }

    @Override
    public void init() {
        load();
        initView();
        initViewPager();
        initControl();
        start();
    }

    private void load() {
        music = Music.getInstance();
        Intent intent = getIntent();
        if (intent != null) {
            current = intent.getIntExtra(Const.KEY_POSITION, 0);
        }
    }

    private void initControl() {
        setPlayer();
    }

    private void setPlayer() {
        clearPlayer();

        // Position 에 해당하는 현재 아이템 꺼내기
        Music.Item item = music.getItemList().get(current);
        Uri musicUri = item.musicUri;

        // Music Uri 를 통해 Player 초기화
        mediaPlayer = MediaPlayer.create(this, musicUri);
        mediaPlayer.setLooping(false);

        setPlayerView(item);

        seekBarThread = new Thread() {
            @Override
            public void run() {
                if (mediaPlayer != null) {
                    try {
                        while (!this.isInterrupted()) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    seekBar.setProgress(mediaPlayer.getCurrentPosition());
                                    textCurrentTime.setText(milliToSec(mediaPlayer.getCurrentPosition()));
                                }
                            });

                            Thread.sleep(100);
                        }
                    } catch (InterruptedException e) {
                        Log.e("Thread.isInterrupted", this.isInterrupted() + "");
                        Log.e("Thread.isAlive()", this.isAlive() + "");
                        Log.e("Thread.getName()", this.getName());
                    }
                }
            }
        };
        seekBarThread.start();
    }

    /**
     * Player 에 관련된 View Setting
     */
    private void setPlayerView(Music.Item item) {
        // 화면 세팅
        String duration = milliToSec(mediaPlayer.getDuration());
        textDuration.setText(duration);
        textCurrentTime.setText("00:00");

        // 배경 세팅
        Glide.with(this)
                .load(item.albumUri)
                .apply(bitmapTransform(new BlurTransformation(25)))
                .into(imgAlbum);

        // Title, Artist 세팅
        textTitle.setText(item.title);
        textArtist.setText(item.artist);
        // seekBar 세팅
        seekBar.setMax(mediaPlayer.getDuration());
    }

    /**
     * 1110101 -> 04:00 으로 변환하는 메소드
     *
     * @param milli
     * @return
     */
    private String milliToSec(int milli) {
        int sec = milli / 1000;
        int min = sec / 60;
        sec = sec % 60;

        return java.lang.String.format("%02d", min) + ":" + java.lang.String.format("%02d", sec);
    }

    private void initView() {
        setContentView(R.layout.activity_player);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        textTitle = toolbar.findViewById(R.id.textTitle);
        textTitle.setSelected(true);
        textArtist = toolbar.findViewById(R.id.textArtist);
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        controller = (RelativeLayout) findViewById(R.id.controller);

        seekBar = (SeekBar) findViewById(R.id.seekBar);
        textCurrentTime = (TextView) findViewById(R.id.textCurrentTime);
        textDuration = (TextView) findViewById(R.id.textDuration);
        imgPlay = (ImageView) findViewById(R.id.imgPlay);
        imgFf = (ImageView) findViewById(R.id.imgFf);
        imgRew = (ImageView) findViewById(R.id.imgRew);
        imgPrev = (ImageView) findViewById(R.id.imgPrev);
        imgNext = (ImageView) findViewById(R.id.imgNext);

        imgAlbum = (ImageView) findViewById(R.id.imgAlbum);

        imgPlay.setOnClickListener(this);
        imgFf.setOnClickListener(this);
        imgRew.setOnClickListener(this);
        imgPrev.setOnClickListener(this);
        imgNext.setOnClickListener(this);
    }

    private void initViewPager() {
        PlayerPageAdapter playerPageAdapter = new PlayerPageAdapter(this, music.getItemList());
        viewPager.setAdapter(playerPageAdapter);
        if (current > 0) {
            viewPager.setCurrentItem(current);
        }
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                current = position;
                setPlayer();
                if (playButtonState == Const.STAT_PLAY) {
                    start();
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void start() {
        playButtonState = Const.STAT_PLAY;
        mediaPlayer.start();
        imgPlay.setBackgroundResource(R.drawable.ic_stop);
    }

    private void pause() {
        playButtonState = Const.STAT_PAUSE;
        mediaPlayer.pause();
        imgPlay.setBackgroundResource(R.drawable.ic_play_arrow);
    }

    private void clearPlayer() {
        if (seekBarThread != null) {
            seekBarThread.interrupt();
        }
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imgPlay:
                if (playButtonState == Const.STAT_PLAY) {
                    // Player 중이라면
                    pause();
                } else {
                    start();
                }
                break;
            case R.id.imgFf:
                break;
            case R.id.imgRew:
                break;
            case R.id.imgPrev:
                viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
                break;
            case R.id.imgNext:
                viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
                break;
        }
    }

    /**
     * Appbar 메뉴 생성 초기화
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_back, menu);
        return true;
    }

    /**
     * Appbar 메뉴 선택 이벤트
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_back) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        clearPlayer();
        super.onDestroy();
    }


}
