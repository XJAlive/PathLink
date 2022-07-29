package cn.mama.com;

import java.io.Serializable;

/**
 * Created by xiej on 2020/11/23
 */
public class PathMeta implements Serializable {

    private String path;//包名路径
    private String parameter;//参数集合
    private String methodName;//在类中原方法名
    private Class<?> className;//当前类，防止混淆之后找不到路径

    public PathMeta() {
    }

    public Class<?> getClassName() {
        return className;
    }

    public void setClassName(Class<?> className) {
        this.className = className;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getParameter() {
        return parameter;
    }

    public void setParameter(String parameter) {
        this.parameter = parameter;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    @Override
    public String toString() {
        return "PathMeta{" +
            "path='" + path + '\'' +
            ", parameter='" + parameter + '\'' +
            ", methodName='" + methodName + '\'' +
            '}';
    }
}
