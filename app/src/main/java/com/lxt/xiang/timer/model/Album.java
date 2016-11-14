package com.lxt.xiang.timer.model;


public class Album {

    private long id;
    private String album;
    private String artist;
    private long artistId;
    private int trackNum;
    private String albumArt;

    public Album() {
        this.id = -1;
        this.album = "";
        this.artist = "";
        this.artistId = -1;
        this.trackNum = -1;
        this.albumArt = "";
    }

    public Album(long id, String album, String artist, long artistId, int trackNum, String albumArt) {
        this.id = id;
        this.album = album;
        this.artist = artist;
        this.artistId = artistId;
        this.trackNum = trackNum;
        this.albumArt = albumArt;
    }

    @Override
    public String toString() {
        return id+" "+album+" "+artist+" "+trackNum;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public long getArtistId() {
        return artistId;
    }

    public void setArtistId(long artistId) {
        this.artistId = artistId;
    }

    public int getTrackNum() {
        return trackNum;
    }

    public void setTrackNum(int trackNum) {
        this.trackNum = trackNum;
    }

    public String getAlbumArt() {
        return albumArt;
    }

    public void setAlbumArt(String albumArt) {
        this.albumArt = albumArt;
    }
}
