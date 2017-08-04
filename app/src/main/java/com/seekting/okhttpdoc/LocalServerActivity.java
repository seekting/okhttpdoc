package com.seekting.okhttpdoc;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import java.io.IOException;

import javax.annotation.Nullable;

import okhttp3.Authenticator;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.Route;
import okio.BufferedSink;

/**
 * Created by seekting on 17-8-3.
 */

public class LocalServerActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.authenticator(new Authenticator() {
            @Nullable
            @Override
            public Request authenticate(Route route, Response response) throws IOException {
                Log.d("seekting", "LocalServerActivity.authenticate()" + "authenticate" + response.body().string());
                Request r = response.request();
                Request.Builder b = r.newBuilder();
                b.post(new RequestBody() {
                    @Nullable
                    @Override
                    public MediaType contentType() {
                        return MediaType.parse("text");
                    }

                    @Override
                    public void writeTo(BufferedSink sink) throws IOException {

                        sink.write("ok".getBytes());
                    }

                    @Override
                    public long contentLength() throws IOException {
                        return "ok".getBytes().length;
                    }
                });
                return b.build();
            }
        });
        OkHttpClient okHttpClient = builder.build();
        Request.Builder requestBuilder = new Request.Builder();
        requestBuilder.url("http://192.168.1.108:8080/401");
        final byte[] bytes = "over".getBytes();
        requestBuilder.post(new RequestBody() {
            @Nullable
            @Override
            public MediaType contentType() {
                return MediaType.parse("text");
            }

            @Override
            public void writeTo(BufferedSink sink) throws IOException {
                sink.write(bytes);

            }

            @Override
            public long contentLength() throws IOException {
                return bytes.length;

            }
        });
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
