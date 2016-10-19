package com.lxt.xiang.timer.activity;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;

import com.lxt.xiang.timer.ITimerInterface;
import com.lxt.xiang.timer.service.TimerService;

public class BaseActivity extends AppCompatActivity implements ServiceConnection {

    public ITimerInterface iTimerService;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = new Intent(this, TimerService.class);
        bindService(intent,this, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(this);
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        iTimerService = ITimerInterface.Stub.asInterface(service);
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        iTimerService = null;
    }

}
