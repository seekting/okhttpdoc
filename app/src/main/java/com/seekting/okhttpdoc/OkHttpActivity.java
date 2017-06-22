package com.seekting.okhttpdoc;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class OkHttpActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient okHttpClient = new OkHttpClient();
                Request.Builder builder = new Request.Builder();
                builder.url("http://www.baidu.com");
                try {
                    Response response = okHttpClient.newCall(builder.build()).execute();
                    String str = response.body().string();
                    Log.d("seekting", "str" + str);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
