package com.lxt.xiang.timer.util;


import android.content.Context;

import com.lxt.xiang.timer.adaptor.AlbumAdaptor;
import com.lxt.xiang.timer.adaptor.ArtistAdaptor;
import com.lxt.xiang.timer.adaptor.TrackAdaptor;
import com.lxt.xiang.timer.fragment.PlaylistFragment;
import com.lxt.xiang.timer.loader.AlbumLoader;
import com.lxt.xiang.timer.loader.ArtistLoader;
import com.lxt.xiang.timer.loader.PlaylistLoader;
import com.lxt.xiang.timer.loader.TrackLoader;
import com.lxt.xiang.timer.model.Album;
import com.lxt.xiang.timer.model.Artist;
import com.lxt.xiang.timer.model.Playlist;
import com.lxt.xiang.timer.model.Track;

import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class LoadUtil {

    public static void loadTracks(final Context context, final String sort, final TrackAdaptor trackAdaptor){
        Observable.just(TrackLoader.loadTracks(context, sort))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<Track>>() {
                    @Override
                    public void call(List<Track> tracks) {
                        if (trackAdaptor != null && tracks != null && tracks.size() != 0) {
                            trackAdaptor.replaceData(tracks);
                        }
                    }
                });
    }

    public static void loadAlbums(final Context context, final String sort, final AlbumAdaptor albumAdaptor) {
        Observable.just(AlbumLoader.loadAlbums(context, sort))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<Album>>() {
                    @Override
                    public void call(List<Album> alba) {
                        if(albumAdaptor!=null && alba!=null && alba.size()!=0) {
                            albumAdaptor.replaceData(alba);
                        }
                    }
                });
    }

    public static void loadArtists(final Context context, final String artistSort,final  ArtistAdaptor artistAdaptor) {
        Observable.just(ArtistLoader.loadArtists(context, artistSort))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<Artist>>() {
                    @Override
                    public void call(List<Artist> artists) {
                        if (artistAdaptor != null && artists!=null && artists.size()!=0) {
                            artistAdaptor.replaceData(artists);
                        }
                    }
                });

    }

    public static void loadPlaylist(Context context, final PlaylistFragment.PlaylistAdaptor adaptor) {
        Observable.just(PlaylistLoader.loadPlayLists(context))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<Playlist>>() {
                    @Override
                    public void call(List<Playlist> playlists) {
                        if(adaptor!=null && playlists!=null && playlists.size()!=0){
                            adaptor.setPlaylists(playlists);
                        }
                    }
                });
    }

    public static void loadTracksByAlbum(Context context, long albumId, final TrackAdaptor trackAdaptor) {
        Observable.just(TrackLoader.loadTracksByAlbumId(context, albumId))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<Track>>() {
                    @Override
                    public void call(List<Track> tracks) {
                        if (trackAdaptor != null && tracks!=null && tracks.size()!=0) {
                            trackAdaptor.replaceData(tracks);
                        }
                    }
                });
    }

    public static void loadTracksByArtist(Context context, long artistId, final TrackAdaptor trackAdaptor) {
        Observable.just(TrackLoader.loadTracksByArtistId(context, artistId))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<Track>>() {
                    @Override
                    public void call(List<Track> tracks) {
                        if (trackAdaptor != null && tracks!=null && tracks.size()!=0) {
                            trackAdaptor.replaceData(tracks);
                        }
                    }
                });
    }

    public static void loadTracksByPlaylist(Context context, long playlistId, final TrackAdaptor trackAdaptor) {
        Observable.just(TrackLoader.loadTracksByPlaylistId(context, playlistId))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<Track>>() {
                    @Override
                    public void call(List<Track> tracks) {
                        if (trackAdaptor != null && tracks!=null && tracks.size()!=0) {
                            trackAdaptor.replaceData(tracks);
                        }
                    }
                });
    }
}
