package com.zwb.ui.refreshlistview.ui;

/**
 * 加载更多的监听类
 * Created by wenbiao_zheng on 2014/10/27.
 *
 * @author wenbiao_zheng
 */
public interface OnMoreListener {
    /**
     * 加载更多
     *
     * @param numberOfItems    Item的数量
     * @param numberBeforeMore 要求加载的位置
     * @param currentItemPos   当前的位置
     */
    public void onMoreAsked(int numberOfItems, int numberBeforeMore, int currentItemPos);
}
