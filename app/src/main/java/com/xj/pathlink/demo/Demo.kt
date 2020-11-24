package com.xj.pathlink.demo

import android.content.Context
import android.widget.Toast
import cn.mama.com.LinkPath

/**
 * Created by xiej on 2020/11/20
 */
class Demo {

    @LinkPath(value = "showToast1")
    fun showToast(context: Context, value: String) {
        Toast.makeText(context, "成功调用了showToast", Toast.LENGTH_SHORT).show()
    }

}