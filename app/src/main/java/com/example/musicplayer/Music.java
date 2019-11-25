package com.example.musicplayer;

import android.graphics.Bitmap;

public class Music {
    private int id;
    private  String title;
    private  String singer;
    private  String album;
    private  long size;
    private  int duration;
    private  String url;
    private Bitmap photo;
    private int albumId;
    public int getId()
    {
        return id;
    }
    public void setId(int id)
    {
        this.id=id;
    }
    public Bitmap getPhoto()
    {
        return photo;
    }
    public void setPhoto(Bitmap photo)
    {
        this.photo=photo;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getSinger() {
        return singer;
    }
    public void setSinger(String singer) {
        this.singer = singer;
    }
    public String getAlbum() {
        return album;
    }
    public void setAlbum(String album) {
        this.album = album;
    }
    public long getSize() {
        return size;
    }
    public void setSize(long size) {
        this.size = size;
    }
    public int getDuration() {
        return duration;
    }
    public void setDuration(int duration) {
        this.duration = duration;
    }
    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    public int getAlbumId()
    {
        return albumId;
    }
    public void setAlbumId(int albumId)
    {
        this.albumId=albumId;
    }

}
