package com.zwb.ui.refreshlistview.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewGroup;

/**
 * 自定义的下拉刷新HeadViw
 * Created by wenbiao_zheng on 2014/12/2.
 *
 * @author wenbiao_zheng
 */
final class CustomSwipeRefreshHeadView extends ViewGroup {
    // Default progress animation colors are grays.
    private final static int COLOR1 = 0xB3000000;
    private final static int COLOR2 = 0x80000000;
    private final static int COLOR3 = 0x4d000000;
    private final static int COLOR4 = 0x1a000000;
    //states
    public final static int STATE_NORMAL = 0;
    public final static int STATE_READY = 1;
    public final static int STATE_REFRESHING = 2;

    // Colors used when rendering the animation,
    private int mColor1;
    private int mColor2;
    private int mColor3;
    private int mColor4;

    private ViewGroup mHeadLayout;

    private Rect mBounds = new Rect();

    public CustomSwipeRefreshHeadView(Context context) {
        super(context);
        mColor1 = COLOR1;
        mColor2 = COLOR2;
        mColor3 = COLOR3;
        mColor4 = COLOR4;

        setDefaultHeadLayout();
    }

    public CustomSwipeRefreshHeadView(Context context, ViewGroup layout) {
        super(context);
        mColor1 = COLOR1;
        mColor2 = COLOR2;
        mColor3 = COLOR3;
        mColor4 = COLOR4;

        setHeadLayout(layout);

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
    }

    public void setDefaultHeadLayout() {
        setHeadLayout(new DefaultCustomHeadViewLayout(getContext()));
    }

    public void setRefreshState(int state) {
        if (mHeadLayout instanceof CustomSwipeRefreshHeadLayout) {
            ((CustomSwipeRefreshHeadLayout) mHeadLayout).setState(state);
        }
    }

    public CustomSwipeRefreshHeadView setHeadLayout(ViewGroup layout) {

        if (!(layout instanceof CustomSwipeRefreshHeadLayout)) {
            throw new IllegalStateException(
                    "ViewGroup must implements CustomSwipeRefreshHeadLayout interface!");
        }

        mHeadLayout = layout;
        mHeadLayout.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        addView(mHeadLayout);
        return this;
    }


    @Override
      public void onDraw(Canvas canvas) {
        final int width = mBounds.width();
        final int height = mBounds.height();
        int restoreCount = canvas.save();
        canvas.clipRect(mBounds);

        Rect mainTextRect = new Rect();
        mainTextRect.set(0, 0, width, height);

        mHeadLayout.setBackgroundColor(Color.TRANSPARENT);

        //Measure the view at the exact dimensions (otherwise the view won't center correctly)
        int widthSpec = MeasureSpec.makeMeasureSpec(mainTextRect.width(), MeasureSpec.EXACTLY);
        int heightSpec = MeasureSpec.makeMeasureSpec(mainTextRect.height(), MeasureSpec.EXACTLY);
        mHeadLayout.measure(widthSpec, heightSpec);
        //Lay the view out at the rect width and height
        mHeadLayout.layout(0, 0, mainTextRect.width(), mainTextRect.height());
        canvas.translate(mainTextRect.left, mainTextRect.top);
        mHeadLayout.draw(canvas);

        canvas.restoreToCount(restoreCount);
    }

    /**
     * 更新高度
     *
     * @param height                高度
     * @param distanceToTriggerSync 阻尼的长度
     * @param changeHeightOnly      是否仅仅改变高度
     */
    public void updateHeight(int height, int distanceToTriggerSync, boolean changeHeightOnly) {
        mBounds.bottom = height;

        if (changeHeightOnly) {
            invalidateView();
            return;
        }

        if (mBounds.bottom > distanceToTriggerSync) {
            setRefreshState(STATE_READY);
        } else {
            setRefreshState(STATE_NORMAL);
        }

        invalidateView();
    }

    /**
     * 绘制View
     */
    protected void invalidateView() {
        if (getParent() != null && getParent() instanceof View)
            ((View) getParent()).postInvalidate();

    }

    /**
     * Set the drawing bounds of this SwipeProgressBar.
     */
    void setBounds(int left, int top, int right, int bottom) {
        mBounds.left = left;
        mBounds.top = top;
        mBounds.right = right;
        mBounds.bottom = bottom;
    }

    public interface CustomSwipeRefreshHeadLayout {
        void setState(int state);
    }
}
