package com.seekting.okhttpdoc;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;

/**
 * Created by Administrator on 2017/8/26.
 */

public class BroadCastActivity extends Activity {
    private static final String ACTION = "OrderBroadCastActivity";
    private static final String PERMISSION = "seekting.demo2017.APP_PERMISSION";
    private Button mBtn;
    private BroadcastReceiver mReceiver;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBtn = new Button(this);
        mBtn.setText("发广播");
        setContentView(mBtn);
        mBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ACTION);
                sendOrderedBroadcast(intent, PERMISSION);
//                sendBroadcast(intent);
            }
        });

        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Bundle a = getResultExtras(true);
                if(a!=null) {
                    Log.d("seekting", "OkhttpActivity.onReceive()" + a.get("seekting"));
                }else{
                    Log.d("seekting","OkhttpActivity.onReceive()");
                }



            }
        };
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION);
        intentFilter.setPriority(10);
//        registerReceiver(mReceiver, intentFilter, PERMISSION, new Handler() {
//            @Override
//            public void handleMessage(Message msg) {
//                super.handleMessage(msg);
//                Log.d("seekting", "OkhttpActivity.handleMessage()");
//            }
//        });
        registerReceiver(mReceiver, intentFilter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }
}
