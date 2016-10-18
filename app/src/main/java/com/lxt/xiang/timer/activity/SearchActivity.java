package com.lxt.xiang.timer.activity;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.lxt.xiang.timer.R;
import com.lxt.xiang.timer.adaptor.SearchAdaptor;
import com.lxt.xiang.timer.loader.AlbumLoader;
import com.lxt.xiang.timer.loader.ArtistLoader;
import com.lxt.xiang.timer.loader.TrackLoader;
import com.lxt.xiang.timer.model.Album;
import com.lxt.xiang.timer.model.Artist;
import com.lxt.xiang.timer.model.Track;
import com.lxt.xiang.timer.util.NaviUtil;

import java.util.List;

public class SearchActivity extends BaseActivity implements SearchView.OnQueryTextListener, SearchAdaptor.OnItemClickListener {

    RecyclerView recyclerView;
    SearchAdaptor searchAdaptor;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        toolbar.setTitle("");
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        searchAdaptor = new SearchAdaptor();
        searchAdaptor.setOnItemClickListener(this);
        recyclerView.setAdapter(searchAdaptor);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        MenuItem menuItem = menu.findItem(R.id.menu_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);
        searchView.setIconified(false);
        searchView.setIconifiedByDefault(false);
        menuItem.expandActionView();
        searchView.setOnQueryTextListener(this);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            finish();
        } else if(item.getItemId()==R.id.menu_settings){
            NaviUtil.naviToSettingsActivity(this);
        }
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        if (query==null || query.trim().equals("")) return false;
        List<Track> tracks = TrackLoader.loadTracksByName(this, query);
        List<Album> albums = AlbumLoader.loadAlbumsByName(this, query);
        List<Artist> artists = ArtistLoader.loadArtistsByName(this, query);
        searchAdaptor.replaceData(tracks, albums, artists);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (newText==null || newText.trim().equals("")) return false;
        return true;
    }

    @Override
    public void onItemClick(Track track) {
        Snackbar.make(recyclerView,track.toString(),Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onItemClick(Album album) {
        Snackbar.make(recyclerView,album.toString(),Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onItemClick(Artist artist) {
        Snackbar.make(recyclerView,artist.toString(),Snackbar.LENGTH_SHORT).show();
    }
}
