package com.example.cloudmusic.activities;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.annotation.SuppressLint;
import android.graphics.Rect;
import android.net.Uri;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.MediaController;
import android.widget.Toast;

import com.example.cloudmusic.R;
import com.example.cloudmusic.adapter.recyclerview.CommentAdapter;
import com.example.cloudmusic.base.BaseActivity;
import com.example.cloudmusic.databinding.ActivityMvactivityBinding;
import com.example.cloudmusic.entity.Comment;
import com.example.cloudmusic.request.activity.RequestMVViewModel;
import com.example.cloudmusic.state.activity.StateMVViewModel;
import com.example.cloudmusic.CloudMusic;

import java.util.ArrayList;
import java.util.List;

public class MVActivity extends BaseActivity implements ViewTreeObserver.OnGlobalLayoutListener {

    ActivityMvactivityBinding binding;
    StateMVViewModel svm;
    RequestMVViewModel rvm;

    //记录原始窗口高度
    private int mWindowHeight = 0;
    private int mWindowHeight2 = 0;
    private int mSoftKeyboardHeight = 0;
    private String mvId;
    private List<Comment> commentList;
    private CommentAdapter commentAdapter;


    @Override
    protected void initActivity() {
        setTransparentStatusBar(false);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_mvactivity);
        svm = new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(StateMVViewModel.class);
        rvm = new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(RequestMVViewModel.class);
        binding.setSvm(svm);
        binding.setClick(new ClickClass());
        binding.setLifecycleOwner(this);
    }

    @Override
    protected void initView() {
        rvm.pausePlaySong();
        binding.mvLoading.show();
        binding.getRoot().getViewTreeObserver().addOnGlobalLayoutListener(this);
        mvId = getIntent().getStringExtra("mvId");
        svm.mvTittle.setValue(getIntent().getStringExtra("mvTittle"));
        binding.smartRefreshLayout.setOnRefreshListener(refreshLayout -> {
            commentList.clear();
            rvm.getMVComment(mvId, 20, commentList.size());
        });
        binding.smartRefreshLayout.setOnLoadMoreListener(refreshLayout -> rvm.getMVComment(mvId, 20, commentList.size()));
    }

    @Override
    protected void initSomeData() {
        commentList = new ArrayList<>();
    }

    @Override
    protected void getInternetData() {
        rvm.getMVUrl(mvId);
    }

    @Override
    protected void observerDataStateUpdateAction() {
        rvm.mvUrl.observe(this, this::initVideoView);
        rvm.commentList.observe(this, comments -> {
            if (commentList.size() == 0)
                setCommentRV(comments);
            else
                moreCommentRV(comments);
        });
        rvm.commentListState.observe(this, s -> {
            if (s.equals(CloudMusic.FAILURE)) {
                Toast.makeText(MVActivity.this, "获取评论失败", Toast.LENGTH_SHORT).show();
            }
            binding.smartRefreshLayout.finishLoadMore();
            binding.smartRefreshLayout.finishRefresh();
        });
        rvm.mvUrlState.observe(this, s -> {
            if (s.equals(CloudMusic.FAILURE)) {
                binding.mvLoading.hide();
                binding.LoadingTV.setText("网络不稳定，请退出重试");
            } else {
                rvm.getMVComment(mvId, 20, 0);
            }
        });
        rvm.sendCommentRequestState.observe(this, s -> {
            if (s.equals(CloudMusic.FAILURE)) {
                Toast.makeText(MVActivity.this, "发表评论失败", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(MVActivity.this, "发表评论成功", Toast.LENGTH_SHORT).show();
                svm.content.setValue("");
            }
        });
        rvm.sendComment.observe(this, comment -> {
            commentList.add(0,comment);
            commentAdapter.notifyItemRangeChanged(0, commentList.size());
        });
    }

    private void setCommentRV(List<Comment> comments) {
        if (comments.size() == 0) binding.nullTV.setVisibility(View.VISIBLE);
        else binding.nullTV.setVisibility(View.GONE);
        commentList.addAll(comments);
        binding.commentRv.setLayoutManager(new LinearLayoutManager(this));
        commentAdapter = new CommentAdapter(commentList);
        commentAdapter.setClickCallback((comment, isLike) -> rvm.likeComment(comment,isLike,mvId));
        binding.commentRv.setAdapter(commentAdapter);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void moreCommentRV(List<Comment> comments) {
        commentList.addAll(comments);
        commentAdapter.notifyDataSetChanged();
    }

    public class ClickClass {

        public void back(View view) {
            finish();
        }

        /**
         * 发送评论
         *
         * @param view
         */
        public void send(View view) {
            if (svm.content.getValue() == null || svm.content.getValue().equals("")) return;
            rvm.sendComment(svm.content.getValue(), mvId);
        }
    }

    private void initVideoView(String mvUrl) {
        final MediaController mediacontroller = new MediaController(this);
        mediacontroller.setAnchorView(binding.videoView);
        binding.videoView.setMediaController(mediacontroller);
        binding.videoView.setVideoURI(Uri.parse(mvUrl));
        binding.videoView.requestFocus();
        binding.videoView.setOnPreparedListener(mp -> {
            binding.mvLoading.hide();
            binding.LoadingTV.setVisibility(View.GONE);
            mp.start();
            mp.setOnVideoSizeChangedListener((mp1, width, height) -> {
                binding.videoView.setMediaController(mediacontroller);
                mediacontroller.setAnchorView(binding.videoView);

            });
        });
        binding.videoView.setOnCompletionListener(mp -> {
            binding.videoView.start();
        });
        binding.videoView.setOnErrorListener((mp, what, extra) -> {
            Log.d("TAG", "videoView: What " + what + " extra " + extra);
            Toast toast = Toast.makeText(MVActivity.this, "\n视频无法播放\n请您稍后重试\n", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            return false;
        });
    }

    @Override
    protected void onDestroy() {
        binding.getRoot().getViewTreeObserver().removeOnGlobalLayoutListener(this);
        binding.videoView.suspend();//释放资源
        super.onDestroy();
    }

    /**
     * 打开键盘editText上移
     */
    @Override
    public void onGlobalLayout() {
        Rect r = new Rect();
        //获取当前窗口实际的可见区域
        getWindow().getDecorView().getWindowVisibleDisplayFrame(r);
        int height = r.height();
        if (mWindowHeight == 0) {
            //一般情况下，这是原始的窗口高度,view第一次绘制时
            mWindowHeight = height;
        } else {
            if (mWindowHeight != height) {
                mWindowHeight2 = height;
                //两次窗口高度相减，就是软键盘高度
                mSoftKeyboardHeight = mWindowHeight - height;
            }
        }
        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) binding.linearLayout10.getLayoutParams();
        if (layoutParams.bottomMargin == 0 && height != mWindowHeight) {
            layoutParams.bottomMargin = layoutParams.bottomMargin + mSoftKeyboardHeight;
            binding.linearLayout10.requestLayout();//这个也会回调onGlobalLayout()
        } else if (layoutParams.bottomMargin != 0 && height != mWindowHeight2) {
            layoutParams.bottomMargin = 0;
            binding.linearLayout10.requestLayout();//这个也会回调onGlobalLayout()
        }
    }
}