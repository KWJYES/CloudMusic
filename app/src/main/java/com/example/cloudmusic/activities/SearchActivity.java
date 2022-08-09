package com.example.cloudmusic.activities;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
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
import com.example.cloudmusic.entity.HistorySearch;
import com.example.cloudmusic.entity.Song;
import com.example.cloudmusic.fragment.search.SearchedFragment;
import com.example.cloudmusic.fragment.search.SearchingFragment;
import com.example.cloudmusic.request.activity.RequestSearchViewModel;
import com.example.cloudmusic.state.activity.StateSearchViewModel;
import com.example.cloudmusic.utils.CloudMusic;
import com.example.cloudmusic.utils.callback.HistorySearchItemClickCallback;
import com.example.cloudmusic.utils.callback.SongListItemOnClickCallback;
import com.example.cloudmusic.utils.callback.SongListItemRemoveCallback;
import com.example.cloudmusic.views.DragFloatActionButton;
import com.example.cloudmusic.views.MusicListDialog;

import java.util.Objects;

public class SearchActivity extends BaseActivity {

    ActivitySearchBinding binding;
    StateSearchViewModel svm;
    RequestSearchViewModel rvm;
    private Fragment currentFragment;

    @Override
    protected void initActivity() {
        setTransparentStatusBar(true);
        overridePendingTransition(R.anim.activity_in_bottom_alpha, R.anim.activity_null);
        svm = new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(StateSearchViewModel.class);
        rvm = new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(RequestSearchViewModel.class);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_search);
        binding.setSvm(svm);
        binding.setClick(new ClickClass());
        binding.setLifecycleOwner(this);//双向绑定

    }

    @Override
    protected void initView() {
        svm.defaultSearchWord.setValue(getIntent().getStringExtra("defaultSearchWord"));
        svm.searchWord.setValue("");
        replaceFragment(new SearchingFragment(null, searchWord -> {
            replaceFragment(new SearchedFragment(searchWord.getSearchWord()));
            svm.searchWord.setValue(searchWord.getSearchWord());
        }, historySearch -> {
            replaceFragment(new SearchedFragment(historySearch.getKeywords()));
            svm.searchWord.setValue(historySearch.getKeywords());
        }));
        binding.searchET.setOnEditorActionListener((textView, i, keyEvent) -> {
            getSearch();
            return false;
        });
        binding.searchET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                Fragment fragment = new SearchingFragment(svm.hotList.getValue(), searchWord -> {
                    replaceFragment(new SearchedFragment(searchWord.getSearchWord()));
                    svm.searchWord.setValue(searchWord.getSearchWord());
                }, historySearch -> {
                    replaceFragment(new SearchedFragment(historySearch.getKeywords()));
                    svm.searchWord.setValue(historySearch.getKeywords());
                });
                if (editable.toString().equals("") && !currentFragment.getClass().equals(fragment.getClass())) {
                    replaceFragment(fragment);
                }
            }
        });
        binding.dragFloatActionButton.setOnClickListener(view -> {
            if (CloudMusic.isStartMusicListDialog) return;
            SongListItemOnClickCallback songListItemOnClickCallback = song -> rvm.play(song);
            SongListItemRemoveCallback removeCallback = song -> rvm.remove(song);
            MusicListDialog dialog = new MusicListDialog(SearchActivity.this, R.style.Base_ThemeOverlay_AppCompat_Dialog, songListItemOnClickCallback, removeCallback);
            dialog.show();
        });
    }

    @Override
    protected void observerDataStateUpdateAction() {
        rvm.hotList.observe(this, searchWordList -> {
            svm.hotList.setValue(searchWordList);
            Fragment fragment = new SearchingFragment(svm.hotList.getValue(), searchWord -> {
                replaceFragment(new SearchedFragment(searchWord.getSearchWord()));
                svm.searchWord.setValue(searchWord.getSearchWord());
            }, historySearch -> {
                replaceFragment(new SearchedFragment(historySearch.getKeywords()));
                svm.searchWord.setValue(historySearch.getKeywords());
            });
            if (currentFragment.getClass().equals(fragment.getClass())) {
                replaceFragment(fragment);
            }
        });
        rvm.hotListState.observe(this, s -> {
            if (s.equals(CloudMusic.FAILURE))
                Toast.makeText(SearchActivity.this, "数据加载失败可尝试刷新", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    protected void initSomeData() {

    }

    @Override
    protected void getInternetData() {
        getHotList();
    }

    /**
     * 获取热搜列表
     */
    private void getHotList() {
        if (svm.hotList.getValue() == null || svm.hotList.getValue().size() == 0) {
            rvm.requestHotList();
        }
    }

    /**
     * 更新Fragment
     *
     * @param fragment
     */
    public void replaceFragment(Fragment fragment) {
        currentFragment = fragment;
        FragmentManager fragmentManager = getSupportFragmentManager();//通过getSupportFragmentManager()获得FragmentManager对象
        FragmentTransaction transaction = fragmentManager.beginTransaction();//通过调用beginTransaction()方法开启一个事务
        transaction.replace(R.id.searchFragment, fragment);//向容器添加或替换碎片，一般用replace()方法实现，需要传入容器的id和待添加的碎片实例
        transaction.commit();//调用commit()方法来提交事务
    }

    private void getSearch(){
        Fragment fragment;
        if (Objects.equals(svm.searchWord.getValue(), "")) {
            String keywords=Objects.requireNonNull(svm.defaultSearchWord.getValue()).split(" ")[0];
            fragment = new SearchedFragment(keywords);//默认搜索
            svm.searchWord.setValue(keywords);
        } else {
            fragment = new SearchedFragment(svm.searchWord.getValue());//搜索
        }
        replaceFragment(fragment);
    }

    public class ClickClass {
        public void back(View view) {
            finish();
        }

        public void search(View view) {
            getSearch();
        }

    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.activity_null, R.anim.activity_out_bottom_alpha);
    }
}