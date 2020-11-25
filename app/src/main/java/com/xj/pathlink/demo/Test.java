package com.xj.pathlink.demo;

import android.util.Log;
import cn.mama.com.LinkPath;

/**
 * Created by xiej on 2020/11/19
 */
public class Test {

    @LinkPath(alias = "print1")
    public void print() {
        Log.i("xj", "--->调用了print方法");
    }

}
