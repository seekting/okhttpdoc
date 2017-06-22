package com.seekting.okhttpdoc;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.IOException;

import okhttp3.Call;
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
                request();

            }
        }).start();
        String ids = null;
        testNullable(ids);
    }

    private void request() {
        OkHttpClient okHttpClient = new OkHttpClient();
        Request.Builder builder = new Request.Builder();
        builder.url("http://www.baidu.com");
        Call call = okHttpClient.newCall(builder.build());
        try {
            Response response = call.execute();
            String str = response.body().string();
            Log.d("seekting", "OkHttpActivity.run()" + str);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void testNullable(@NonNull String ids) {
        ids.toCharArray();
    }
}
