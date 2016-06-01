package com.sahadev.example;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.sahadev.example.vipprogressview.R;
import com.sahadev.view.VipProgressView;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class SampleActivity extends AppCompatActivity implements Runnable {
    Handler handler;

    ExecutorService executorService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample);

        executorService = Executors.newSingleThreadExecutor();

        final VipProgressView vipProgressView = (VipProgressView) findViewById(R.id.vipProgressView);

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                vipProgressView.setData(msg.arg1, msg.arg2);
            }
        };


    }

    public void start(View v) {
        executorService.submit(this);
    }

    @Override
    public void run() {
        for (int i = 3; i < 6; i++) {
            for (int j = 0; j <= i; j++) {
                Message message = handler.obtainMessage();
                message.arg1 = i;
                message.arg2 = j;
                handler.sendMessage(message);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
