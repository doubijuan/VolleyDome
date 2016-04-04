# Android 网络通信框架Volley的二次封装
##在Android开发中不可避免地需要用到网络访问，多数情况下会使用HTTP协议来发送和接收网络数据。Android系统中主要提供了两种方式来进行HTTP通信：HttpURLConnection和HttpClient。

##HttpURLConnection和HttpClient的用法还是稍微有些复杂，如果没有进行封装的话，很容易写出不少重复代码。这时就出现很多Android网络通信框架，比如AsyncHttpClient，它把HTTP所有的通信细节全部封装在了内部，我们只需要简单调用几行代码就可以完成通信操作了。比如Universal-Image-Loader，它使得在界面上显示网络图片的操作变得极度简单，因为Universal-Image-Loader已经把一切都做好了，我们不用关心如何从网络上获取图片，也不用关心开启线程、回收图片资源等。

##而当前十分热门的Volley网络框架，也是在2013年Google I/O大会上推出了一个新的网络通信框架。Volley把AsyncHttpClient和Universal-Image-Loader的优点集于了一身，既可以像AsyncHttpClient一样非常简单地进行HTTP通信，也可以像Universal-Image-Loader一样轻松加载网络上的图片。

##本身已有良好封装的Volley确实给程序开发带来了很多便利与快捷，但我们仍然可以对Volley进行二次封装。 
##由于需要用到网络访问，所以不要忘记添加权限哦
···Java
<uses-permission android:name="android.permission.INTERNET" />
···
##首先我们创建一个类继承Application并在AndroidManifest.xml文件中的application标签中进行注册，在我们创建的Application中通过Volley.newRequestQueue(getApplicationContext())获取 RequestQueue对象。
···Java
package com.xiaolijuan.volleydome;

import android.app.Application;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * @author: xiaolijuan
 * @description:
 * @date: 2016-03-11
 * @time: 11:34
 */
public class AppApplication extends Application {

    public static RequestQueue queues;

    @Override
    public void onCreate() {
        super.onCreate();
        queues = Volley.newRequestQueue(getApplicationContext());
    }

    public static RequestQueue getHttpQueues() {
        return queues;
    }
}
```
##HttpUtils.java
···Java
package com.xiaolijuan.volleydome.network;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request.Method;
import com.android.volley.toolbox.StringRequest;
import com.xiaolijuan.volleydome.AppApplication;

import java.util.Map;

/**
 * @author: xiaolijuan
 * @description: 使用Volley访问Http请求管理的工具类
 * @date: 2016-03-15
 * @time: 15:39
 */
public class HttpUtils {
    public static StringRequest stringRequest;

    /**
     * Get请求，获得返回数据
     *
     * @param context 上下文
     * @param url     发送请求的URL
     * @param tag     TAG标签
     * @param vif     请求回调的接口（请求成功或者失败）
     */
    public static void doGet(Context context, String url, String tag, VolleyInterface vif) {
        Log.e("发送Get请求的URL:", url);
        //获取全局的请求队列并把基于Tag标签的请求全部取消，防止重复请求
        AppApplication.getHttpQueues().cancelAll(tag);
        //实例化StringRequest
        stringRequest = new StringRequest(Method.GET, url, vif.loadingListener(), vif.errorListener());
        // 设置标签
        stringRequest.setTag(tag);
        // 将请求添加至队列里面
        AppApplication.getHttpQueues().add(stringRequest);
        // 启动
        AppApplication.getHttpQueues().start();
    }

    /**
     * 向指定 URL 发送POST方法的请求
     *
     * @param context 上下文
     * @param url     发送请求的URL
     * @param tag     TAG标签
     * @param params  请求参数，请求参数应该是Hashmap类型
     * @param vif     请求回调的接口（请求成功或者失败）
     */
    public static void doPost(Context context, String url, String tag, final Map<String, String> params,
                              VolleyInterface vif) {
        Log.e("发送Get请求的URL:", url);
        //获取全局的请求队列并把基于Tag标签的请求全部取消，防止重复请求
        AppApplication.getHttpQueues().cancelAll(tag);
        stringRequest = new StringRequest(url, vif.loadingListener(), vif.errorListener()) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return params;
            }
        };
        // 设置标签
        stringRequest.setTag(tag);
        // 加入队列
        AppApplication.getHttpQueues().add(stringRequest);
        // 启动
        AppApplication.getHttpQueues().start();
    }
}
```
##VolleyInterface.java
```Java
package com.xiaolijuan.volleydome.network;

import android.content.Context;
import android.util.Log;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;

/**
 * @author: xiaolijuan
 * @description: 请求成功或失败的接口回调
 * @date: 2016-03-15
 * @time: 15:39
 */
public abstract class VolleyInterface {
    /**
     * 上下文
     */
    public Context mContext;
    /**
     * 请求成功监听
     */
    public static Listener<String> mListener;
    /**
     * 请求失败监听
     */
    public static ErrorListener mErrorListtener;

    public VolleyInterface(Context context, Listener<String> listener, ErrorListener errorListener) {
        this.mContext = context;
        this.mListener = listener;
        this.mErrorListtener = errorListener;
    }

    /**
     * 请求成功的抽象类
     *
     * @param result
     */
    public abstract void onSuccess(String result);

    /**
     * 请求失败的抽象类
     *
     * @param error
     */
    public abstract void onError(VolleyError error);

    /**
     * 请求成功监听
     *
     * @return
     */
    public Listener<String> loadingListener() {
        mListener = new Listener<String>() {

            @Override
            public void onResponse(String result) {
                Log.e("请求成功返回的数据：", result);
                onSuccess(result);
            }
        };
        return mListener;
    }

    /**
     * 请求失败监听
     *
     * @return
     */
    public ErrorListener errorListener() {
        mErrorListtener = new ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("请求失败返回的数据：", error.toString());
                onError(error);
            }
        };
        return mErrorListtener;
    }
}
```
##由于代码写得很详细，我这里就不再一一解释啦
```Java
package com.xiaolijuan.volleydome;

import android.app.Activity;
import android.os.Bundle;

import com.android.volley.VolleyError;
import com.xiaolijuan.volleydome.network.HttpUtils;
import com.xiaolijuan.volleydome.network.VolleyInterface;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        doGet();
    }

    private void doGet() {
        String url = "";
        String tag = "GET";
        HttpUtils.doGet(getApplicationContext(), url, tag,
                new VolleyInterface(getApplicationContext(), VolleyInterface.mListener, VolleyInterface.mErrorListtener) {

                    @Override
                    public void onSuccess(String result) {
                    }

                    @Override
                    public void onError(VolleyError error) {
                    }
                });
    }

    /**
     * volley和activity的关联
     */
    @Override
    protected void onStop() {
        super.onStop();
        // 在停止的时候也把tag标签的网络请求给停掉
        AppApplication.getHttpQueues().cancelAll("");
    }
}
```
![源码请点击这里：Android 网络通信框架Volley的二次封装](http://download.csdn.net/detail/qq_20785431/9479812)



