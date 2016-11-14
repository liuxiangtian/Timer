package com.lxt.xiang.timer.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lxt.xiang.timer.R;
import com.lxt.xiang.timer.util.PrefsUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainFragment extends Fragment {

    @Bind(R.id.tabs)
    TabLayout tabs;
    @Bind(R.id.viewpager)
    ViewPager viewpager;

    private int currentPosition;
    private MainAdaptor mainAdaptor;

    public static MainFragment newInstance() {
        return new MainFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_main_library, container, false);
        ButterKnife.bind(this, root);
        currentPosition = PrefsUtil.getMainFragmentPosition();
        return root;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        activity.setSupportActionBar(toolbar);
        ActionBar ab = activity.getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_home);
        ab.setDisplayHomeAsUpEnabled(true);
        toolbar.setTitle("Timer");

        mainAdaptor = new MainAdaptor(getChildFragmentManager());
        mainAdaptor.addFragmentWithKey(getResources().getString(R.string.track), TrackFragment.newInstance());
        mainAdaptor.addFragmentWithKey(getResources().getString(R.string.album), AlbumFragment.newInstance());
        mainAdaptor.addFragmentWithKey(getResources().getString(R.string.artist), ArtistFragment.newInstance());
        viewpager.setAdapter(mainAdaptor);
        tabs.setupWithViewPager(viewpager);
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        PrefsUtil.setMainFragmentPosition(viewpager.getCurrentItem());
    }

    public class MainAdaptor extends FragmentPagerAdapter {

        private List<Pair<String, Fragment>> mPairs = new ArrayList<>();

        public MainAdaptor(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mPairs.get(position).second;
        }

        @Override
        public int getCount() {
            return mPairs.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mPairs.get(position).first;
        }

        public void addFragmentWithKey(final String key, final Fragment f) {
            mPairs.add(new Pair<>(key, f));
        }
    }

}