package com.lxt.xiang.timer.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;

import com.lxt.xiang.timer.R;
import com.lxt.xiang.timer.adaptor.SearchAdaptor;
import com.lxt.xiang.timer.loader.AlbumLoader;
import com.lxt.xiang.timer.loader.ArtistLoader;
import com.lxt.xiang.timer.loader.TrackLoader;
import com.lxt.xiang.timer.model.Album;
import com.lxt.xiang.timer.model.Artist;
import com.lxt.xiang.timer.model.Track;
import com.lxt.xiang.timer.util.ConstantsUtil;
import com.lxt.xiang.timer.util.NavUtil;
import com.lxt.xiang.timer.util.PlayUtil;

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
            NavUtil.navToSettingsActivity(this);
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
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(recyclerView.getWindowToken(), 0);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (newText==null || newText.trim().equals("")) return false;
        return true;
    }

    @Override
    public void onItemClick(Track track) {
        long[] ids = new long[1];
        ids[0] = track.getId();
        PlayUtil.playTracks(this, ids, ids[0], 0);
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(MainActivity.NAVIGATION, MainActivity.NAVI_NOW_PLAYING);
        startActivity(intent);
    }

    @Override
    public void onItemClick(Album album) {
        Intent intent = new Intent(this, DetailsActivity.class);
        intent.putExtra(ConstantsUtil.DETAIL_TYPE, ConstantsUtil.DETAIL_TYPE_ALBUM);
        intent.putExtra(ConstantsUtil.DETAIL_ALBUM_ID, album.getId());
        intent.putExtra(ConstantsUtil.DETAIL_ALBUM_NAME, album.getAlbum());
        NavUtil.navToDetailsActivity(this, null, intent);
    }

    @Override
    public void onItemClick(Artist artist) {
        Intent intent = new Intent(this, DetailsActivity.class);
        intent.putExtra(ConstantsUtil.DETAIL_TYPE, ConstantsUtil.DETAIL_TYPE_ARTIST);
        intent.putExtra(ConstantsUtil.DETAIL_ARTIST_ID, artist.getId());
        intent.putExtra(ConstantsUtil.DETAIL_ARTIST_NAME, artist.getArtist());
        NavUtil.navToDetailsActivity(this, null, intent);
    }

}
