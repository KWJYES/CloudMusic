package com.example.cloudmusic.sevices;

import static android.app.PendingIntent.FLAG_IMMUTABLE;
import static android.app.PendingIntent.FLAG_MUTABLE;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.NotificationTarget;
import com.example.cloudmusic.R;
import com.example.cloudmusic.activities.MainActivity;
import com.example.cloudmusic.entity.MyEvent;
import com.example.cloudmusic.entity.Song;
import com.example.cloudmusic.response.media.MediaManager;
import com.example.cloudmusic.utils.CloudMusic;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;

public class PlayerService extends Service {

    private MediaPlayer mediaPlayer;
    boolean isStopThread;//是否停止线程
    boolean finishing = false;//是否正在结束上一首的播放,上一首在结束时，可能已经开了下一首的发送线程，但发送的是正在结束的进度信息，会导致出现多次发送“播放完成”
    boolean isPrepared = false;//避免错误的状态传入
    private RemoteViews contentView;
    private Song currentSong = new Song("0", "暂无播放", "歌手未知");
    ;
    private IntentFilter intentFilter;
    private NotificationClickReceiver notificationClickReceiver;

    private NotificationManager manager;
    private Notification notification;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("TAG", "PlayerService onCreate()");
        intentFilter = new IntentFilter();
        intentFilter.addAction("stop");
        intentFilter.addAction("next");
        intentFilter.addAction("last");
        intentFilter.addAction("notification_cancelled");
        notificationClickReceiver = new NotificationClickReceiver();
        registerReceiver(notificationClickReceiver, intentFilter);
        createNotificationChannel();//创建通知频道
        createNotification();//创建前台通知
    }

    public PlayerService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        PlayerBinder playerBinder = new PlayerBinder();
        Log.d("TAG", "PlayerService onBind()");
        mediaPlayer = new MediaPlayer();//创建播放器
        mediaPlayer.release();
        mediaPlayer = null;
        mediaPlayer = new MediaPlayer();//创建播放器
        mediaPlayer.setOnPreparedListener(mediaPlayer -> {
            isPrepared = true;
            playerBinder.start();
            int duration = mediaPlayer.getDuration();
            MyEvent myEvent = new MyEvent();
            myEvent.setDuration(duration);
            myEvent.setMsg(0);//准备播放完成
            EventBus.getDefault().post(myEvent);
            Log.d("TAG", "EventBus 播放准备就绪 开始播放");
        });
        return playerBinder;
    }


    public class PlayerBinder extends Binder implements IPlay {
        @Override
        public void play(Song song) {
            try {
                reset();//关原来的发送消息线程
                mediaPlayer.setDataSource(song.getUrl());
                Log.d("TAG", "play song:" + song.getName());
                mediaPlayer.setLooping(false);//是否循环播放
                currentSong = song;
                MyEvent myEvent = new MyEvent();
                myEvent.setMsg(4);
                myEvent.setSong(song);
                EventBus.getDefault().post(myEvent);
                createNotification();//创建前台通知
                mediaPlayer.prepareAsync();//异步
                Log.d("TAG", song.getName() + " play() prepareAsync()...");
            } catch (IOException e) {
                Log.d("TAG", "PlayerService 音乐url设置异常");
                Log.d("TAG", "url:" + song.getUrl());
                isPrepared = false;
                if (mediaPlayer != null && mediaPlayer.isPlaying())
                    mediaPlayer.stop();
                if (mediaPlayer != null)
                    mediaPlayer.release();
                mediaPlayer = null;
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setOnPreparedListener(mediaPlayer -> {
                    isPrepared = true;
                    start();
                    int duration = mediaPlayer.getDuration();
                    MyEvent myEvent = new MyEvent();
                    myEvent.setDuration(duration);
                    myEvent.setMsg(0);//准备播放
                    EventBus.getDefault().post(myEvent);
                    Log.d("TAG", "EventBus 播放准备就绪 开始播放");
                });
                try {
                    mediaPlayer.setDataSource(song.getUrl());
                } catch (IOException ioException) {
                    Log.d("TAG", "PlayerService 重置mediaPlayer后 音乐url设置异常");
                    ioException.printStackTrace();
                }
                e.printStackTrace();
            }
            //重开发送消息线程
            isStopThread = false;
            updateSeekBar();
        }

        @Override
        public void pause() {
            if (mediaPlayer == null) return;
            if (mediaPlayer.isPlaying() && isPrepared) {
                mediaPlayer.pause();
                createNotification();//创建前台通知
            }
        }

        @Override
        public void start() {
            if (mediaPlayer == null) return;
            if (!mediaPlayer.isPlaying() && isPrepared) {
                mediaPlayer.start();
                createNotification();//创建前台通知
                MyEvent myEvent = new MyEvent();
                myEvent.setSong(currentSong);
                myEvent.setMsg(3);//开始播放
                EventBus.getDefault().post(myEvent);
            }
        }

        @Override
        public void seekToPosition(int position) {
            if (mediaPlayer == null || !isPrepared) return;
            mediaPlayer.seekTo(position);
        }

        @Override
        public boolean isPlaying() {
            if (mediaPlayer == null || CloudMusic.isReset || CloudMusic.isGettingSongUrl || !isPrepared)
                return false;
            return mediaPlayer.isPlaying();
        }

        @Override
        public void reset() {
            //播放结束关发送消息线程
            if (mediaPlayer == null) return;
            isPrepared = false;
            isStopThread = true;
            CloudMusic.isReset = true;
            mediaPlayer.reset();
            currentSong = new Song("0", "暂无播放", "歌手未知");
            CloudMusic.isReset = false;
        }

        /**
         * 更新进度条
         */
        private void updateSeekBar() {
            new Thread(() -> {
                Log.d("TAG", "CurrentPosition发送线程启动");
                while (!isStopThread) {
                    try {
                        Thread.sleep(500);
                        if (isStopThread) break;
                        if (!CloudMusic.isReset && isPrepared) {
                            MyEvent myEvent = new MyEvent();
                            int currentPosition = mediaPlayer.getCurrentPosition();
                            myEvent.setCurrentPosition(currentPosition);
                            int duration = mediaPlayer.getDuration();
                            myEvent.setDuration(duration);
                            myEvent.setMsg(1);//当前进度
                            //发送数据给activity fragment, 使用EventBus实现
                            EventBus.getDefault().post(myEvent);
                            if (currentPosition < duration - 500) {
                                finishing = false;
                            }
                            //Log.d("TAG",currentPosition+" "+duration);
                            if (currentPosition >= duration - 500 && !finishing && duration < 72000000 && duration > 0) {
                                finishing = true;
                                MyEvent myEvent2 = new MyEvent();
                                myEvent2.setMsg(2);//播放结束
                                EventBus.getDefault().post(myEvent2);
                                reset();
                                Log.d("TAG", currentPosition + " " + duration);
                                Log.d("TAG", "EventBus 播放结束");
                                break;
                            }
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                Log.d("TAG", "CurrentPosition发送线程关闭");
            }).start();
        }
    }


    /**
     * 创建前台通知
     */
    private void createNotification() {
        initRemoteView();
        notification = new NotificationCompat.Builder(getApplicationContext(), "云听通知频道id")
                .setCustomContentView(contentView)
                .setCustomBigContentView(contentView)
                .setShowWhen(false)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setSmallIcon(R.drawable.ic_cloudmusic_logo)
                .build();
        startForeground(1, notification);//显示在下拉栏中，0为不显示
        NotificationTarget notificationTarget = new NotificationTarget(getApplicationContext(), R.id.bgmmMusicImageView, contentView, notification, 1);
        if (currentSong.getPicUrl() != null)
            Glide.with(getApplicationContext()).asBitmap().placeholder(R.drawable.ic_cd_default).load(currentSong.getPicUrl()).override(120,120).into(notificationTarget);
        if(currentSong.getSongId().startsWith("000"))
            Glide.with(getApplicationContext()).asBitmap().load(R.drawable.ic_cd_default).override(120,120).into(notificationTarget);
    }

    private void initRemoteView() {
        contentView = new RemoteViews(getPackageName(), R.layout.notify_layout);
        contentView.setTextViewText(R.id.musicTitleTextView, currentSong.getName() + " - " + currentSong.getArtist());
        contentView.setImageViewResource(R.id.audio_close_btn, R.drawable.ic_notify_close);
        contentView.setImageViewResource(R.id.lastImageView, R.drawable.ic_last_song3);
        contentView.setImageViewResource(R.id.nextImageView, R.drawable.ic_next_song3);
        contentView.setImageViewResource(R.id.stopImageView, R.drawable.ic_notify_play);

        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            contentView.setImageViewResource(R.id.stopImageView, R.drawable.ic_notify_pause);
        } else {
            contentView.setImageViewResource(R.id.stopImageView, R.drawable.ic_notify_play);
        }
        //跳转MainActivity
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, FLAG_IMMUTABLE);//暂时没搞懂
        contentView.setOnClickPendingIntent(R.id.bgmmMusicImageView, pendingIntent);
        // 实现停止/播放
        Intent intentStop = new Intent("stop");
        PendingIntent pIntentStop = PendingIntent.getBroadcast(getApplicationContext(), 0, intentStop, FLAG_IMMUTABLE);
        contentView.setOnClickPendingIntent(R.id.stopImageView, pIntentStop);
        //下一首事件
        Intent intentNext = new Intent("next");//发送播放下一曲的通知
        PendingIntent pIntentNext = PendingIntent.getBroadcast(getApplicationContext(), 0, intentNext, FLAG_IMMUTABLE);
        contentView.setOnClickPendingIntent(R.id.nextImageView, pIntentNext);
        //上一首事件
        Intent intentLast = new Intent("last");//发送播放上一曲的通知
        PendingIntent pIntentLast = PendingIntent.getBroadcast(getApplicationContext(), 0, intentLast, FLAG_IMMUTABLE);
        contentView.setOnClickPendingIntent(R.id.lastImageView, pIntentLast);
        // 关闭通知栏
        Intent intentCancelled = new Intent("notification_cancelled");
        PendingIntent pIntentCancelled = PendingIntent.getBroadcast(getApplicationContext(), 0, intentCancelled, FLAG_IMMUTABLE);
        contentView.setOnClickPendingIntent(R.id.audio_close_btn, pIntentCancelled);
    }

    /**
     * 创建通知频道
     */
    private void createNotificationChannel() {
        //Android8.0(API26)以上需要
        int importance = NotificationManager.IMPORTANCE_HIGH;//重要程度
        NotificationChannel channel = new NotificationChannel("云听通知频道id", "云听通知频道name", importance);
        channel.setDescription("云听通知频道");
        manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);//获取NotificationManager实例
        manager.createNotificationChannel(channel);//设置频道
    }

    class NotificationClickReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            MyEvent myEvent=new MyEvent();
            myEvent.setMsg(10);
            if (action.equals("stop")) {
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    contentView.setImageViewResource(R.id.stopImageView, R.drawable.ic_notify_pause);
                } else {
                    contentView.setImageViewResource(R.id.stopImageView, R.drawable.ic_notify_play);
                }
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    MediaManager.getInstance().pause(null);
                    myEvent.setPlaying(false);
                } else {
                    MediaManager.getInstance().start(null);
                    myEvent.setPlaying(true);
                }
                EventBus.getDefault().post(myEvent);
            }
            if (action.equals("next")) {
                MediaManager.getInstance().nextSong(null, null);

            }
            if (action.equals("last")) {
                MediaManager.getInstance().lastSong(null, null);
            }

            // 关闭通知栏
            if (action.equals("notification_cancelled")) {
                stopForeground(true);
                if (mediaPlayer == null) return;
                if (mediaPlayer.isPlaying() && isPrepared) {
                    mediaPlayer.pause();
                    myEvent.setPlaying(false);
                    EventBus.getDefault().post(myEvent);
                }
            }
        }
    }

    @Override
    public boolean onUnbind(Intent intent) {
        unregisterReceiver(notificationClickReceiver);
        isStopThread = true;
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.reset();
        }
        if (mediaPlayer != null)
            mediaPlayer.release();
        mediaPlayer = null;
        Log.d("TAG", "PlayerService onUnbind()...");
        return super.onUnbind(intent);
    }
}