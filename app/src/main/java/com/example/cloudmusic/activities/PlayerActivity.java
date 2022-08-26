package com.example.cloudmusic.activities;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.example.cloudmusic.R;
import com.example.cloudmusic.adapter.viewpager2.ViewPager2Adapter;
import com.example.cloudmusic.base.BaseActivity;
import com.example.cloudmusic.databinding.ActivityPlayerBinding;
import com.example.cloudmusic.entity.MyEvent;
import com.example.cloudmusic.fragment.play.LyricFragment;
import com.example.cloudmusic.fragment.play.SongFragment;
import com.example.cloudmusic.request.activity.RequestPlayViewModel;
import com.example.cloudmusic.state.activity.StatePlayerViewModel;
import com.example.cloudmusic.utils.CloudMusic;
import com.example.cloudmusic.utils.callback.FragmentMsgCallback;
import com.google.android.material.tabs.TabLayoutMediator;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.glide.transformations.BlurTransformation;

public class PlayerActivity extends BaseActivity implements FragmentMsgCallback {

    private ActivityPlayerBinding binding;
    private StatePlayerViewModel svm;
    private RequestPlayViewModel rvm;
    private List<Fragment> fragmentList;

    @Override
    protected void initActivity() {
        setTransparentStatusBar(false);
        svm = new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(StatePlayerViewModel.class);
        rvm = new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(RequestPlayViewModel.class);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_player);
        binding.setClick(new ClickClass());
        binding.setSvm(svm);
        binding.setLifecycleOwner(this);//数据绑定
    }

    @Override
    protected void initView() {
        initViewPager2();
    }

    @Override
    protected void initSomeData() {
        EventBus.getDefault().register(this);
        rvm.getCurrentSong();
    }

    @Override
    protected void observerDataStateUpdateAction() {
        rvm.song.observe(this, song -> {
            svm.song.setValue(song);
            if (song.getName().equals("暂无播放")) {
                Glide.with(PlayerActivity.this)
                        .load(R.drawable.pic_cd)
                        // 设置高斯模糊
                        // "25"	模糊度，取值范围1~25
                        // "3"	图片缩放3倍后再进行模糊，一般缩放3-5倍
                        .transform(new CenterCrop(), new BlurTransformation(25, 5))
                        // 将得到的模糊图像替换原来的背景
                        .into(binding.playBgIV);
            } else {
                Glide.with(PlayerActivity.this)
                        .load(song.getPicUrl())
                        // 设置高斯模糊
                        // "25"	模糊度，取值范围1~25
                        // "3"	图片缩放3倍后再进行模糊，一般缩放3-5倍
                        .transform(new CenterCrop(), new BlurTransformation(25, 5))
                        // 将得到的模糊图像替换原来的背景
                        .into(binding.playBgIV);
            }
        });
    }

    private void initViewPager2() {
        fragmentList = new ArrayList<>();
        List<String> titles = new ArrayList<>();
        titles.add("歌曲");
        titles.add("歌词");
        fragmentList.add(new SongFragment(this));
        fragmentList.add(new LyricFragment(this));
        ViewPager2Adapter adapter = new ViewPager2Adapter(PlayerActivity.this, fragmentList);
        binding.playViewPager2.setAdapter(adapter);
        //去除尽头阴影
        View childAt = binding.playViewPager2.getChildAt(0);
        if (childAt instanceof RecyclerView) {
            childAt.setOverScrollMode(View.OVER_SCROLL_NEVER);
        }
        //TableLayout对接
        new TabLayoutMediator(binding.playTablelayout, binding.playViewPager2, (tab, position) -> tab.setText(titles.get(position))).attach();
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        CloudMusic.isStartPlayerActivity = false;
        super.onDestroy();
    }

    @Override
    public void transferData(MyEvent myEvent,int fragmentIndex) {
            if(fragmentIndex==0){
                Fragment fragment1=fragmentList.get(1);
                ((LyricFragment)fragment1).setPlayBtn(myEvent.isPlaying(),myEvent.isRemoveSong());
            }else {
                Fragment fragment0=fragmentList.get(0);
                ((SongFragment)fragment0).transferData(myEvent.isPlaying());
            }
    }

    public class ClickClass {

        /**
         * 返回
         *
         * @param view
         */
        public void back(View view) {
            finish();
        }


    }

    /**
     * 接收EventBus数据
     *
     * @param myEvent
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(MyEvent myEvent) {
        int msg = myEvent.getMsg();
        switch (msg) {
            case 3://开始播放
                if(svm.song.getValue()!=null)
                    rvm.song.setValue(svm.song.getValue());
                break;
            case 7:
                Toast toast = Toast.makeText(PlayerActivity.this, "\n网络繁忙中\n", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                break;

            case 4:
                svm.song.setValue(myEvent.getSong());

        }
    }

}
