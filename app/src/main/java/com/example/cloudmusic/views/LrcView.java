package com.example.cloudmusic.views;

import static androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_DRAGGING;
import static androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_IDLE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cloudmusic.R;
import com.example.cloudmusic.adapter.recyclerview.LrcAdapter;
import com.example.cloudmusic.entity.Lyric;
import com.example.cloudmusic.CloudMusic;
import com.example.cloudmusic.utils.callback.LrcClickCallback;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class LrcView extends RelativeLayout {

    @SuppressLint("StaticFieldLeak")
    private static Context context;
    private static RecyclerView recyclerView;
    private final List<Lyric> lyricList = new ArrayList<>();
    private LrcClickCallback lrcClickCallback;
    private LrcAdapter adapter;
    private View view;
    private int top;
    private int scrollState = SCROLL_STATE_IDLE;
    private int delayScrollState = SCROLL_STATE_DRAGGING;//松手2s后滚动


    public LrcView(Context context) {
        super(context, null);
        if (LrcView.context == null)
            LrcView.context = context;
        if (view == null)
            initView();//导入布局
    }

    public LrcView(Context context, AttributeSet attrs) {
        super(context, attrs, -1);
        if (LrcView.context == null)
            LrcView.context = context;
        if (view == null)
            initView();//导入布局
    }

    public LrcView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (LrcView.context == null)
            LrcView.context = context;
        if (view == null)
            initView();//导入布局
    }


    private void initView() {
        view = View.inflate(getContext(), R.layout.my_lrc_view, null);
        addView(view);
        recyclerView = view.findViewById(R.id.rv);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == SCROLL_STATE_IDLE && scrollState == SCROLL_STATE_DRAGGING) {
                    delayScrollState=newState;
                    TimerTask task = new TimerTask() {
                        @Override
                        public void run() {
                            scrollState=delayScrollState;
                            delayScrollState = SCROLL_STATE_DRAGGING;
                        }
                    };
                    Timer timer = new Timer();
                    timer.schedule(task, 2000);//2秒后执行TimeTask的run方法
                } else {
                    scrollState = newState;
                }
                Log.d("TEST", "scrollState:" + scrollState);
            }
        });
    }

    /**
     * 更新要选中歌词
     *
     * @param time
     */
    public void updateCurrentLrc(int time) {
        if (adapter == null || CloudMusic.isReset || CloudMusic.isGettingSongUrl) return;
        int firstPos = recyclerView.getChildLayoutPosition(recyclerView.getChildAt(0));
        //  Log.d("TEST", "firstPos:" + firstPos);
        if (firstPos == -1) return;
        for (int i = lyricList.size() - 1; i >= 0; i--) {
            //CloudMusic.lrcOffset歌词偏移
            if (lyricList.get(i).getTime() - CloudMusic.lrcOffset <= time) {
                int pos = lyricList.get(i).getPosition();
                // Log.d("TEST", "pos:" + pos);
                int movePos = pos - firstPos;
                //Log.d("TEST", "movePos:" + movePos);
                //   Log.d("TEST", "lyricList.size()-1:" + (lyricList.size() - 1));
                adapter.updateCurrentLrc(pos);
                if (scrollState != SCROLL_STATE_IDLE) return;
                if (movePos - 3 < 0) {
                    recyclerView.smoothScrollToPosition(pos - 3 > lyricList.size() - 1 ? pos : pos - 3);
                } else {
                    //获取当前item与顶部距离
                    if (recyclerView.getChildAt(movePos - 3) == null) {
                        recyclerView.smoothScrollToPosition(pos - 3 > lyricList.size() - 1 ? pos : pos - 3);
                    } else {
                        top = recyclerView.getChildAt(movePos - 3).getTop();
                        //   Log.d("TEST", "top:" + top);
                        recyclerView.smoothScrollBy(0, top);
                    }
                }
                break;
            }
        }
    }

    /**
     * 设置歌词
     */
    public void setLrc(String lrc) {
        parseLrcString(lrc);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        adapter = new LrcAdapter(lyricList);
        if (lrcClickCallback != null)
            adapter.setLrcClickCallback(lrcClickCallback);
        recyclerView.setAdapter(adapter);
    }


    public void setLrcClickCallback(LrcClickCallback lrcClickCallback) {
        this.lrcClickCallback = lrcClickCallback;
    }

    /**
     * 处理歌词
     *
     * @param lrc
     */
    private void parseLrcString(String lrc) {
        lyricList.clear();
        String blankStr = " ";
        Lyric lyric = new Lyric();
        lyric.setSentence(blankStr);
        lyric.setTime(0);
        lyric.setPosition(0);
        lyricList.add(lyric);
        lyric.setTime(1);
        lyric.setPosition(1);
        lyricList.add(lyric);
        lyric.setTime(2);
        lyric.setPosition(2);
        lyricList.add(lyric);
        int pos = 3;
        String[] lrcs = lrc.split("\n");
        for (String s : lrcs) {
            String[] line = new String[2];
            if (s.length() > 11) line = s.split("]");
            else line[0] = s.replace("]", "");
            String timeString = line[0].replace("[", "");
            int min = Integer.parseInt(timeString.split(":")[0]) * 60 * 1000;
            int sec = Integer.parseInt(timeString.split(":")[1].split("\\.")[0]) * 1000 + Integer.parseInt(timeString.split(":")[1].split("\\.")[1]);
            int time = min + sec;
            String sentence;
            if (line.length > 1 && line[1] != null) {
                sentence = line[1];
                Lyric lyric2 = new Lyric();
                lyric2.setTime(time);
                lyric2.setSentence(sentence);
                lyric2.setPosition(pos);
                lyricList.add(lyric2);
                pos++;
            }

        }
    }

    /**
     * 设定RecyclerView最大滑动速度
     * @param recyclerview
     * @param velocity
     */
    private void setMaxFlingVelocity(RecyclerView recyclerview, int velocity) {
        try{
            Field field = recyclerview.getClass().getDeclaredField("mMaxFlingVelocity");
            field.setAccessible(true);
            field.set(recyclerview, velocity);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
