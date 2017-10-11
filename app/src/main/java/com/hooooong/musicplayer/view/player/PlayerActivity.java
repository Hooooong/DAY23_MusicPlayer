package com.hooooong.musicplayer.view.player;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hooooong.musicplayer.R;
import com.hooooong.musicplayer.data.Const;
import com.hooooong.musicplayer.view.main.adapter.model.Music;
import com.hooooong.musicplayer.view.player.adapter.PlayerPageAdapter;

import jp.wasabeef.glide.transformations.BlurTransformation;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

public class PlayerActivity extends AppCompatActivity implements View.OnClickListener {

    private MediaPlayer player;
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
    private ImageButton btnPlay;
    private ImageButton btnFf;
    private ImageButton btnRew;
    private ImageButton btnNext;
    private ImageButton btnPrev;
    private int playButtonState = Const.STAT_PLAY;
    Thread seekBarThread = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        load();
        initView();
        initViewPager();
        initControl();
        start();
    }

    private void load() {
        music = Music.getInstance();
        Intent intent = getIntent();
        current = intent.getIntExtra(Const.KEY_POSITION, -1);
    }

    private void initControl() {
        // 볼룜 조절 버튼으로 미디어 음량만 조절하기 위한 설정
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        if (current == 0) {
            setPlayer();
        }
    }

    private void setPlayer() {
        // Position 에 해당하는 현재 아이템 꺼내기
        Music.Item item = music.getItemList().get(current);
        Uri musicUri = item.musicUri;

        if (player != null) {
            player.release();
        }

        if (seekBarThread != null) {
            seekBarThread.interrupt();
        }

        // Music Uri 를 통해 Player 초기화
        player = MediaPlayer.create(this, musicUri);
        player.setLooping(false);

        // 화면 세팅
        String duration = milliToSec(player.getDuration());
        textDuration.setText(duration);
        textCurrentTime.setText("00:00");
        // 배경 세팅
        Glide.with(this).load(item.albumUri).apply(bitmapTransform(new BlurTransformation(25))).into(imgAlbum);
        // Title, Artist 세팅
        textTitle.setText(item.title);
        textArtist.setText(item.artist);
        // seekBar 세팅
        seekBar.setMax(player.getDuration());

        seekBarThread = new Thread() {
            @Override
            public void run() {
                if (player != null) {
                    try {
                        while (true) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    seekBar.setProgress(player.getCurrentPosition());
                                    textCurrentTime.setText(milliToSec(player.getCurrentPosition()));
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

    private String milliToSec(int milli) {
        int sec = milli / 1000;
        int min = sec / 60;
        sec = sec % 60;

        return java.lang.String.format("%02d", min) + ":" + java.lang.String.format("%02d", sec);
    }

    private void initView() {
        setContentView(R.layout.activity_player);
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        textTitle = toolbar.findViewById(R.id.textTitle);
        textTitle.setSelected(true);
        textArtist = toolbar.findViewById(R.id.textArtist);
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        controller = (RelativeLayout) findViewById(R.id.controller);


        seekBar = (SeekBar) findViewById(R.id.seekBar);
        textCurrentTime = (TextView) findViewById(R.id.textCurrentTime);
        textDuration = (TextView) findViewById(R.id.textDuration);
        btnPlay = (ImageButton) findViewById(R.id.btnPlay);
        btnFf = (ImageButton) findViewById(R.id.btnFf);
        btnRew = (ImageButton) findViewById(R.id.btnRew);
        btnPrev = (ImageButton) findViewById(R.id.btnPrev);
        btnNext = (ImageButton) findViewById(R.id.btnNext);

        imgAlbum = (ImageView) findViewById(R.id.imgAlbum);

        btnPlay.setOnClickListener(this);
        btnFf.setOnClickListener(this);
        btnRew.setOnClickListener(this);
        btnPrev.setOnClickListener(this);
        btnNext.setOnClickListener(this);
    }

    private void initViewPager() {
        PlayerPageAdapter playerPageAdapter = new PlayerPageAdapter(this, music.getItemList());
        viewPager.setAdapter(playerPageAdapter);
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

        if (current > -1) {
            // 0 page 에서 current page 로 이동을 하는 개념이기 때문에
            viewPager.setCurrentItem(current);
        }
    }

    private void start() {
        playButtonState = Const.STAT_PLAY;
        player.start();
        btnPlay.setImageResource(R.drawable.ic_stop);
    }

    private void pause() {
        playButtonState = Const.STAT_PAUSE;
        player.pause();
        btnPlay.setImageResource(R.drawable.ic_play_arrow);
    }

    @Override
    protected void onDestroy() {
        if (seekBarThread != null) {
            seekBarThread.interrupt();
        }

        if (player != null) {
            player.release();
        }
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnPlay:
                if (playButtonState == Const.STAT_PLAY) {
                    // Play 중이라면
                    pause();
                } else {
                    start();
                }
                break;
            case R.id.btnFf:
                break;
            case R.id.btnRew:
                break;
            case R.id.btnPrev:
                viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
                break;
            case R.id.btnNext:
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

}
