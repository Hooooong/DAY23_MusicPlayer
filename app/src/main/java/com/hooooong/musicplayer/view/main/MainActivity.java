package com.hooooong.musicplayer.view.main;

import android.Manifest;
import android.content.Intent;
import android.media.AudioManager;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;

import com.hooooong.musicplayer.R;
import com.hooooong.musicplayer.data.Const;
import com.hooooong.musicplayer.view.BaseActivity;
import com.hooooong.musicplayer.view.main.adapter.ListPagerAdapter;
import com.hooooong.musicplayer.data.model.Music;
import com.hooooong.musicplayer.view.player.PlayerActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity implements MusicFragment.OnListFragmentInteractionListener{

    private ViewPager viewPager;
    private TabLayout tabLayout;
    private Music music;

    public MainActivity() {
        super(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE});
    }

    @Override
    public void init() {
        setContentView(R.layout.activity_main);

        // 볼룜 조절 버튼으로 미디어 음량만 조절하기 위한 설정
        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        load();
        initView();
        initTabLayout();
        initViewPager();
        initListener();
    }

    private void initListener() {
        // TabLayout 을 ViewPager 에 연결
        tabLayout.addOnTabSelectedListener(
                new TabLayout.ViewPagerOnTabSelectedListener(viewPager));
        // ViewPager 에 변경사항을 TabLayout 에 전달
        viewPager.addOnPageChangeListener(
                new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
    }

    private void initViewPager() {
        MusicFragment fragmentTitle = MusicFragment.newInstance(1);
        MusicFragment fragmentArtist = MusicFragment.newInstance(1);
        MusicFragment fragmentAlbum = MusicFragment.newInstance(1);
        MusicFragment fragmentGenre = MusicFragment.newInstance(1);

        List<Fragment> fragmentList = new ArrayList<>();
        fragmentList.add(fragmentTitle);
        fragmentList.add(fragmentArtist);
        fragmentList.add(fragmentAlbum);
        fragmentList.add(fragmentGenre);

        ListPagerAdapter customAdapter = new ListPagerAdapter(getSupportFragmentManager(), fragmentList);
        viewPager.setAdapter(customAdapter);
    }

    private void initTabLayout() {
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.tab_title)));
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.tab_Artist)));
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.tab_Album)));
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.tab_Genre)));
    }

    private void initView() {
        viewPager = (ViewPager)findViewById(R.id.viewPager);
        tabLayout = (TabLayout)findViewById(R.id.tabLayout);
    }

    private void load() {
        music = Music.getInstance();
        music.load(this);
    }

    @Override
    public List<Music.Item> getList() {
        return music.getItemList();
    }

    @Override
    public void openPlayer(int position) {
        Intent intent = new Intent(MainActivity.this, PlayerActivity.class);
        // putExtra 의 String 값은 상수의 이름이기 때문에
        // class 를 만들어서
        intent.putExtra(Const.KEY_POSITION, position);
        startActivity(intent);
    }
}
