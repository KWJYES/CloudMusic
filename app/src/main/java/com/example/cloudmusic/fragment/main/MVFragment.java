package com.example.cloudmusic.fragment.main;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.cloudmusic.R;
import com.example.cloudmusic.activities.MVActivity;
import com.example.cloudmusic.adapter.recyclerview.MVAllAdapter;
import com.example.cloudmusic.base.BaseFragment;
import com.example.cloudmusic.databinding.FragmentAllMvBinding;
import com.example.cloudmusic.entity.MV;
import com.example.cloudmusic.request.fragment.main.RequestMvFragmentViewModel;
import com.example.cloudmusic.state.fragment.main.StateAllMVFragmentViewModel;
import com.example.cloudmusic.utils.CloudMusic;
import com.example.cloudmusic.utils.callback.MVItemClickCallback;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.List;


public class MVFragment extends BaseFragment implements View.OnClickListener {

    FragmentAllMvBinding binding;
    StateAllMVFragmentViewModel svm;
    RequestMvFragmentViewModel rvm;
    private List<MV> mvList;
    private MVAllAdapter adapter;

    @Override
    protected View initFragment(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_all_mv, container, false);
        svm = new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(StateAllMVFragmentViewModel.class);
        rvm = new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(RequestMvFragmentViewModel.class);
        binding.setSvm(svm);
        binding.setLifecycleOwner(this);
        return binding.getRoot();
    }


    @Override
    protected void initSomeData() {
        mvList = new ArrayList<>();
    }

    @Override
    protected void getInternetData() {
        rvm.getMvs(svm.firstSelect.getValue(), svm.secondSelect.getValue(), mvList.size());
    }

    @Override
    protected void initView() {
        binding.mvLoadFalse.setVisibility(View.GONE);
        binding.allMvLoading.show();
        svm.firstSelect.setValue("全部");
        svm.secondSelect.setValue("全部");
        setSelectButtonListener();
        binding.smartRefreshLayout.setOnRefreshListener(refreshLayout -> {
            mvList.clear();
            rvm.getMvs(svm.firstSelect.getValue(), svm.secondSelect.getValue(), mvList.size());
            binding.mvLoadFalse.setVisibility(View.GONE);
            binding.allMvLoading.show();
        });
        binding.smartRefreshLayout.setOnLoadMoreListener(refreshLayout -> rvm.getMvs(svm.firstSelect.getValue(), svm.secondSelect.getValue(), mvList.size()));
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void observerDataStateUpdateAction() {
        rvm.mvRequestState.observe(this, s -> {
            if (s.equals(CloudMusic.FAILURE)) {
                binding.mvLoadFalse.setVisibility(View.VISIBLE);
                if (adapter != null)
                    adapter.notifyDataSetChanged();
            } else {
                binding.mvLoadFalse.setVisibility(View.GONE);
            }
            binding.allMvLoading.hide();
            binding.smartRefreshLayout.finishRefresh();
            binding.smartRefreshLayout.finishLoadMore();
        });
        rvm.mvList.observe(this, mvs -> {
            if (mvList.size() == 0) setMvRV(mvs);
            else moreMvLoad(mvs);
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void moreMvLoad(List<MV> mvs) {
        mvList.addAll(mvs);
        adapter.notifyDataSetChanged();
    }

    private void setMvRV(List<MV> mvs) {
        mvList.addAll(mvs);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        binding.allMvRv.setLayoutManager(layoutManager);
        adapter = new MVAllAdapter(mvList);
        adapter.setItemClickCallback(mv -> {
            Intent intent = new Intent(getActivity(), MVActivity.class);
            intent.putExtra("mvId", mv.getMvId());
            intent.putExtra("mvTittle", mv.getName());
            startActivity(intent);
        });
        binding.allMvRv.setAdapter(adapter);
    }

    private void setSelectButtonListener() {
        binding.sb10.setOnClickListener(this);
        binding.sb11.setOnClickListener(this);
        binding.sb12.setOnClickListener(this);
        binding.sb13.setOnClickListener(this);
        binding.sb14.setOnClickListener(this);
        binding.sb15.setOnClickListener(this);
        binding.sb20.setOnClickListener(this);
        binding.sb21.setOnClickListener(this);
        binding.sb22.setOnClickListener(this);
        binding.sb23.setOnClickListener(this);
        binding.sb24.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("TAG", "DiscussionFragment onResume");
    }

    @SuppressLint({"NonConstantResourceId", "NotifyDataSetChanged"})
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.sb10:
                svm.firstSelect.setValue("全部");
                break;
            case R.id.sb11:
                svm.firstSelect.setValue("内地");
                break;
            case R.id.sb12:
                svm.firstSelect.setValue("港台");
                break;
            case R.id.sb13:
                svm.firstSelect.setValue("欧美");
                break;
            case R.id.sb14:
                svm.firstSelect.setValue("日本");
                break;
            case R.id.sb15:
                svm.firstSelect.setValue("韩国");
                break;
            case R.id.sb20:
                svm.secondSelect.setValue("全部");
                break;
            case R.id.sb21:
                svm.secondSelect.setValue("官方版");
                break;
            case R.id.sb22:
                svm.secondSelect.setValue("原生");
                break;
            case R.id.sb23:
                svm.secondSelect.setValue("现场版");
                break;
            case R.id.sb24:
                svm.secondSelect.setValue("网易出品");
                break;
        }
        Log.d("TAG", "选择了：" + svm.firstSelect.getValue() + " " + svm.secondSelect.getValue());
        mvList.clear();
        if (adapter != null)
            adapter.notifyDataSetChanged();
        rvm.getMvs(svm.firstSelect.getValue(), svm.secondSelect.getValue(), mvList.size());
        binding.allMvLoading.show();
    }
}