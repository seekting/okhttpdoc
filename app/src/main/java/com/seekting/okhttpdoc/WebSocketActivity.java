package com.seekting.okhttpdoc;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import java.io.IOException;
import java.nio.charset.Charset;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

/**
 * Created by seekting on 17-8-7.
 */

public class WebSocketActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        ws://35.161.31.28:8888/ws. Post URL: 63cc18e390ee5ab8aff25ac243b41b59
//        name = "Authorization"
//        value = "63cc18e390ee5ab8aff25ac243b41b59"
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        OkHttpClient client = builder.build();
        Request.Builder requestBuilder = new Request.Builder();
        requestBuilder.url("ws://35.161.31.28:8888/ws");
        requestBuilder.addHeader("Sec-WebSocket-Version", "13");
        requestBuilder.addHeader("Authorization", "49691cc0dad87f657ebec889ff45cbba");
        final WebSocket webSocket = client.newWebSocket(requestBuilder.build(), new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                super.onOpen(webSocket, response);
                try {
                    Log.d("seekting", "WebSocketActivity.onOpen()" + response.body().string());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                webSocket.send("{\"cmd\":\"send\",\"msg\":\"{\\\"cmd\\\":\\\"h\\\"}\"}");
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                super.onMessage(webSocket, text);
                Log.d("seekting", "WebSocketActivity.onMessage()" + text);
            }

            @Override
            public void onMessage(WebSocket webSocket, ByteString bytes) {
                super.onMessage(webSocket, bytes);
                Log.d("seekting", "WebSocketActivity.onMessage()" + bytes.string(Charset.defaultCharset()));
            }

            @Override
            public void onClosing(WebSocket webSocket, int code, String reason) {
                super.onClosing(webSocket, code, reason);
                Log.d("seekting", "WebSocketActivity.onClosing()" + reason + "code=" + code);
            }

            @Override
            public void onClosed(WebSocket webSocket, int code, String reason) {
                super.onClosed(webSocket, code, reason);
                Log.d("seekting", "WebSocketActivity.onClosed()" + reason);
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                super.onFailure(webSocket, t, response);
                    Log.d("seekting", "WebSocketActivity.onFailure()", t);
            }
        });
    }
}
