package com.lxt.xiang.timer.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.lxt.xiang.timer.R;
import com.lxt.xiang.timer.activity.BaseActivity;
import com.lxt.xiang.timer.adaptor.TrackAdaptor;
import com.lxt.xiang.timer.listener.OnPlayStateChangeListener;
import com.lxt.xiang.timer.loader.TrackLoader;
import com.lxt.xiang.timer.model.Track;
import com.lxt.xiang.timer.util.LoadUtil;
import com.lxt.xiang.timer.util.PlayUtil;
import com.lxt.xiang.timer.util.PrefsUtil;
import com.lxt.xiang.timer.view.DivideItemDecoration;

import butterknife.Bind;
import butterknife.ButterKnife;

public class TrackFragment extends Fragment implements TrackAdaptor.OnItemClickListener, OnPlayStateChangeListener {

    @Bind(R.id.recycler_view)
    RecyclerView recyclerView;
    private TrackAdaptor trackAdaptor;
    private String trackSort;

    public static TrackFragment newInstance() {
        return new TrackFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_standant_recycler, container, false);
        ButterKnife.bind(this,root);
        trackSort = PrefsUtil.getLibraryTrackSort();
        BaseActivity baseActivity = (BaseActivity) getActivity();
        if (baseActivity != null) {
            baseActivity.addOnPlayStateChangeListener(this);
        }
        return root;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        trackAdaptor = new TrackAdaptor(null);
        trackAdaptor.setOnItemClickListener(this);
        recyclerView.setAdapter(trackAdaptor);
        recyclerView.addItemDecoration(new DivideItemDecoration(1));
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        LoadUtil.loadTracks(getContext(), trackSort, trackAdaptor);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        PrefsUtil.setLibraryTrackSort(trackSort);
        BaseActivity baseActivity = (BaseActivity) getActivity();
        if (baseActivity != null) {
            baseActivity.removeOnPlayStateChangeListener(this);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_track, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.menu_sort_track_a2z:
                update(TrackLoader.SORT_TITLE);
                break;
            case R.id.menu_sort_track_z2a:
                update(TrackLoader.SORT_TITLE+" DESC");
                break;
            case R.id.menu_sort_track_album:
                update(TrackLoader.SORT_ALBUM);
                break;
            case R.id.menu_sort_track_artist:
                update(TrackLoader.SORT_ARTIST);
                break;
            case R.id.menu_sort_track_year:
                update(TrackLoader.SORT_YEAR);
                break;
            case R.id.menu_sort_track_duration:
                update(TrackLoader.SORT_DURATION);
                break;
            default:
                break;
        }
        return true;
    }

    private void update(final String sort) {
        trackSort = sort;
        LoadUtil.loadTracks(getContext(), trackSort, trackAdaptor);
    }

    @Override
    public void onItemClick(Track item, int position, long[] ids) {
        BaseActivity baseActivity = (BaseActivity) getActivity();
        PlayUtil.playTracks(baseActivity,ids, item.getId(), position);
        trackAdaptor.notifyItem(position,true);
    }

    @Override
    public void onMetaChange() {

    }

    @Override
    public void onMetaPlay() {

    }

    @Override
    public void onMetaPause() {

    }

    @Override
    public void onMetaStop() {

    }
}
