package com.zwb.ui.refreshlistview.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.zwb.ui.refreshlistview.utils.LogUtil;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * List的底部操作栏
 * Created by wenbiao_zheng on 2015/1/1.
 *
 * @author wenbiao_zheng
 */
public class BaseBottomOperation extends LinearLayout {
    private Context context;

    public BaseBottomOperation(Context context) {
        super(context);

        this.context = context;
    }

    public BaseBottomOperation(Context context, AttributeSet attrs) {
        super(context, attrs);

        this.context = context;
    }

    @SuppressLint("NewApi")
    public BaseBottomOperation(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        this.context = context;
    }

    @SuppressLint("NewApi")
    public BaseBottomOperation(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        this.context = context;
    }

    /**
     * 初始化布局
     *
     * @param id 布局id
     */
    public void init(int id) {
        LayoutInflater.from(context).inflate(id, this);
    }

    /**
     * Item的点击事件，适合没有参数的方法的调用
     *
     * @param view       设置点击事件的view
     * @param object     声明方法的类的对象
     * @param methodName 方法名
     */
    public void setItemClick(View view, final Object object, final String methodName) {
        final Class[] methodParam = null;
        final Object[] invokeParam = null;
        view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Class classObj = object.getClass();
                try {
                    Method method = classObj.getMethod(methodName, methodParam);
                    method.invoke(object, invokeParam);
                } catch (NoSuchMethodException e) {
                    LogUtil.e(e.toString());
                } catch (InvocationTargetException e) {
                    LogUtil.e(e.toString());
                } catch (IllegalAccessException e) {
                    LogUtil.e(e.toString());
                }
            }
        });
    }
}
