package com.lxt.xiang.timer.activity;


import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;

import com.lxt.xiang.timer.ITimerInterface;
import com.lxt.xiang.timer.listener.OnPlayStateChangeListener;
import com.lxt.xiang.timer.service.TimerService;
import com.lxt.xiang.timer.util.ConstantsUtil;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class BaseActivity extends AppCompatActivity implements ServiceConnection {

    public ITimerInterface iTimerService;
    private List<OnPlayStateChangeListener> listeners = new ArrayList<>();
    private PlayStatusReceive playStatusReceive;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        playStatusReceive = new PlayStatusReceive(this);
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConstantsUtil.ACTION_STATUS_METACHANGE);
        filter.addAction(ConstantsUtil.ACTION_STATUS_PAUSE);
        filter.addAction(ConstantsUtil.ACTION_STATUS_PLAY);
        filter.addAction(ConstantsUtil.ACTION_STATUS_SEEK);
        filter.addAction(ConstantsUtil.ACTION_STATUS_STOP);
        registerReceiver(playStatusReceive, filter);

        Intent intent = new Intent(this, TimerService.class);
        bindService(intent,this, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(playStatusReceive);
        unbindService(this);
        listeners = null;
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        iTimerService = ITimerInterface.Stub.asInterface(service);
        onMetaChange();
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        iTimerService = null;
    }

    public void addOnPlayStateChangeListener(OnPlayStateChangeListener listener) {
        if (listener == null) {
            return;
        }
        listeners.add(listener);
    }

    public void removeOnPlayStateChangeListener(OnPlayStateChangeListener listener) {
        if (listener == null) {
            return;
        }
        listeners.remove(listener);
    }

    public class PlayStatusReceive extends BroadcastReceiver{

        private WeakReference<BaseActivity> reference;

        public PlayStatusReceive(final BaseActivity baseActivity) {
            this.reference = new WeakReference<BaseActivity>(baseActivity);
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action==null || reference.get()==null) return;
            BaseActivity baseActivity = reference.get();
            if(action.equals(ConstantsUtil.ACTION_STATUS_PLAY)){
                baseActivity.onMetaPlay();
            } else if(action.equals(ConstantsUtil.ACTION_STATUS_METACHANGE)) {
                baseActivity.onMetaChange();
            } else if(action.equals(ConstantsUtil.ACTION_STATUS_PAUSE)) {
                baseActivity.onMetaPause();
            } else if(action.equals(ConstantsUtil.ACTION_STATUS_STOP)) {
                baseActivity.onMetaStop();
            }
        }
    }

    protected void onMetaStop() {
        for (OnPlayStateChangeListener listener : listeners) {
            listener.onMetaStop();
        }
    }

    protected void onMetaPause() {
        for (OnPlayStateChangeListener listener : listeners) {
            listener.onMetaPause();
        }
    }

    protected void onMetaPlay() {
        for (OnPlayStateChangeListener listener : listeners) {
            listener.onMetaPlay();
        }
    }

    protected void onMetaChange() {
        for (OnPlayStateChangeListener listener : listeners) {
            listener.onMetaChange();
        }
    }

}
