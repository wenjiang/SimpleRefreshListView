package com.zwb.ui.refreshlistview.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;

import java.lang.reflect.Constructor;

/**
 * Created by pc on 2015/4/7.
 */
public class ViewUtil {
    public static View reflectView(@NonNull Context context, @NonNull String className) {
        try {
            Class<? extends View> clazz = context.getClassLoader().loadClass(className).asSubclass(View.class);
            Constructor<? extends View> constructor = clazz.getConstructor(new Class[]{Context.class});
            constructor.setAccessible(true);
            View view = constructor.newInstance(context);
            return view;
        } catch (Exception e) {
            LogUtil.e(e.toString());
            return null;
        }
    }
}
