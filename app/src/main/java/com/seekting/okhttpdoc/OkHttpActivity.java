package com.seekting.okhttpdoc;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nullable;

import okhttp3.Cache;
import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.BufferedSink;


public class OkHttpActivity extends Activity {

    private String url = "http://testpolamall.i.360overseas.com/NoEncrypt/mall";
//    private String url = "http://username:password@example.com/path";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new Thread(new Runnable() {
            @Override
            public void run() {
                requestPost();

            }
        }).start();

        Request.Builder builder = new Request.Builder();
        builder.url(url);
//        Log.d("seekting","OkHttpActivity.onCreate()"+builder.url);
    }

    private OkHttpClient newOkHttpClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.cache(new Cache(getCacheDir(), 5 * 1024 * 1024));
        builder.readTimeout(2000, TimeUnit.MILLISECONDS);
        builder.writeTimeout(2000, TimeUnit.MILLISECONDS);
        builder.connectTimeout(2000, TimeUnit.MILLISECONDS);
        builder.retryOnConnectionFailure(true);
        OkHttpClient okHttpClient = builder.build();
        return okHttpClient;
    }

    private void requestPost() {

        OkHttpClient okHttpClient = newOkHttpClient();
        final String body = "{\"version\":\"6\",\"data_ver\":\"\"}";
        Request.Builder builder = new Request.Builder();
        builder.url(url);
        builder.addHeader("appInfo", "pola.cam.video.android|CN|zh_CN|20714382667|104488|2");
//        builder.addHeader("Content-Length", "1024");
        builder.method("POST", new RequestBody() {
            @Nullable
            @Override
            public MediaType contentType() {
                return MediaType.parse("application/x-www-form-urlencoded");
            }

            @Override
            public long contentLength() throws IOException {
                //此处返回-1
                return super.contentLength();
            }

            @Override
            public void writeTo(BufferedSink sink) throws IOException {
                sink.write(body.getBytes());

            }
        });
        Call call = okHttpClient.newCall(builder.build());
        try {
            Response response = call.execute();

            String str = response.body().string();
            Log.d("seekting", "OkHttpActivity.run()" + builder.url.redact());
            Log.d("seekting", "OkHttpActivity.run()" + str);
        } catch (IOException e) {
            e.printStackTrace();
        }
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

}
