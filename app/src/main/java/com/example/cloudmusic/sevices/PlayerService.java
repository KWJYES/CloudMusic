package com.example.cloudmusic.sevices;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.example.cloudmusic.activities.MainActivity;
import com.example.cloudmusic.entity.MyEvent;
import com.example.cloudmusic.entity.Song;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;

public class PlayerService extends Service {

    private MediaPlayer mediaPlayer;
    boolean isStopThread;//是否停止线程
    boolean finishing=false;//是否正在结束上一首的播放,上一首在结束时，可能已经开了下一首的发送线程，但发送的是正在结束的进度信息，会导致出现多次发送“播放完成”

    private NotificationManager manager;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("TAG", "PlayerService onCreate()");
        createNotificationChannel();//创建通知频道
        createNotification();//创建前台通知
    }

    public PlayerService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        PlayerBinder playerBinder=new PlayerBinder();
        Log.d("TAG", "PlayerService onBind()");
        mediaPlayer = new MediaPlayer();//创建播放器
        mediaPlayer.setOnPreparedListener(mediaPlayer -> {
            int duration = mediaPlayer.getDuration();
            MyEvent myEvent = new MyEvent();
            myEvent.setDuration(duration);
            myEvent.setMsg(0);//准备播放完成
            EventBus.getDefault().post(myEvent);
            Log.d("TAG", "EventBus 播放准备就绪");
            playerBinder.start();

        });
        return playerBinder;
    }


    public class PlayerBinder extends Binder implements IPlay {
        @Override
        public void play(Song song) {
            try {
                reset();//关原来的发送消息线程
                mediaPlayer.setDataSource(song.getUrl());
                mediaPlayer.setLooping(false);//是否循环播放
                mediaPlayer.prepareAsync();//异步
                createNotification();//创建前台通知
                Log.d("TAG",song.getName()+" play() prepareAsync()...");
            } catch (IOException e) {
                Log.d("TAG", "PlayerService 音乐url设置异常");
                Log.d("TAG", "url:" + song.getUrl());
                mediaPlayer.release();
                mediaPlayer = null;
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setOnPreparedListener(mediaPlayer -> {
                    int duration = mediaPlayer.getDuration();
                    MyEvent myEvent = new MyEvent();
                    myEvent.setDuration(duration);
                    myEvent.setMsg(0);//准备播放
                    EventBus.getDefault().post(myEvent);
                    Log.d("TAG", "EventBus 播放准备就绪");
                    start();
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
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
            }
        }

        @Override
        public void start() {
            if (mediaPlayer == null) return;
            if (!mediaPlayer.isPlaying()) {
                mediaPlayer.start();
                createNotification();//创建前台通知
                MyEvent myEvent=new MyEvent();
                myEvent.setMsg(3);//开始播放
                EventBus.getDefault().post(myEvent);
            }
        }

        @Override
        public void seekToPosition(int position) {
            if (mediaPlayer == null) return;
            mediaPlayer.seekTo(position);
        }

        @Override
        public boolean isPlaying() {
            if (mediaPlayer == null) return false;
            return mediaPlayer.isPlaying();
        }

        @Override
        public void reset() {
            //播放结束关发送消息线程
            if (mediaPlayer == null) return;
            isStopThread = true;
//            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setOnPreparedListener(mediaPlayer -> {
                int duration = mediaPlayer.getDuration();
                MyEvent myEvent = new MyEvent();
                myEvent.setDuration(duration);
                myEvent.setMsg(0);//准备播放
                EventBus.getDefault().post(myEvent);
                Log.d("TAG", "EventBus 播放准备就绪");
                start();
            });
        }

        /**
         * 更新进度条
         */
        private void updateSeekBar() {
            new Thread(() -> {
                while (!isStopThread) {
                    try {
                        Thread.sleep(1000);
                        MyEvent myEvent = new MyEvent();
                        int currentPosition=mediaPlayer.getCurrentPosition();
                        myEvent.setCurrentPosition(currentPosition);
                        int duration=mediaPlayer.getDuration();
                        myEvent.setDuration(duration);
                        myEvent.setMsg(1);//当前进度
                        //发送数据给activity fragment, 使用EventBus实现
                        EventBus.getDefault().post(myEvent);
                        if(currentPosition<duration-500){
                            finishing=false;
                        }
                        //Log.d("TAG",currentPosition+" "+duration);
                        if(currentPosition>=duration-500&&!finishing&&duration<72000000){
                            finishing=true;
                            MyEvent myEvent2 = new MyEvent();
                            myEvent2.setMsg(2);//播放结束
                            EventBus.getDefault().post(myEvent2);
                            reset();
                            Log.d("TAG", "EventBus 播放结束");
                            break;
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }


    /**
     * 创建前台通知
     */
    private void createNotification() {
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);//暂时没搞懂
        Notification notification = new NotificationCompat.Builder(this, "channel_id")
                .setContentTitle("标题")
                .setContentText("内容")
                .setWhen(System.currentTimeMillis())
                //.setSmallIcon(R.mipmap.ic_launcher)
                //.setLargeIcon(BitmapFactory.decodeResource(getResources(),R.mipmap.ic_launcher))
                .setContentIntent(pendingIntent)//设置PendingIntent
                .build();
        startForeground(1, notification);//显示在下拉栏中，0为不显示
    }


    /**
     * 创建通知频道
     */
    private void createNotificationChannel() {
        //Android8.0(API26)以上需要
        int importance = NotificationManager.IMPORTANCE_DEFAULT;//重要程度
        NotificationChannel channel = new NotificationChannel("channel_id", "channel_name", importance);
        channel.setDescription("description");
        manager = (NotificationManager)
                getSystemService(Context.NOTIFICATION_SERVICE);//获取NotificationManager实例
        manager.createNotificationChannel(channel);//设置频道
    }

    @Override
    public boolean onUnbind(Intent intent) {
        isStopThread = true;
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        mediaPlayer = null;
        Log.d("TAG", "PlayerService onUnbind()...");
        return super.onUnbind(intent);
    }
}