package com.zwb.ui.refreshlistview.ui;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.os.Build;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Transformation;
import android.widget.AbsListView;

/**
 * 自定义的下拉刷新组件
 * Created by wenbiao_zheng on 2014/12/2.
 *
 * @author wenbiao_zheng
 */
public class CustomSwipeRefreshLayout extends ViewGroup {
    //这种模式没有向上的箭头
    public static final int REFRESH_MODE_SWIPE = 1;
    //这种模式有向上的箭头，这种模式为默认模式
    public static final int REFRESH_MODE_PULL = 2;
    // 不滑动时返回原状态的时间限制
    private static final int RETURN_TO_ORIGINAL_POSITION_TIMEOUT = 50;
    // 不滑动时返回原状态的时间限制
    private static final int REFRESH_COMPLETE_POSITION_TIMEOUT = 50;
    // 顶部滑动条动画加速度
    private static final float ACCELERATE_INTERPOLATION_FACTOR = 1.5f;
    // 顶部滑动条动画减速度
    private static final float DECELERATE_INTERPOLATION_FACTOR = 2f;
    //顶部进度条高度
    private static final float PROGRESS_BAR_HEIGHT = 4;
    //最大触发滑动长度（父容器高度百分比）
    private static final float MAX_SWIPE_DISTANCE_FACTOR = .5f;
    //下拉至释放的触发滑动长度
    private static final int REFRESH_TRIGGER_DISTANCE = 80;
    //下拉阻尼系数
    private static final float SWIPE_DOWN_FACTOR = .5f;
    //顶部进度条view
    private CustomSwipeProgressBar topProgressBar;
    //刷新头部view
    private CustomSwipeRefreshHeadView headView;
    //是否加载顶部进度条
    private boolean enableTopProgressBar = false;

    private boolean enableTopRefreshingHead = true;
    private int refreshMode = REFRESH_MODE_PULL;
    private View target = null;
    private int originalOffsetTop;
    private int originalOffsetBottom;
    private OnRefreshListener listener;
    private MotionEvent downEvent;
    private int from; //触发点位置
    private boolean refreshing = false;
    private int touchSlop;
    private float distanceToTriggerSync = -1;
    private float prevY;
    private int mediumAnimationDuration;
    private float fromPercentage = 0;
    private float currPercentage = 0;
    private int progressBarHeight;
    private int currentTargetOffsetTop = 0;
    private boolean toRefreshFlag = false;
    private boolean checkValidMotionFlag = true;
    private int returnToOriginalTimeout = RETURN_TO_ORIGINAL_POSITION_TIMEOUT;
    private int refreshCompleteTimeout = 0;
    private boolean isPull = true;

    //是否返回至原始状态
    private boolean returningToStart;
    private DecelerateInterpolator decelerateInterpolator;

    private AccelerateInterpolator accelerateInterpolator;
    private static final int[] LAYOUT_ATTRS = new int[]{
            android.R.attr.state_enabled
    };

    //回到起始位置的动画
    private final Animation animateToStartPosition = new Animation() {
        @Override
        public void applyTransformation(float interpolatedTime, Transformation t) {
            int targetTop = 0;
            if (from != originalOffsetTop) {
                targetTop = (from + (int) ((originalOffsetTop - from) * interpolatedTime));
            }
            final int currentTop = target.getTop();
            int offset = targetTop - currentTop;
            if (targetTop < 0) {
                offset = 0 - currentTop;
            }
            setTargetOffsetTop(offset, true);
        }
    };

    //回到触发点的动画
    private final Animation animateToTriggerPosition = new Animation() {
        @Override
        public void applyTransformation(float interpolatedTime, Transformation t) {
            int targetTop = 0;
            if (from > distanceToTriggerSync) {
                targetTop = (from + (int) ((distanceToTriggerSync - from) * interpolatedTime));
            }
            final int currentTop = target.getTop();
            int offset = targetTop - currentTop;
            if (targetTop < 0) {
                offset = 0 - currentTop;
            }
            setTargetOffsetTop(offset, true);
        }
    };

    //刷新完成的动画，默认是不实现的
    private final Animation animateStayComplete = new Animation() {
        @Override
        public void applyTransformation(float interpolatedTime, Transformation t) {
            // DO NOTHING
        }
    };

    public CustomSwipeRefreshLayout(Context context) {
        super(context, null);
    }

    public CustomSwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        //表示滑动的时候，手的移动要大于这个距离才开始移动控件,如果小于这个距离就不触发移动控件
        touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();

        mediumAnimationDuration = getResources().getInteger(
                android.R.integer.config_mediumAnimTime);

        //表示这时候不绘制自己
        setWillNotDraw(false);
        topProgressBar = new CustomSwipeProgressBar(this);
        headView = new CustomSwipeRefreshHeadView(context);

        final DisplayMetrics metrics = getResources().getDisplayMetrics();
        progressBarHeight = (int) (metrics.density * PROGRESS_BAR_HEIGHT);
        decelerateInterpolator = new DecelerateInterpolator(DECELERATE_INTERPOLATION_FACTOR);
        accelerateInterpolator = new AccelerateInterpolator(ACCELERATE_INTERPOLATION_FACTOR);

        final TypedArray a = context.obtainStyledAttributes(attrs, LAYOUT_ATTRS);
        //间接调用invalidate，重新绘制headView，index为0表示headView,1表示target
        setEnabled(a.getBoolean(0, true));
        a.recycle();

        addView(headView);
    }

    public CustomSwipeRefreshLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @SuppressLint("NewApi")
    public CustomSwipeRefreshLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    /**
     * 刷新完成的动画
     *
     * @param listener 监听
     */
    private void animateStayComplete(Animation.AnimationListener listener) {
        animateStayComplete.reset();
        animateStayComplete.setDuration(refreshCompleteTimeout);
        animateStayComplete.setAnimationListener(listener);
        target.startAnimation(animateStayComplete);
    }

    /**
     * 返回触发点位置的动画
     *
     * @param from     触发点位置
     * @param listener 监听
     */
    private void animateOffsetToTriggerPosition(int from, Animation.AnimationListener listener) {
        this.from = from;
        animateToTriggerPosition.reset();
        animateToTriggerPosition.setDuration(mediumAnimationDuration);
        animateToTriggerPosition.setAnimationListener(listener);
        animateToTriggerPosition.setInterpolator(decelerateInterpolator);
        target.startAnimation(animateToTriggerPosition);
    }

    //刷新完成的Runnable
    private final Runnable stayRefreshCompletePosition = new Runnable() {

        @Override
        public void run() {
            animateStayComplete(stayCompleteListener);
        }

    };

    //返回触发点位置的Runnable
    private final Runnable returnToTriggerPosition = new Runnable() {

        @Override
        public void run() {
            animateOffsetToTriggerPosition(currentTargetOffsetTop + getPaddingTop(),
                    null);
        }

    };

    //阻尼触发的动画
    private Animation shrinkTrigger = new Animation() {
        @Override
        public void applyTransformation(float interpolatedTime, Transformation t) {
            float percent = fromPercentage + ((0 - fromPercentage) * interpolatedTime);
            topProgressBar.setTriggerPercentage(percent);
        }
    };

    //刷新完成的监听
    private final Animation.AnimationListener stayCompleteListener = new BaseAnimationListener() {
        @Override
        public void onAnimationEnd(Animation animation) {
            returnToStartPosition.run();
            refreshing = false;
        }
    };

    //返回起始位置的监听
    private final Animation.AnimationListener returnToStartPositionListener = new BaseAnimationListener() {
        @Override
        public void onAnimationEnd(Animation animation) {
            //一旦内容已经返回到起始位置，重置offset为0
            currentTargetOffsetTop = 0;
        }
    };

    //阻尼的监听
    private final Animation.AnimationListener shrinkAnimationListener = new BaseAnimationListener() {
        @Override
        public void onAnimationEnd(Animation animation) {
            currPercentage = 0;
        }
    };

    //返回到起始位置的Runnable
    private final Runnable returnToStartPosition = new Runnable() {

        @Override
        public void run() {
            returningToStart = true;
            animateOffsetToStartPosition(currentTargetOffsetTop + getPaddingTop(),
                    returnToStartPositionListener);
        }

    };

    //取消刷新的手势和动画，还原所有状态
    private final Runnable cancel = new Runnable() {

        @Override
        public void run() {
            returningToStart = true;
            if (topProgressBar != null && enableTopProgressBar) {
                fromPercentage = currPercentage;
                shrinkTrigger.setDuration(mediumAnimationDuration);
                shrinkTrigger.setAnimationListener(shrinkAnimationListener);
                shrinkTrigger.reset();
                shrinkTrigger.setInterpolator(decelerateInterpolator);
                startAnimation(shrinkTrigger);
            }
            animateOffsetToStartPosition(currentTargetOffsetTop + getPaddingTop(),
                    returnToStartPositionListener);
        }
    };

    /**
     * 设置刷新的模式
     *
     * @param mode 模式
     */
    public void setRefreshMode(int mode) {
        switch (mode) {
            case REFRESH_MODE_PULL:
                refreshMode = REFRESH_MODE_PULL;
                headView.setRefreshState(CustomSwipeRefreshHeadView.STATE_NORMAL);
                break;
            case REFRESH_MODE_SWIPE:
                refreshMode = REFRESH_MODE_SWIPE;
                enableTopRefreshingHead(false);
                headView.setRefreshState(CustomSwipeRefreshHeadView.STATE_NORMAL);
                break;
            default:
                throw new IllegalStateException(
                        "refresh mode " + mode + " is node supported in CustomSwipeRefreshLayout");

        }
    }

    /**
     * 获取刷新的模式
     *
     * @return 模式
     */
    public int getRefreshMode() {
        return refreshMode;
    }

    /**
     * 显示顶部的进度框
     *
     * @param isEnable 是否显示
     */
    public void enableTopProgressBar(boolean isEnable) {
        if (enableTopProgressBar == isEnable)
            return;

        enableTopProgressBar = isEnable;
        requestLayout();
    }

    /**
     * 显示顶部的刷新头
     *
     * @param isEnable 是否显示
     */
    public void enableTopRefreshingHead(boolean isEnable) {
        enableTopRefreshingHead = isEnable;
    }

    /**
     * 是否显示刷新头
     *
     * @return 是否显示
     */
    public boolean isEnableTopRefreshingHead() {
        return enableTopRefreshingHead;
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        removeCallbacks(cancel);
        removeCallbacks(returnToStartPosition);
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        removeCallbacks(returnToStartPosition);
        removeCallbacks(cancel);
    }

    /**
     * 回到初始位置的动画
     *
     * @param from     初始位置
     * @param listener 监听
     */
    private void animateOffsetToStartPosition(int from, Animation.AnimationListener listener) {
        this.from = from;
        animateToStartPosition.reset();
        animateToStartPosition.setDuration(mediumAnimationDuration);
        animateToStartPosition.setAnimationListener(listener);
        animateToStartPosition.setInterpolator(decelerateInterpolator);
        target.startAnimation(animateToStartPosition);
    }

    /**
     * 设置刷新监听
     *
     * @param listener 监听
     */
    public void setOnRefreshListener(OnRefreshListener listener) {
        this.listener = listener;
    }

    /**
     * 设置阻尼的系数大小
     *
     * @param percent 系数大小
     */
    private void setTriggerPercentage(float percent) {
        if (percent == 0f) {
            currPercentage = 0;
            return;
        }
        currPercentage = percent;
        if (enableTopProgressBar) {
            topProgressBar.setTriggerPercentage(percent);
        }
    }

    /**
     * Notify the widget that refresh state has changed. Do not call this when
     * refresh is triggered by a swipe gesture.
     *
     * @param refreshing Whether or not the view should show refresh progress.
     */
    public void setRefreshing(boolean refreshing) {
        if (this.refreshing != refreshing) {
            ensureTarget();
            currPercentage = 0;
            this.refreshing = refreshing;
            if (this.refreshing) {
                if (enableTopProgressBar) {
                    topProgressBar.start();
                } else {
                    postInvalidate();
                }
                returnToTriggerPosition.run();
                removeCallbacks(returnToStartPosition);
                removeCallbacks(cancel);
                stayRefreshCompletePosition.run();
            } else {
                if (enableTopProgressBar) {
                    topProgressBar.stop();
                } else {
                    postInvalidate();
                }
                this.refreshing = false;
            }
        } else {
            if (enableTopProgressBar) {
                topProgressBar.stop();
            } else {
                postInvalidate();
            }
            this.refreshing = false;
        }
    }

    /**
     * 设置下拉刷新的持续时间
     *
     * @param refreshing 是否下拉刷新
     * @param delay      持续时间
     */
    public void setRefreshing(boolean refreshing, long delay) {
        if (this.refreshing != refreshing) {
            ensureTarget();
            currPercentage = 0;
            this.refreshing = refreshing;
            if (this.refreshing) {
                if (enableTopProgressBar) {
                    topProgressBar.start();
                } else {
                    postInvalidate();
                }
                returnToTriggerPosition.run();
                removeCallbacks(returnToStartPosition);
                removeCallbacks(cancel);
                stayRefreshCompletePosition.run();
            } else {
                if (enableTopProgressBar) {
                    topProgressBar.stop();
                } else {
                    postInvalidate();
                }
                this.refreshing = false;
            }
        } else {
            if (enableTopProgressBar) {
//                topProgressBar.stop();
            } else {
                postInvalidate();
            }
            this.refreshing = false;
        }
    }

    /**
     * 设置刷新结束时，停止刷新
     */
    public void onRefreshingComplete() {
        setRefreshing(false);
    }

    /**
     * Set the four colors used in the progress animation. The first color will
     * also be the color of the bar that grows in response to a user swipe
     * gesture.
     *
     * @param colorRes1 Color resource.
     * @param colorRes2 Color resource.
     * @param colorRes3 Color resource.
     * @param colorRes4 Color resource.
     */
    public void setProgressBarColorRes(int colorRes1, int colorRes2, int colorRes3, int colorRes4) {
        final Resources res = getResources();
        final int color1 = res.getColor(colorRes1);
        final int color2 = res.getColor(colorRes2);
        final int color3 = res.getColor(colorRes3);
        final int color4 = res.getColor(colorRes4);
        topProgressBar.setColorScheme(color1, color2, color3, color4);
    }


    /**
     * 设置进度条的颜色
     *
     * @param color1
     * @param color2
     * @param color3
     * @param color4
     */
    public void setProgressBarColor(int color1, int color2, int color3, int color4) {
        topProgressBar.setColorScheme(color1, color2, color3, color4);
    }

    /**
     * @return Whether the SwipeRefreshWidget is actively showing refresh
     * progress.
     */
    public boolean isRefreshing() {
        return refreshing;
    }

    /**
     * 初始化Target的参数
     */
    private void ensureTarget() {
        // Don't bother getting the parent height if the parent hasn't been laid out yet.
        if (target == null) {
            if (getChildCount() > 2 && !isInEditMode()) {
                throw new IllegalStateException(
                        "CustomSwipeRefreshLayout can host only one direct child");
            }
            target = getChildAt(1);
            originalOffsetTop = target.getTop() + getPaddingTop();
            originalOffsetBottom = getChildAt(1).getHeight();
        }
        if (distanceToTriggerSync == -1) {
            if (getParent() != null && ((View) getParent()).getHeight() > 0) {
                final DisplayMetrics metrics = getResources().getDisplayMetrics();
                distanceToTriggerSync = (int) Math.min(
                        ((View) getParent()).getHeight() * MAX_SWIPE_DISTANCE_FACTOR,
                        REFRESH_TRIGGER_DISTANCE * metrics.density);
            }
        }
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        if (enableTopProgressBar) {
            topProgressBar.draw(canvas);
        }

        headView.draw(canvas);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        final int width = getMeasuredWidth();
        final int height = getMeasuredHeight();
        if (enableTopProgressBar) {
            topProgressBar.setBounds(0, 0, width, progressBarHeight);
        } else {
            topProgressBar.setBounds(0, 0, 0, 0);
        }
        headView.setBounds(0, 0, width, currentTargetOffsetTop);

        if (getChildCount() == 0) {
            return;
        }
        final View child = getChildAt(1);
        final int childLeft = getPaddingLeft();
        final int childTop = currentTargetOffsetTop + getPaddingTop();
        final int childWidth = width - getPaddingLeft() - getPaddingRight();
        final int childHeight = height - getPaddingTop() - getPaddingBottom();
        child.layout(childLeft, childTop, childLeft + childWidth, childTop + childHeight);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (getChildCount() > 2 && !isInEditMode()) {
            throw new IllegalStateException("CustomSwipeRefreshLayout can host only one child content view");
        }
        if (getChildCount() > 0) {
            getChildAt(1).measure(
                    MeasureSpec.makeMeasureSpec(
                            getMeasuredWidth() - getPaddingLeft() - getPaddingRight(),
                            MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(
                            getMeasuredHeight() - getPaddingTop() - getPaddingBottom(),
                            MeasureSpec.EXACTLY));
        }


    }

    /**
     * @return Whether it is possible for the child view of this layout to
     * scroll up. Override this if the child view is a custom view.
     */
    public boolean canChildScrollUp() {
        boolean ret;
        if (Build.VERSION.SDK_INT < 14) {
            if (target instanceof AbsListView) {
                final AbsListView absListView = (AbsListView) target;
                ret = absListView.getChildCount() > 0
                        && (absListView.getFirstVisiblePosition() > 0 || absListView.getChildAt(0)
                        .getTop() < absListView.getPaddingTop());
            } else {
                ret = target.getScrollY() > 0;
            }
        } else {
            ret = ViewCompat.canScrollVertically(target, -1);
        }

        return ret;
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {

        // to be further modified here ...

        return super.dispatchTouchEvent(event);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        ensureTarget();
        boolean handled = false;
        float curY = ev.getY();

        if (returningToStart && ev.getAction() == MotionEvent.ACTION_DOWN) {
            returningToStart = false;
        }

        // record the first event:
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            currPercentage = 0;
            downEvent = MotionEvent.obtain(ev);
            prevY = downEvent.getY();
            toRefreshFlag = false;
            checkValidMotionFlag = true;
        } else if (ev.getAction() == MotionEvent.ACTION_MOVE) {
            float yDiff = curY - downEvent.getY();
            if (yDiff < 0)
                yDiff = -yDiff;

            if (yDiff < touchSlop) {
                prevY = curY;
                return false;
            }
        }

        if (isEnabled()) {
            if (!returningToStart && !canChildScrollUp()) {
                handled = onTouchEvent(ev);
            } else {
                // keep updating last Y position when the event is not intercepted!
                prevY = ev.getY();
            }
        }

        return !handled ? super.onInterceptTouchEvent(ev) : handled;
    }

    @Override
    public void requestDisallowInterceptTouchEvent(boolean b) {
        // Nope.
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isPull) {
            return false;
        }
        final int action = event.getAction();
        boolean handled = false;

        switch (action) {
            case MotionEvent.ACTION_MOVE:
                if (downEvent != null && !returningToStart) {
                    final float eventY = event.getY();
                    float yDiff = eventY - downEvent.getY();
                    int curTargetTop = target.getTop();
                    currentTargetOffsetTop = curTargetTop;
                    boolean isScrollUp = eventY - prevY > 0;

                    // if yDiff is large enough to be counted as one move event
                    if (checkValidMotionFlag && (yDiff > touchSlop || yDiff < -touchSlop)) {
                        checkValidMotionFlag = false;
                    }
                    // if refresh head moving with the mTarget is enabled
                    if (!enableTopRefreshingHead) {
                        // when it is refreshing
                        if (isRefreshing()) {
                            // scroll down
                            if (!isScrollUp) {
                                // when the top of mTarget reach the parent top
                                if (curTargetTop <= 0) {
                                    prevY = event.getY();
                                    handled = false;
                                    updateContentOffsetTop(originalOffsetTop, true);
                                    //mStopInterceptFlag = true;
                                    break;
                                }
                            }
                            // scroll up
                            else {
                                // when refresh head is entirely visible
                                if (curTargetTop >= distanceToTriggerSync) {
                                    prevY = event.getY();
                                    handled = true;
                                    updateContentOffsetTop((int) distanceToTriggerSync, true);
                                    break;
                                }
                            }

                            setTargetOffsetTop((int) ((eventY - prevY)), true);
                            prevY = event.getY();
                            handled = true;
                            break;
                        }
                    }
                    // keep refresh head above mTarget when refreshing
                    else {
                        if (isRefreshing()) {
                            prevY = event.getY();
                            handled = false;
                            break;
                        }
                    }

                    // curTargetTop is bigger than trigger
                    if (curTargetTop >= distanceToTriggerSync) {
                        // User movement passed distance; trigger a refresh
                        if (enableTopProgressBar)
                            topProgressBar.setTriggerPercentage(1f);

                        removeCallbacks(cancel);
                        if (refreshMode == REFRESH_MODE_SWIPE) {
                            toRefreshFlag = false;
                            startRefresh();
                            handled = true;
                            break;
                        } else if (refreshMode == REFRESH_MODE_PULL) {
                            toRefreshFlag = true;
                        }
                    }
                    // curTargetTop is not bigger than trigger
                    else {
                        toRefreshFlag = false;
                        // Just track the user's movement

                        setTriggerPercentage(
                                accelerateInterpolator.getInterpolation(
                                        curTargetTop / distanceToTriggerSync));

                        if (!isScrollUp && (curTargetTop < 1)) {
                            removeCallbacks(cancel);
                            prevY = event.getY();
                            handled = false;
                            // clear the progressBar
                            topProgressBar.setTriggerPercentage(0f);
                            break;
                        } else {
                            updatePositionTimeout();
                        }

                    }

                    handled = true;
                    if (curTargetTop > 0 && !isRefreshing())
                        setTargetOffsetTop((int) ((eventY - prevY) * SWIPE_DOWN_FACTOR), false);
                    else
                        setTargetOffsetTop((int) ((eventY - prevY)), true);
                    prevY = event.getY();
                }

                break;
            case MotionEvent.ACTION_UP:
                if (toRefreshFlag && refreshMode == REFRESH_MODE_PULL) {
                    startRefresh();
                    toRefreshFlag = false;
                    handled = true;
                    break;
                }

            case MotionEvent.ACTION_CANCEL:
                if (downEvent != null) {
                    downEvent.recycle();
                    downEvent = null;
                }
                break;
        }
        return handled;
    }

    /**
     * 开始下拉刷新
     */
    private void startRefresh() {
        removeCallbacks(cancel);
        headView.setRefreshState(CustomSwipeRefreshHeadView.STATE_REFRESHING);
        setRefreshing(true);
        listener.onRefresh();
    }

    /**
     * 更新target的offsetTop
     *
     * @param targetTop        target的top值
     * @param changeHeightOnly 是否只是改变高度
     */
    private void updateContentOffsetTop(int targetTop, boolean changeHeightOnly) {
        final int currentTop = target.getTop();
        if (targetTop < 0) {
            targetTop = 0;
        }

        setTargetOffsetTop(targetTop - currentTop, changeHeightOnly);
    }

    /**
     * 设置offsetTop
     *
     * @param offset           要设置的offset值
     * @param changeHeightOnly 是否只是改变高度
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void setTargetOffsetTop(int offset, boolean changeHeightOnly) {
        if (currentTargetOffsetTop + offset >= 0) {
            target.offsetTopAndBottom(offset);
        } else {
            updateContentOffsetTop(0, changeHeightOnly);
        }
        currentTargetOffsetTop = target.getTop();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            target.setBottom(originalOffsetBottom);
        }
        headView.updateHeight(target.getTop(), (int) distanceToTriggerSync, changeHeightOnly);
    }

    /**
     * 返回到初始位置的延迟
     */
    private void updatePositionTimeout() {
        removeCallbacks(cancel);
        postDelayed(cancel, returnToOriginalTimeout);
    }

    /**
     * 设置返回到初始位置的延迟
     *
     * @param returnToOriginalTimeout 延迟
     */
    public void setReturnToOriginalTimeout(int returnToOriginalTimeout) {
        this.returnToOriginalTimeout = returnToOriginalTimeout;
    }

    /**
     * 获取刷新完成的延迟
     *
     * @return 延迟
     */
    public int getRefreshCompleteTimeout() {
        return refreshCompleteTimeout;
    }

    /**
     * 设置刷新完成的延迟
     *
     * @param refreshCompleteTimeout 延迟
     */
    public void setRefreshCompleteTimeout(int refreshCompleteTimeout) {
        this.refreshCompleteTimeout = refreshCompleteTimeout;
    }

    /**
     * Classes that wish to be notified when the swipe gesture correctly
     * triggers a refresh should implement this interface.
     */
    public interface OnRefreshListener {
        public void onRefresh();
    }

    /**
     * Simple AnimationListener to avoid having to implement unneeded methods in
     * AnimationListeners.
     */
    private class BaseAnimationListener implements Animation.AnimationListener {
        @Override
        public void onAnimationStart(Animation animation) {
        }

        @Override
        public void onAnimationEnd(Animation animation) {
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
        }
    }

    public void isPull(boolean isPull) {
        this.isPull = isPull;
    }
}
