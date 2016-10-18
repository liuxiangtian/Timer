package com.lxt.xiang.timer.model;


public class Playlist {

    private long id;
    private String name;
    private int songCount;

    public Playlist()
    {
        this.id = -1;
        this.name = "";
        this.songCount = -1;
    }

    public Playlist(long _id, String _name, int _songCount)
    {
        this.id = _id;
        this.name = _name;
        this.songCount = _songCount;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getSongCount() {
        return songCount;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSongCount(int songCount) {
        this.songCount = songCount;
    }
}
