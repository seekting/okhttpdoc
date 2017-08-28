package com.seekting.okhttpdoc;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.seekting.demo_lib.Demo;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by seekting on 17-5-18.
 */
@Demo(title = "SPDYActivity", desc = "")
public class SPDYActivity extends Activity {

    public static final boolean DEBUG = AppEnv.bAppdebug;
    public static final String TAG = SPDYActivity.class.getSimpleName();
    public static final String URL = "http://192.168.31.163:8080/test";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (DEBUG) {
            Log.d(TAG, "onCreate.");
        }
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        OkHttpClient client = builder.build();
        Request.Builder builder1 = new Request.Builder();
        builder1.url(URL);
        Call c = client.newCall(builder1.build());
        c.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("seekting", "SPDYActivity.onFailure()", e);

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String s = response.body().string();

                Log.d("seekting", "SPDYActivity.onResponse()" + s);

            }
        });
         c = client.newCall(builder1.build());
         c.enqueue(new Callback() {
             @Override
             public void onFailure(Call call, IOException e) {

             }

             @Override
             public void onResponse(Call call, Response response) throws IOException {
                 String s = response.body().string();

                 Log.d("seekting", "SPDYActivity.onResponse()2" + s);
             }
         });


    }
}
