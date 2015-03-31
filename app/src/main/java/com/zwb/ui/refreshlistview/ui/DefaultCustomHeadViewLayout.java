package com.zwb.ui.refreshlistview.ui;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.zwb.ui.refreshlistview.R;


/**
 * 默认的自定义下拉刷新HeadView
 * Created by wenbiao_zheng on 2014/12/2.
 *
 * @author wenbiao_zheng
 */
public class DefaultCustomHeadViewLayout extends LinearLayout implements CustomSwipeRefreshHeadView.CustomSwipeRefreshHeadLayout {
    private LinearLayout mContainer;
    private TextView mMainTextView;
    private ImageView mImageView;
    private ProgressBar mProgressBar;

    private Animation mRotateUpAnim;
    private Animation mRotateDownAnim;
    private final int ROTATE_ANIM_DURATION = 180;

    private int mState = -1;
    private Animation.AnimationListener animationListener;

    public DefaultCustomHeadViewLayout(Context context) {
        super(context);

        setWillNotDraw(false);
        setupLayout();
    }

    public void setupLayout() {
        LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        mContainer = (LinearLayout) LayoutInflater.from(getContext()).inflate(R.layout.widget_swiperefresh_head_layout, null);
        addView(mContainer, lp);
        setGravity(Gravity.BOTTOM);
        mImageView = (ImageView) findViewById(R.id.iv_header_arrow);
        mMainTextView = (TextView) findViewById(R.id.tv_refresh_tips_text);
        mProgressBar = (ProgressBar) findViewById(R.id.pb_refreshheader_progressbar);

        setupAnimation();
    }

    public void setupAnimation() {

        mRotateUpAnim = new RotateAnimation(0.0f, -180.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        Animation.AnimationListener mRotateUpAnimListener = animationListener;
        mRotateUpAnim.setAnimationListener(mRotateUpAnimListener);
        mRotateUpAnim.setDuration(ROTATE_ANIM_DURATION);
        mRotateUpAnim.setFillAfter(true);

        mRotateDownAnim = new RotateAnimation(-180.0f, 0.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        mRotateDownAnim.setDuration(ROTATE_ANIM_DURATION);
        mRotateDownAnim.setFillAfter(true);
    }

    @Override
    public void setState(int state) {
        if (state == mState) {
            return;
        }
        switch (state) {
            case CustomSwipeRefreshHeadView.STATE_NORMAL:
                if (mState == CustomSwipeRefreshHeadView.STATE_READY) {
                    mImageView.clearAnimation();
                }
                if (mState == CustomSwipeRefreshHeadView.STATE_REFRESHING) {
                    mImageView.clearAnimation();
                }
                mMainTextView.setText(R.string.csr_text_state_normal);
                break;
            case CustomSwipeRefreshHeadView.STATE_READY:
                if (mState != CustomSwipeRefreshHeadView.STATE_READY) {
                    mImageView.clearAnimation();
                    mImageView.startAnimation(mRotateUpAnim);
                    mMainTextView.setText(R.string.csr_text_state_ready);
                }
                break;
            case CustomSwipeRefreshHeadView.STATE_REFRESHING:
                mMainTextView.setText(R.string.csr_text_state_refresh);
                break;
            default:
        }
        mState = state;
    }
}
