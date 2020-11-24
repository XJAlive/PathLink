package com.xj.pathlink

import android.content.Context
import android.net.Uri
import android.util.Log
import cn.mama.com.PathMeta
import java.lang.reflect.Method
import java.util.*

/**
 * Created by xiej on 2020/11/20
 */
class AppPathLinkUtils {

    companion object {
        var pathRouter = HashMap<String, PathMeta>()

        /**
         * 初始化PathLink组件相关业务，生成路由表
         */
        @JvmStatic
        fun init() {
            try {
                val clazz = Class.forName("com.xj.pathlink.PathLinkUtils")
                clazz.newInstance()
                val methods = clazz.declaredMethods
                for (i in methods.indices) {
                    if (methods[i].name == "initRouter") {
                        pathRouter =
                            methods[i].invoke(clazz.newInstance()) as HashMap<String, PathMeta>
                        val iterator: Iterator<*> = pathRouter.entries.iterator()
                        while (iterator.hasNext()) {
                            val entry = iterator.next() as Map.Entry<*, *>
                            val key = entry.key!!
                            val value = entry.value!!
                            Log.i("xj", "pathRouter路由内容：key=$key , value=$value")
                        }
                        break
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun handlerUrl(context: Context?, url: String) {
        try {
            val uri = Uri.parse(url)
            //匹配路由表，找到调用对应方法需要的所有信息
            val pathMeta = pathRouter[uri.host] ?: return
            val className = pathMeta.path
            if (className == null || className.isEmpty()) {
                Log.w(
                    AppPathLinkUtils::class.java.simpleName,
                    "AppLink跳转出错,Url = $url , className = null"
                )
                return
            }
            val clazz = Class.forName(className)

            for (method in clazz.declaredMethods) {
                if (method.name != pathMeta.methodName) {
                    continue
                }

                //获取该方法需要的所有参数名称和顺序
                val array = pathMeta.parameter.split(",").filter { !it.isBlank() }
                if (array.isNullOrEmpty()) {
                    //反射调用无参方法
                    method.invoke(clazz.newInstance())
                } else {
                    val parameterList = mutableListOf<Any>()
                    for (parameter in array) {
                        //从Scheme中获取前端传过来的值
                        val value = uri.getQueryParameter(parameter)
                        if (!value.isNullOrBlank()) {
                            parameterList.add(value)
                        }
                    }

                    if (isDeliverContext(method)) {
                        //如果需要用到Context，请把Context作为方法的第一个参数!!!!
                        context?.let { parameterList.add(0, it) }
                    }
                    //反射调用有参方法
                    method.invoke(clazz.newInstance(), *parameterList.toTypedArray())
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.w(
                AppPathLinkUtils::class.java.simpleName,
                "AppLink跳转出错,Url = $url , message = ${e.message ?: ""}"
            )
        }
    }

    /**
     * 是否需要在参数中传递context
     */
    private fun isDeliverContext(method: Method): Boolean {
        var isDeliverContext = false
        for (parameterType in method.parameterTypes) {
            if (Context::class.java.isAssignableFrom(parameterType)) {
                isDeliverContext = true
            }
        }
        return isDeliverContext
    }

}