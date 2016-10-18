package com.lxt.xiang.timer.fragment;

import android.os.Bundle;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lxt.xiang.timer.ITimerInterface;
import com.lxt.xiang.timer.R;
import com.lxt.xiang.timer.activity.BaseActivity;
import com.lxt.xiang.timer.adaptor.TrackAdaptor;
import com.lxt.xiang.timer.listener.OnPlayStateChangeListener;
import com.lxt.xiang.timer.model.Track;
import com.lxt.xiang.timer.util.PlayUtil;
import com.lxt.xiang.timer.view.DivideItemDecoration;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class QueueFragment extends Fragment implements OnPlayStateChangeListener, TrackAdaptor.OnItemClickListener {

    @Bind(R.id.recycler_view)
    RecyclerView recyclerView;

    private TrackAdaptor trackAdaptor;
    private RecyclerView.LayoutManager layoutManager;

    public static QueueFragment newInstance() {
        return new QueueFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        trackAdaptor = new TrackAdaptor(null);
        trackAdaptor.setOnItemClickListener(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_queue, container, false);
        ButterKnife.bind(this, root);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        Toolbar toolbar = (Toolbar) root.findViewById(R.id.toolbar);
        activity.setSupportActionBar(toolbar);
        ActionBar ab = activity.getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        toolbar.setTitle(getResources().getString(R.string.playqueue));
        return root;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        layoutManager = new LinearLayoutManager(view.getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(trackAdaptor);
        recyclerView.addItemDecoration(new DivideItemDecoration(1));
    }

    @Override
    public void onResume() {
        super.onResume();
        BaseActivity baseActivity = (BaseActivity) getActivity();
        if (baseActivity == null) return;
        baseActivity.addOnPlayStateChangeListener(this);
        ITimerInterface iTimerService = baseActivity.iTimerService;
        if (iTimerService == null) return;
        try {
            List<Track> tracks = iTimerService.getQueues();
            if (tracks == null) return;
            trackAdaptor.replaceData(tracks);
            int position = iTimerService.getCurrentPosition();
            layoutManager.scrollToPosition(position);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        BaseActivity baseActivity = (BaseActivity) getActivity();
        if (baseActivity == null) return;
        baseActivity.removeOnPlayStateChangeListener(this);
    }

    @Override
    public void onMetaChange() {
        trackAdaptor.notifyDataSetChanged();
    }

    @Override
    public void onMetaPlay() {
        BaseActivity baseActivity = (BaseActivity) getActivity();
        PlayUtil.responsePlay(baseActivity, trackAdaptor);
    }

    @Override
    public void onMetaPause() {

    }

    @Override
    public void onMetaStop() {

    }

    @Override
    public void onItemClick(Track item, int position, long[] ids) {
        BaseActivity baseActivity = (BaseActivity) getActivity();
        PlayUtil.playTracks(baseActivity, ids, item.getId(), position);
        trackAdaptor.notifyItem(position,true);
    }
}