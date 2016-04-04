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
