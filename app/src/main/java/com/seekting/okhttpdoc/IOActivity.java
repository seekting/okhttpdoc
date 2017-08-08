package com.seekting.okhttpdoc;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

import okio.Buffer;

/**
 * Created by seekting on 17-8-8.
 */

public class IOActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Buffer buffer = new Buffer();
//        byte[] bytes = new byte[10];
        String str = "hello okhttp io";
        byte[] bytes = new byte[0];
        try {
            bytes = str.getBytes("utf-8");
            buffer.write(bytes);
            String read = buffer.readString(Charset.forName("utf-8"));
            Log.d("seekting","IOActivity.onCreate()"+read);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();

        }

    }
}
