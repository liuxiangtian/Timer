package com.lxt.xiang.timer.model;


public class Artist {

    private long id;
    private String artist;
    private int albumNum;
    private int trackNum;

    public Artist() {
        this.id = -1;
        this.artist = "";
        this.albumNum = -1;
        this.trackNum = -1;
    }

    public Artist(long id, String artist, int albumNum, int trackNum) {
        this.id = id;
        this.artist = artist;
        this.albumNum = albumNum;
        this.trackNum = trackNum;
    }

    @Override
    public String toString() {
        return id+" "+artist+" "+albumNum+" "+trackNum;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public int getAlbumNum() {
        return albumNum;
    }

    public void setAlbumNum(int albumNum) {
        this.albumNum = albumNum;
    }

    public int getTrackNum() {
        return trackNum;
    }

    public void setTrackNum(int trackNum) {
        this.trackNum = trackNum;
    }
}
