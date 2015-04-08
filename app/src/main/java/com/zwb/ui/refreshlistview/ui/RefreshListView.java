package com.zwb.ui.refreshlistview.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.zwb.ui.refreshlistview.R;
import com.zwb.ui.refreshlistview.utils.ViewUtil;


/**
 * 自定义的ListView，用于下拉刷新
 * Created by wenbiao_zheng on 2014/10/27.
 *
 * @author wenbiao_zheng
 */
public class RefreshListView extends BaseRefreshAbsListView {

    private String commonListViewBottomView;

    public RefreshListView(Context context) {
        super(context);
    }

    public RefreshListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RefreshListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void initCustomAttrs(AttributeSet attrs, TypedArray array) {
        super.initCustomAttrs(attrs, array);

        commonListViewMainLayoutId = array.getResourceId(R.styleable.commonlistview_commonlv_mainLayoutID, R.layout.widget_refresh_listview);
        // TODO: commonListViewBottomView在Attr上定义为String属性，没有相应的IDE提示
        commonListViewBottomView = array.getString(R.styleable.commonlistview_commonlv_bottom_view);
    }

    @Override
    protected void initAbsListView(View view) {
        baseListView = (ListView) view.findViewById(R.id.lv_base);
        bottomOperation = (BaseBottomOperation) view.findViewById(R.id.cbo_bottom_operation);
        bottomOperation.setVisibility(View.GONE);

        if (baseListView != null) {
            baseListView.setClipToPadding(clipToPadding);

            //TODO 这里的divider是资源id
            ((ListView) getListView()).setDivider(new ColorDrawable(divider));
            getListView().setDividerHeight((int) dividerHeight);

            baseListView.setOnScrollListener(this);
            if (selector != 0) {
                baseListView.setSelector(selector);
            }
            if (padding != -1.0f) {
                baseListView.setPadding(padding, padding, padding, padding);
            } else {
                baseListView.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
            }

            if (scrollbarStyle != -1) {
                baseListView.setScrollBarStyle(scrollbarStyle);
            }
        }

        ptrLayout.setProgressBarColorRes(R.color.holo_orange_light, R.color.holo_blue_light, R.color.holo_green_light, R.color.holo_red_light);
    }

    @Override
    public void setAdapter(ListAdapter adapter) {
        getListView().setAdapter(adapter);
        super.setAdapter(adapter);
    }

    @Override
    public void clear() {
        getListView().setAdapter(null);
    }

    @Override
    public ListView getListView() {
        return (ListView) baseListView;
    }

    /**
     * 获取HeaderView的数量
     *
     * @return HeaderView的数量
     */
    public int getHeaderViewsCount() {
        return ((ListView) baseListView).getHeaderViewsCount();
    }

    /**
     * 添加进度条
     *
     * @param progressView 进度加载框
     */
    public void addProgressView(View progressView) {
        ((ListView) baseListView).addFooterView(progressView);
        setProgressView(progressView);
    }

    /**
     * 添加底部的View
     *
     * @param footerView 底部的View
     */
    public void addFooterView(View footerView) {
        ((ListView) baseListView).addFooterView(footerView);
    }


    /**
     * 添加HeaderView
     *
     * @param headerView ListView的HeaderView
     */
    public void addHeaderView(View headerView) {
        ((ListView) baseListView).addHeaderView(headerView);
    }

    /**
     * 删除FooterView
     *
     * @param view FooterView
     */
    public void removeFooterView(View view) {
        ((ListView) baseListView).removeFooterView(view);
    }

    /**
     * 设置选中状态
     */
    public void setSelection(int position) {
        ((ListView) baseListView).setSelection(position);
    }

    /**
     * 设置垂直方向的滚动条是否可见
     *
     * @param enabled 是否可见
     */
    public void setVerticalScrollBarEnabled(boolean enabled) {
        ((ListView) baseListView).setVerticalScrollBarEnabled(enabled);
    }

    /**
     * 设置水平方向的滚动条是否可见
     *
     * @param enabled 是否可见
     */
    public void setHorizontalScrollBarEnabled(boolean enabled) {
        ((ListView) baseListView).setHorizontalScrollBarEnabled(enabled);
    }

    /**
     * 设置底部操作栏是否可见
     *
     * @param visibility 可见性
     */
    public void setBottomVisibility(int visibility) {
        bottomOperation.setVisibility(visibility);
    }

    /**
     * 初始化底部操作栏
     *
     * @param id 底部操作栏的布局id
     * @return 底部操作栏组件
     */
    public BaseBottomOperation initBottom(int id) {
        bottomOperation.setVisibility(View.VISIBLE);
        bottomOperation.init(id);
        return bottomOperation;
    }

    /**
     * 初始化底部操作栏
     * 使用反射生成底部View
     */
    public void initBottom() {
        View view = ViewUtil.reflectView(getContext(), commonListViewBottomView);
        ViewGroup.LayoutParams params = bottomOperation.getLayoutParams();
        bottomOperation.addView(view, params);
        bottomOperation.setVisibility(View.VISIBLE);
    }
}
