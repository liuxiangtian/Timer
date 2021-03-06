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
import com.lxt.xiang.timer.listener.PlayObserver;
import com.lxt.xiang.timer.loader.TrackLoader;
import com.lxt.xiang.timer.model.Track;
import com.lxt.xiang.timer.util.LoadUtil;
import com.lxt.xiang.timer.util.PlayUtil;
import com.lxt.xiang.timer.util.PrefsUtil;
import com.lxt.xiang.timer.view.DivideItemDecoration;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.lxt.xiang.timer.util.PlayUtil.probePlayState;

public class TrackFragment extends Fragment implements TrackAdaptor.OnItemClickListener {

    @Bind(R.id.recycler_view)
    RecyclerView recyclerView;
    private TrackAdaptor trackAdaptor;
    private String trackSort;

    private PlayObserver playStateObserver = new PlayObserver(){

        @Override
        public void onMetaChange(Track track) {
            PlayUtil.probePlayState(getActivity(), trackAdaptor);
        }

        @Override
        public void onMetaPlay() {
            PlayUtil.probePlayState(getActivity(), trackAdaptor);
        }

        @Override
        public void onMetaPause() {
            probePlayState(getActivity(), trackAdaptor);
        }
    };
    private RecyclerView.LayoutManager layoutManager;

    public static TrackFragment newInstance() {
        return new TrackFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_standant_recycler, container, false);
        ButterKnife.bind(this,root);
        trackSort = PrefsUtil.getLibraryTrackSort();
        PlayUtil.registerPlayObserver(getActivity(), playStateObserver);
        return root;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        layoutManager = new LinearLayoutManager(view.getContext());
        recyclerView.setLayoutManager(layoutManager);
        trackAdaptor = new TrackAdaptor(null);
        trackAdaptor.setOnItemClickListener(this);
        recyclerView.setAdapter(trackAdaptor);
        recyclerView.addItemDecoration(new DivideItemDecoration(1));
        LoadUtil.loadTracks(getContext(), trackSort, trackAdaptor);
        view.postDelayed(new Runnable() {
            @Override
            public void run() {
                int position = PlayUtil.probePlayState(getActivity(), trackAdaptor);
//                layoutManager.scrollToPosition(position);
            }
        }, 200);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        PrefsUtil.setLibraryTrackSort(trackSort);
        PlayUtil.unRegisterPlayObserver(getActivity(), playStateObserver);
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
        PlayUtil.playTracks(baseActivity, ids, item.getId(), position);
    }

}
