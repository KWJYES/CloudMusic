package com.example.cloudmusic.activities;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;


import com.example.cloudmusic.R;
import com.example.cloudmusic.base.BaseActivity;
import com.example.cloudmusic.databinding.ActivitySearchBinding;
import com.example.cloudmusic.entity.SearchWord;
import com.example.cloudmusic.fragment.search.SearchedFragment;
import com.example.cloudmusic.fragment.search.SearchingFragment;
import com.example.cloudmusic.request.RequestSearchViewModel;
import com.example.cloudmusic.state.StateSearchViewModel;
import com.example.cloudmusic.utils.CloudMusic;

import java.util.List;

public class SearchActivity extends BaseActivity {

    ActivitySearchBinding binding;
    StateSearchViewModel svm;
    RequestSearchViewModel rvm;

    @Override
    protected void initActivity() {
        setTransparentStatusBar(true);
        overridePendingTransition(R.anim.activity_in_bottom_alpha,R.anim.activity_null);
        svm = new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(StateSearchViewModel.class);
        rvm = new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(RequestSearchViewModel.class);
        binding= DataBindingUtil.setContentView(this, R.layout.activity_search);
        binding.setSvm(svm);
        binding.setClick(new ClickClass());
        binding.setLifecycleOwner(this);//双向绑定

    }

    @Override
    protected void initView() {
        svm.defaultSearchWord.setValue(getIntent().getStringExtra("defaultSearchWord"));
        svm.searchWord.setValue("");
        replaceFragment(new SearchingFragment(null));
        binding.searchET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                replaceFragment(new SearchingFragment(svm.hotList.getValue()));
            }
        });
    }

    @Override
    protected void observerDataStateUpdateAction() {
        rvm.hotList.observe(this, searchWordList -> {
            svm.hotList.setValue(searchWordList);
            replaceFragment(new SearchingFragment(searchWordList));
        });
        rvm.hotListState.observe(this, s -> {
            if(s.equals(CloudMusic.FAILURE))
                Toast.makeText(SearchActivity.this, "数据加载失败可尝试刷新", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    protected void initSomeData() {
        rvm.requestHotList();
    }

    /**
     *更新Fragment
     * @param fragment
     */
    public void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager=getSupportFragmentManager();//通过getSupportFragmentManager()获得FragmentManager对象
        FragmentTransaction transaction=fragmentManager.beginTransaction();//通过调用beginTransaction()方法开启一个事务
        transaction.replace(R.id.searchFragment,fragment);//向容器添加或替换碎片，一般用replace()方法实现，需要传入容器的id和待添加的碎片实例
        transaction.commit();//调用commit()方法来提交事务
    }

    public class ClickClass{
        public void back(View view){
            finish();
        }

        public void search(View view){
            if(svm.searchWord.getValue().equals("")){
                replaceFragment(new SearchedFragment(svm.defaultSearchWord.getValue()));//默认搜索
            }else {
                replaceFragment(new SearchedFragment(svm.searchWord.getValue()));//搜索
            }
        }

    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.activity_null,R.anim.activity_out_bottom_alpha);
    }
}