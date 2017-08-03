package com.seekting.okhttpdoc;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by seekting on 17-8-3.
 */

public class LocalServerActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        OkHttpClient okHttpClient = builder.build();
        Request.Builder requestBuilder = new Request.Builder();
        requestBuilder.url("http://192.168.1.108:8080");
        Call call = okHttpClient.newCall(requestBuilder.build());
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("seekting", "LocalServerActivity.onFailure()", e);

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d("seekting", "LocalServerActivity.onResponse()" + response.body().string());

            }
        });

    }
}
