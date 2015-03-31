package com.zwb.ui.refreshlistview.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewStub;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.zwb.ui.refreshlistview.R;


/**
 * 自定义的AbsListView，用于下拉刷新
 * Created by wenbiao_zheng on 2014/10/27.
 *
 * @author wenbiao_zheng
 */
public abstract class BaseRefreshAbsListView extends FrameLayout implements AbsListView.OnScrollListener {
    //底部加载框加载的最大数据
    protected int ITEM_LEFT_TO_LOAD_MORE = 1;

    protected int commonListViewMainLayoutId;
    protected CustomSwipeRefreshLayout ptrLayout;
    protected AbsListView baseListView;
    protected AbsListView.OnScrollListener onScrollListener;
    protected float dividerHeight;
    protected int divider;
    protected boolean clipToPadding;
    protected int padding;
    protected int paddingTop;
    protected int paddingBottom;
    protected int paddingLeft;
    protected int paddingRight;
    protected int scrollbarStyle;
    protected int selector;
    protected View progress;
    protected ViewStub emptyView;
    protected int emptyId;
    protected OnMoreListener onMoreListener;
    protected boolean isLoadingMore;
    protected BaseBottomOperation bottomOperation;

    public BaseRefreshAbsListView(Context context) {
        super(context);

        initView();
    }

    public BaseRefreshAbsListView(Context context, AttributeSet attrs) {
        super(context, attrs);

        initAttrs(attrs);
        initView();
    }

    public BaseRefreshAbsListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        initAttrs(attrs);
        initView();
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (onScrollListener != null) {
            onScrollListener.onScrollStateChanged(view, scrollState);
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
                         int totalItemCount) {
        //这段代码的作用就是解决下拉刷新时停留在第一项的时候会出现第一项显示不全的问题
        if (firstVisibleItem != 0) {
            ptrLayout.setEnabled(false);
        } else if (view.getChildCount() > 0 && view.getFirstVisiblePosition() == 0
                && view.getChildAt(0).getTop() >= 0) {
            ptrLayout.setEnabled(true);
        } else {
            ptrLayout.setEnabled(false);
        }

        if ((((totalItemCount - firstVisibleItem - visibleItemCount) == ITEM_LEFT_TO_LOAD_MORE) || (totalItemCount - firstVisibleItem - visibleItemCount) == 0 && totalItemCount > visibleItemCount) && !isLoadingMore) {
            isLoadingMore = true;
            if (onMoreListener != null) {
                if (progress != null) {
                    if (totalItemCount <= 2) {
                        progress.setVisibility(GONE);
                    } else {
                        progress.setVisibility(VISIBLE);
                    }
                }

                onMoreListener.onMoreAsked(baseListView.getAdapter().getCount(), ITEM_LEFT_TO_LOAD_MORE, firstVisibleItem);
            }
        }
        if (onScrollListener != null) {
            onScrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
        }
    }

    /**
     * 初始化AbsListView的布局
     *
     * @param view 布局
     */
    protected abstract void initAbsListView(View view);

    /**
     * 清除数据
     */
    public abstract void clear();

    /**
     * 隐藏加载框
     */
    public void hideProgress() {
        progress.setVisibility(View.GONE);
    }

    /**
     * 初始化View
     */
    private void initView() {
        // 如果在自定义控件的构造函数或者其他绘制相关地方使用系统依赖的代码，会导致可视化编辑器无法报错，因此需要添加上这句判断
        if (isInEditMode()) {
            return;
        }

        View view = LayoutInflater.from(getContext()).inflate(commonListViewMainLayoutId, this);
        ptrLayout = (CustomSwipeRefreshLayout) view.findViewById(R.id.ptr_layout);
        ptrLayout.setEnabled(false);

        // OPTIONAL:  Enable the top progress bar
        ptrLayout.enableTopProgressBar(true);

        // OPTIONAL:  keep the refreshing head movable(true stands for fixed) on the top
        ptrLayout.enableTopRefreshingHead(false);

        // OPTIONAL:  Timeout to return to original state when the swipe motion stay in the same position
        ptrLayout.setReturnToOriginalTimeout(200);

        // OPTIONAL:  Timeout to show the refresh complete information on the refreshing head.
        ptrLayout.setRefreshCompleteTimeout(50);

        emptyView = (ViewStub) view.findViewById(R.id.vs_empty);
        emptyView.setLayoutResource(R.layout.view_status_empty);
        emptyView.inflate();

        emptyView.setVisibility(GONE);

        initAbsListView(view);
    }

    /**
     * 显示ListView
     */
    public void showList() {
        baseListView.setVisibility(VISIBLE);
    }

    /**
     * 隐藏ListView
     */
    public void hideList() {
        baseListView.setVisibility(GONE);
    }

    /**
     * 获取Adapter
     *
     * @return ListAdapter
     */
    public ListAdapter getAdapter() {
        return baseListView.getAdapter();
    }

    /**
     * 获取下拉刷新的布局
     *
     * @return 下拉刷新的布局
     */
    public CustomSwipeRefreshLayout getSwipeToRefresh() {
        return ptrLayout;
    }

    /**
     * 获取ListView
     *
     * @return AbsListView
     */
    public AbsListView getListView() {
        return baseListView;
    }

    /**
     * 显示底部加载框
     */
    public void showProgress() {
        hideList();
        if (emptyId != 0) {
            emptyView.setVisibility(INVISIBLE);
            if (progress != null) {
                progress.setVisibility(VISIBLE);
            }
        }
    }

    /**
     * 获取第一个可见元素的位置
     *
     * @return 可见元素的位置
     */
    private int getFirstVisiblePosition() {
        return baseListView.getFirstVisiblePosition();
    }

    /**
     * 是否加载更多
     *
     * @return 是否加载更多，true表示是，false表示不是
     */
    public boolean isLoadingMore() {
        return isLoadingMore;
    }

    /**
     * 判断加载框是否显示
     *
     * @return 是否显示
     */
    public boolean isProgressShow() {
        return progress.getVisibility() == VISIBLE;
    }

    /**
     * 删除加载更多的监听的时间
     */
    public void removeMoreListener() {
        onMoreListener = null;
    }

    /**
     * 设置空白页是否可见
     *
     * @param visibility 可见性
     */
    public void setEmptyViewVisibility(int visibility) {
        emptyView.setVisibility(visibility);
    }

    /**
     * 初始化参数
     *
     * @param attrs 参数集
     */
    private void initAttrs(AttributeSet attrs) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.commonlistview);
        try {
            clipToPadding = a.getBoolean(R.styleable.commonlistview_commonlv__listClipToPadding, false);
            divider = a.getColor(R.styleable.commonlistview_commonlv__listDivider, 0);
            dividerHeight = a.getDimension(R.styleable.commonlistview_commonlv__listDividerHeight, 0.0f);
            padding = (int) a.getDimension(R.styleable.commonlistview_commonlv__listPadding, -1.0f);
            paddingTop = (int) a.getDimension(R.styleable.commonlistview_commonlv__listPaddingTop, 0.0f);
            paddingBottom = (int) a.getDimension(R.styleable.commonlistview_commonlv__listPaddingBottom, 0.0f);
            paddingLeft = (int) a.getDimension(R.styleable.commonlistview_commonlv__listPaddingLeft, 0.0f);
            paddingRight = (int) a.getDimension(R.styleable.commonlistview_commonlv__listPaddingRight, 0.0f);
            scrollbarStyle = a.getInt(R.styleable.commonlistview_commonlv__scrollbarStyle, -1);
            selector = a.getResourceId(R.styleable.commonlistview_commonlv__listSelector, 0);
            emptyId = a.getResourceId(R.styleable.commonlistview_commonlv__empty, 0);

            initCustomAttrs(attrs, a);
        } finally {
            a.recycle();
        }
    }

    /**
     * 初始化自定义的参数集
     *
     * @param attrs 参数集
     * @param array 属性数组
     */
    protected void initCustomAttrs(AttributeSet attrs, TypedArray array) {
    }

    /**
     * 设置滑动监听事件
     *
     * @param listener 监听事件
     */
    public void setOnScrollListener(AbsListView.OnScrollListener listener) {
        onScrollListener = listener;
    }

    /**
     * 设置下拉刷新监听事件
     *
     * @param listener 监听事件
     */
    public void setRefreshListener(CustomSwipeRefreshLayout.OnRefreshListener listener) {
        ptrLayout.setEnabled(true);
        ptrLayout.setOnRefreshListener(listener);
    }

    /**
     * 设置点击事件
     *
     * @param listener 点击事件监听
     */
    public void setOnItemClickListener(AdapterView.OnItemClickListener listener) {
        baseListView.setOnItemClickListener(listener);
    }

    /**
     * 设置长按的点击事件
     *
     * @param listener 长按点击事件监听
     */
    public void setOnItemLongClickListener(AdapterView.OnItemLongClickListener listener) {
        baseListView.setOnItemLongClickListener(listener);
    }

    /**
     * 设置触摸监听事件
     *
     * @param listener 触摸监听事件
     */
    public void setOnTouchListener(OnTouchListener listener) {
        baseListView.setOnTouchListener(listener);
    }

    /**
     * 设置Adapter
     *
     * @param adapter ListAdapter
     */
    public void setAdapter(ListAdapter adapter) {
        if (emptyView != null) {
            baseListView.setEmptyView(emptyView);
        }
        baseListView.setVisibility(View.VISIBLE);
        ptrLayout.setRefreshing(false);

        adapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                isLoadingMore = false;
                ptrLayout.setRefreshing(false);
                if (baseListView.getAdapter().getCount() == 0 && emptyView != null) {
                    emptyView.setVisibility(VISIBLE);
                    if (progress != null) {
                        progress.setVisibility(GONE);
                    }
                } else if (emptyView != null) {
                    emptyView.setVisibility(GONE);
                }
            }
        });
        if ((adapter == null || adapter.getCount() == 0) && emptyView != null) {
            if (progress != null) {
                progress.setVisibility(GONE);
            }
            if (((ListView) baseListView).getHeaderViewsCount() > 0) {
                emptyView.setVisibility(View.GONE);
                return;
            }
            emptyView.setVisibility(VISIBLE);
        }
    }

    /**
     * 设置加载更多的监听事件
     *
     * @param listener 监听事件
     */
    public void setOnMoreListener(OnMoreListener listener) {
        onMoreListener = listener;
    }

    /**
     * 设置到第几项就开始加载
     *
     * @param max 到第几项就开始加载更多
     */
    public void setNumberBeforeMoreIsCalled(int max) {
        ITEM_LEFT_TO_LOAD_MORE = max;
    }

    /**
     * 设置是否加载更多
     *
     * @param isLoadingMore 是否加载更多
     */
    public void setLoadingMore(boolean isLoadingMore) {
        this.isLoadingMore = isLoadingMore;
    }

    /**
     * 设置进度加载框
     *
     * @param progressView 进度加载框
     */
    protected void setProgressView(View progressView) {
        progress = progressView;
        progress.setVisibility(GONE);
    }

    /**
     * 设置加载更多
     *
     * @param listener 监听事件
     * @param max      到第几项就开始加载更多
     */
    public void setupMoreListener(OnMoreListener listener, int max) {
        this.onMoreListener = listener;
        ITEM_LEFT_TO_LOAD_MORE = max;
    }

    /**
     * 设置下拉刷新的颜色
     *
     * @param col1 颜色1
     * @param col2 颜色2
     * @param col3 颜色3
     * @param col4 颜色4
     */
    public void setRefreshingColor(int col1, int col2, int col3, int col4) {
        ptrLayout.setProgressBarColorRes(col1, col2, col3, col4);
    }
}
