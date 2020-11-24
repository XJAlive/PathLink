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

//            Constructor<?>[] constructors = clazz.getConstructors();
//            for (Constructor constructor : constructors) {
//                String constructorName = constructor.getName();
//                System.out.print(constructorName + "(");
//                //获得构造函数的所有参数
//                Class[] classType = constructor.getParameterTypes();
//                for (int i = 0; i < classType.length; i++) {
//                    if (i == classType.length - 1) {
//                        System.out.print(classType[i].getName());
//                    } else {
//                        System.out.print(classType[i].getName() + ",");
//                    }
//                }
//                System.out.println(")");
//            }
            for (method in clazz.declaredMethods) {
                if (method.name != pathMeta.methodName) {
                    continue
                }

                val array = pathMeta.parameter.split(",").filter { !it.isBlank() }
                if (array.isNullOrEmpty()) {
                    //直接调用无参方法
                    method.invoke(clazz.newInstance())
                } else {
                    val parameterList = mutableListOf<Any>()
                    for (parameter in array) {
                        val value = uri.getQueryParameter(parameter)
                        if (!value.isNullOrBlank()) {
                            parameterList.add(value)
                        }
                    }

                    if (isDeliverContext(method)) {
                        context?.let { parameterList.add(0, it) }
                    }
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