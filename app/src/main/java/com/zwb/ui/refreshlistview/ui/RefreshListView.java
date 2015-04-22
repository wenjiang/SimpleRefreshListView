package com.zwb.ui.refreshlistview.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.zwb.ui.refreshlistview.R;
import com.zwb.ui.refreshlistview.exception.BaseListViewException;


/**
 * 自定义的ListView，用于下拉刷新
 * Created by wenbiao_zheng on 2014/10/27.
 *
 * @author wenbiao_zheng
 */
public class RefreshListView extends BaseRefreshAbsListView {
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
    }

    @Override
    protected void initAbsListView(View view) {
        baseListView = (ListView) view.findViewById(R.id.lv_base);

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
     * 添加HeaderView
     *
     * @param headerView ListView的HeaderView
     */
    public void addHeaderView(View headerView) {
        ((ListView) baseListView).addHeaderView(headerView);
    }

    public void addFooterView(View footerView) throws BaseListViewException {
        if (progressViewId != 0 && ((ListView) baseListView).getFooterViewsCount() == 1) {
            throw new BaseListViewException("You must call this method before setupMoreListener");
        }

        ((ListView) baseListView).addFooterView(footerView);
    }

    /**
     * 删除FooterView
     *
     * @param footerView FooterView
     */
    public void removeFooterView(View footerView) {
        ((ListView) baseListView).removeFooterView(footerView);
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
        bottomOperationView.setVisibility(visibility);
    }

    /**
     * 初始化底部操作栏
     *
     * @param id 底部操作栏的布局id
     * @return 底部操作栏组件
     */
    private BaseBottomOperationView initBottom(int id) {
        bottomOperationView.setVisibility(View.VISIBLE);
        bottomOperationView.init(id);
        return bottomOperationView;
    }
}
