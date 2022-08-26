package com.example.cloudmusic.utils.callback;

import com.example.cloudmusic.entity.MyEvent;

public interface FragmentMsgCallback {
    void transferData(MyEvent myEvent,int fragmentIndex);
}
