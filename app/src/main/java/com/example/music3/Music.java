package com.example.music3;

import android.graphics.Bitmap;
import android.os.Parcelable;

import org.parceler.Parcel;

import java.io.File;
import java.io.Serializable;

@Parcel
public class Music  {
     String songName;
     String path;
     Bitmap bitmap;
     String artist;

    public Music()
    {
    }

    public Music(String songName, String path,Bitmap bitmap,String artist) {
        this.songName = songName;
        this.path = path;
        this.bitmap = bitmap;
        this.artist = artist;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public String getPath()
    {
        return path;
    }

    public String getSongName()
    {
        return songName;
    }

    public String getArtist(){return artist;}

    public void setSongName(String songName) {
        this.songName = songName;
    }
}
