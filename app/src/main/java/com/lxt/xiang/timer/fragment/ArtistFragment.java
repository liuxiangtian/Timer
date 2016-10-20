package com.lxt.xiang.timer.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.lxt.xiang.timer.R;
import com.lxt.xiang.timer.activity.DetailsActivity;
import com.lxt.xiang.timer.adaptor.ArtistAdaptor;
import com.lxt.xiang.timer.loader.ArtistLoader;
import com.lxt.xiang.timer.model.Artist;
import com.lxt.xiang.timer.util.ConstantsUtil;
import com.lxt.xiang.timer.util.LoadUtil;
import com.lxt.xiang.timer.util.NavUtil;
import com.lxt.xiang.timer.util.PrefsUtil;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ArtistFragment extends Fragment implements ArtistAdaptor.OnItemClickListener {

    @Bind(R.id.recycler_view)
    RecyclerView recyclerView;
    private String artistSort;
    private ArtistAdaptor artistAdaptor;

    public static ArtistFragment newInstance() {
        return new ArtistFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_standant_recycler, container, false);
        ButterKnife.bind(this,root);
        artistSort = PrefsUtil.getLibraryArtistSort();
        return root;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView.setLayoutManager(new GridLayoutManager(view.getContext(), 2));
        artistAdaptor = new ArtistAdaptor(null);
        recyclerView.setAdapter(artistAdaptor);
        artistAdaptor.setOnItemClickListener(this);
        LoadUtil.loadArtists(getContext(), artistSort, artistAdaptor);

        view.postDelayed(new Runnable() {
            @Override
            public void run() {
            }
        }, 200);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        PrefsUtil.setLibraryArtistSort(artistSort);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_artist, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.menu_sort_artist_a2z:
                update(ArtistLoader.SORT_ARTIST);
                break;
            case R.id.menu_sort_artist_z2a:
                update(ArtistLoader.SORT_ARTIST_DESC);
                break;
            case R.id.menu_sort_artist_album:
                update(ArtistLoader.SORT_ALBUM_NUMS);
                break;
            case R.id.menu_sort_track_nums:
                update(ArtistLoader.SORT_TRACK_NUMS);
                break;
            default:
                break;
        }
        return true;
    }

    private void update(String sort) {
        artistSort = sort;
        LoadUtil.loadArtists(getView().getContext(), artistSort, artistAdaptor);
    }

    @Override
    public void onItemClick(Artist item, int position, Pair<View, String> pair) {
        Intent intent = new Intent(getActivity(), DetailsActivity.class);
        intent.putExtra(ConstantsUtil.DETAIL_TYPE, ConstantsUtil.DETAIL_TYPE_ARTIST);
        intent.putExtra(ConstantsUtil.DETAIL_ARTIST_ID, item.getId());
        intent.putExtra(ConstantsUtil.DETAIL_ARTIST_NAME, item.getArtist());
        NavUtil.navToDetailsActivity(getActivity(), pair, intent);
    }

}