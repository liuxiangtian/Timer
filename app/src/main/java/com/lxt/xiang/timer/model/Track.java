package com.lxt.xiang.timer.model;


import android.os.Parcel;
import android.os.Parcelable;

public class Track implements Parcelable{

    private long id;
    private long albumId;
    private long artistId;
    private long dataAdd;
    private String title;
    private String artist;
    private String album;
    private String track;
    private long duration;
    private int year;

    public Track() {
        this.id = -1;
        this.albumId = -1;
        this.artistId = -1;
        this.dataAdd = -1;
        this.title = "";
        this.artist = "";
        this.album = "";
        this.track = "";
        this.duration = -1;
        this.year = -1;
    }

    public Track(long id, long albumId, long artistId, long dataAdd, String title, String artist, String album, String track, long duration, int year) {
        this.id = id;
        this.albumId = albumId;
        this.artistId = artistId;
        this.dataAdd = dataAdd;
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.track = track;
        this.duration = duration;
        this.year = year;
    }

    protected Track(Parcel in) {
        id = in.readLong();
        albumId = in.readLong();
        artistId = in.readLong();
        dataAdd = in.readLong();
        title = in.readString();
        artist = in.readString();
        album = in.readString();
        track = in.readString();
        duration = in.readLong();
        year = in.readInt();
    }

    public static final Creator<Track> CREATOR = new Creator<Track>() {
        @Override
        public Track createFromParcel(Parcel in) {
            return new Track(in);
        }

        @Override
        public Track[] newArray(int size) {
            return new Track[size];
        }
    };

    @Override
    public String toString() {
        return "Track:"+id+" "+title+" "+album+" "+artist+" "+albumId+" "+artistId+" "+duration;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getAlbumId() {
        return albumId;
    }

    public void setAlbumId(long albumId) {
        this.albumId = albumId;
    }

    public long getArtistId() {
        return artistId;
    }

    public void setArtistId(long artistId) {
        this.artistId = artistId;
    }

    public long getDataAdd() {
        return dataAdd;
    }

    public void setDataAdd(long dataAdd) {
        this.dataAdd = dataAdd;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getTrack() {
        return track;
    }

    public void setTrack(String track) {
        this.track = track;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeLong(albumId);
        dest.writeLong(artistId);
        dest.writeLong(dataAdd);
        dest.writeString(title);
        dest.writeString(artist);
        dest.writeString(album);
        dest.writeString(track);
        dest.writeLong(duration);
        dest.writeInt(year);
    }
}
