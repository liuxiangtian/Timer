package com.lxt.xiang.timer.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lxt.xiang.timer.R;
import com.lxt.xiang.timer.model.Playlist;
import com.lxt.xiang.timer.util.LoadUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class PlaylistFragment extends Fragment {

    @Bind(R.id.viewpager)
    ViewPager viewpager;

    private int currentPosition;

    private PlaylistAdaptor playlistAdaptor;

    public PlaylistFragment() {
    }

    public static PlaylistFragment newInstance() {
        return new PlaylistFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_playlist_list, container, false);
        ButterKnife.bind(this, root);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        Toolbar toolbar = (Toolbar) root.findViewById(R.id.toolbar);
        activity.setSupportActionBar(toolbar);
        ActionBar ab = activity.getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        toolbar.setTitle(getResources().getString(R.string.playlist));
        return root;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        playlistAdaptor = new PlaylistAdaptor(getChildFragmentManager());
        viewpager.setAdapter(playlistAdaptor);
        LoadUtil.loadPlaylist(view.getContext(), playlistAdaptor);
    }

    @Override
    public void onResume() {
        super.onResume();
        viewpager.setCurrentItem(currentPosition);
    }

    @Override
    public void onPause() {
        super.onPause();
        currentPosition = viewpager.getCurrentItem();
    }

    public class PlaylistAdaptor extends FragmentPagerAdapter{

        private List<Playlist> playlists;

        public PlaylistAdaptor(FragmentManager fm) {
            super(fm);
            playlists = new ArrayList<>();
        }

        @Override
        public Fragment getItem(int position) {
            long id = playlists.get(position).getId();
            String name = playlists.get(position).getName();
            int num = playlists.get(position).getSongCount();
            return PlaylistItemFragment.newInstance(id, name, num, position+1);
        }

        @Override
        public int getCount() {
            return playlists.size();
        }

        public void setPlaylists(List<Playlist> playlists) {
            this.playlists = playlists;
            notifyDataSetChanged();
        }
    }

}
