package com.xj.pathlink.share;

import android.util.Log;
import cn.mama.com.LinkPath;

/**
 * Created by xiej on 2020/11/19
 */
public class Applink {

    @LinkPath(alias = "")
    public static void add(String i) {
        Log.i("xj", "------->反射调用了add方法,传参i=" + i);
    }

}
