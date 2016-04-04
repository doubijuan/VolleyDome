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
