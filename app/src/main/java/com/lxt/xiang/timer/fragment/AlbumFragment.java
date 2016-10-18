package com.lxt.xiang.timer.fragment;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
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
import com.lxt.xiang.timer.adaptor.AlbumAdaptor;
import com.lxt.xiang.timer.loader.AlbumLoader;
import com.lxt.xiang.timer.model.Album;
import com.lxt.xiang.timer.util.ConstantsUtil;
import com.lxt.xiang.timer.util.LoadUtil;
import com.lxt.xiang.timer.util.PrefsUtil;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.lxt.xiang.timer.R.layout.fragment_standant_recycler;

public class AlbumFragment extends Fragment implements AlbumAdaptor.OnItemClickListener {

    @Bind(R.id.recycler_view)
    RecyclerView recyclerView;
    private String albumSort;
    private AlbumAdaptor albumAdaptor;

    public static AlbumFragment newInstance() {
        return new AlbumFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(fragment_standant_recycler, container, false);
        ButterKnife.bind(this,root);
        albumSort= PrefsUtil.getLibraryAlbumSort();
        return root;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView.setLayoutManager(new GridLayoutManager(view.getContext(), 2));
        albumAdaptor = new AlbumAdaptor(null);
        albumAdaptor.setOnItemClickListener(this);
        recyclerView.setAdapter(albumAdaptor);
    }

    @Override
    public void onResume() {
        super.onResume();
        LoadUtil.loadAlbums(getContext(), albumSort, albumAdaptor);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        PrefsUtil.setLibraryAlbumSort(albumSort);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_album, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.menu_sort_album_a2z:
                update(AlbumLoader.SORT_ALBUM);
                break;
            case R.id.menu_sort_album_z2a:
                update(AlbumLoader.SORT_ALBUM_DESC);
                break;
            case R.id.menu_sort_album_artist:
                update(AlbumLoader.SORT_ARTIST);
                break;
            case R.id.menu_sort_album_nums:
                update(AlbumLoader.SORT_NUMS);
                break;
            default:
                break;
        }
        return true;
    }

    private void update(String sort) {
        albumSort = sort;
        LoadUtil.loadAlbums(getView().getContext(), albumSort, albumAdaptor);
    }

    @Override
    public void onItemClick(Album item, int position, Pair<View, String> pair) {
        Intent intent = new Intent(getActivity(), DetailsActivity.class);
        intent.putExtra(ConstantsUtil.DETAIL_TYPE, ConstantsUtil.DETAIL_TYPE_ALBUM);
        intent.putExtra(ConstantsUtil.DETAIL_ALBUM_ID, item.getId());
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){
            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(), pair);
            startActivity(intent, options.toBundle());
        } else {
            startActivity(intent);
        }
    }

}