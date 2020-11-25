# PathLink
Scheme方式调用方法、唤起app/页面(q://update?value=1)


#### 项目应用场景：
统一以下几种常见常见的跳转方式
1、前端页面需要跳转APP内的原生页面
2、不同应用唤起APP后需要跳转原生页面，如微信分享链接
3、APP某个方法需要对外提供跳转服务  

#### 实现方式
使用Android APT 在需要对外提供服务的方法前添加注解，在编译期生成<方法名，类路径信息>的路由表
在应用层需要唤起或调用方法时遍历路由表，找到对应的类，使用反射生成实体，并调用方法，完成唤起  

#### 使用方式
在app.gradle文件中导入项目
```
    implementation project(':path-annotation')
    kapt project(':path-compiler')
```

在需要对外提供服务的方法上面添加`LinkPath()`注解即可
```
public class Test {

    @LinkPath(value = "print1")
    public void print() {
        Log.i("xj", "--->调用了print方法");
    }

}
```  


调用方式：
```
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppPathLinkUtils.init();
    }

    /**
    *直接用url进行跳转
    */
    public void print(View view) {
        appPathLinkUtils.handlerUrl(this, "q://print1");
    }

    public void add(View view) {
        appPathLinkUtils.handlerUrl(this, "q://add?i=8");
    }
}
```

[原理介绍](https://www.jianshu.com/p/34ca8f7632e8)





