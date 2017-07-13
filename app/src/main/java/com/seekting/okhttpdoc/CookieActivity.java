package com.seekting.okhttpdoc;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.seekting.demo_lib.Demo;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by seekting on 17-7-13.
 */
@Demo(title = "cookie的实现", desc = "自定义cookie的实现")
public class CookieActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Button button = new Button(this);
        button.setText("发请求");
        setContentView(button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();
                httpClientBuilder.cookieJar(new CookieJar() {
                    @Override
                    public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
                        Log.d("seekting", "CookieActivity.saveFromResponse()" + url);

                        for (Cookie cookie : cookies) {
                            Log.d("seekting", "CookieActivity.saveFromResponse()" + cookie.value());

                        }

                    }

                    @Override
                    public List<Cookie> loadForRequest(HttpUrl url) {
                        return Collections.emptyList();
                    }
                });
                OkHttpClient okHttpClient = httpClientBuilder.build();
                Request.Builder reqBuilder = new Request.Builder();
                reqBuilder.addHeader("Cookie","name=value");
                reqBuilder.url("http://10.9.178.241:8081/login");
                Call call = okHttpClient.newCall(reqBuilder.build());
                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {

                        Log.d("seekting", "CookieActivity.onFailure()", e);
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        ResponseBody body = response.body();
                        Log.d("seekting", "CookieActivity.onClick()" + body.string());
                    }
                });


            }
        });
    }
}
