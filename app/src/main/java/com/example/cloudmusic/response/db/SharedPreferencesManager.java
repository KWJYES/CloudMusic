package com.example.cloudmusic.response.db;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.lifecycle.MutableLiveData;

import com.example.cloudmusic.entity.Song;

import java.util.ArrayList;
import java.util.List;

public class SharedPreferencesManager implements ISharedPreferencesRequest {

    /**
     * 使用单例模式
     */
    private SharedPreferencesManager(){}
    private static SharedPreferencesManager sharedPreferencesManager;
    public static SharedPreferencesManager getInstance(){
        if(sharedPreferencesManager==null){
            synchronized (SharedPreferencesManager.class){
                if(null==sharedPreferencesManager){
                    sharedPreferencesManager=new SharedPreferencesManager();
                }
            }
        }
        return sharedPreferencesManager;
    }

    private SharedPreferences.Editor editor;
    private SharedPreferences.Editor getEditor(Context context){
        if(editor==null){
            synchronized (SharedPreferences.Editor.class){
                if(null==editor){
                    editor=context.getSharedPreferences("data",MODE_PRIVATE).edit();
                }
            }
        }
        return editor;
    }

    private SharedPreferences pref;
    private SharedPreferences getPref(Context context){
        if(pref==null){
            synchronized (SharedPreferences.class){
                if(null==pref){
                    pref=context.getSharedPreferences("data",MODE_PRIVATE);
                }
            }
        }
        return pref;
    }

    /**
     * 使用状态，是否第一次打开app
     * @param context
     * @return
     */
    @Override
    public void getUseState(Context context,MutableLiveData<Boolean> useState){
        useState.setValue(getPref(context).getBoolean("login_state",false));
    }

    @Override
    public void applyUseState(Context context, boolean isLogin, MutableLiveData<Boolean> loginState){
        getEditor(context).putBoolean("login_state",isLogin);
        getEditor(context).apply();
        loginState.setValue(isLogin);
    }

    /**
     * 当前用户账号
     * @param context
     * @return
     */
    @Override
    public String getAccount(Context context){
        return getPref(context).getString("account","");
    }

    @Override
    public void applyAccount(Context context,String account){
        getEditor(context).putString("account",account);
        getEditor(context).apply();
    }
}
