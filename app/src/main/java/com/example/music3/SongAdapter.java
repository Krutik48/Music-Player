package com.example.music3;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.ThumbnailUtils;
import android.os.Build;
import android.provider.MediaStore;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.TextAppearanceSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;

public class SongAdapter extends ArrayAdapter<Music> implements Filterable  {

    ArrayList<Music> musicList;
    String mSearchText;
    int flag;
    int previous;

    public SongAdapter(@NonNull Context context, ArrayList<Music> music,String mSearchText, int flag) {
        super(context, 0,music);
        this.musicList = music;
        this.mSearchText = mSearchText;
        this.flag = flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }


    @SuppressLint("ResourceAsColor")
    @Override
    public View getView(int position,View convertView,ViewGroup parent) {

        View listItemView = convertView;

        if (listItemView==null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.custom_list_view, parent, false);
        }

        Music currMusic=getItem(position);



        TextView textView = (TextView)listItemView.findViewById(R.id.textView);
        textView.setText(musicList.get(position).songName);

        TextView textView1 =listItemView.findViewById(R.id.artist);
        textView1.setText(musicList.get(position).artist);

        ImageView imageView =(ImageView)listItemView.findViewById(R.id.imageView);
        LinearLayout linearLayout =listItemView.findViewById(R.id.linearLayout);
//        Glide.with(getContext()).load(currMusic.getFile()).override(30,30).into(imageView);
//        Bitmap bitmap = ThumbnailUtils.createAudioThumbnail(currMusic.getPath(), MediaStore.Images.Thumbnails.MINI_KIND);

        Glide.with(listItemView).load(musicList.get(position).getBitmap()).error(R.drawable.music22).into(imageView);

//        imageView.setImageBitmap(musicList.get(position).getBitmap());
//        Glide.with(listItemView).load(ThumbnailUtils.createAudioThumbnail(currMusic.getFile().getPath(), MediaStore.Images.Thumbnails.MINI_KIND)).override(100,100).error(R.drawable.music22).into(imageView);


        Log.v("flag", String.valueOf(flag));
        String fullText = musicList.get(position).songName;


        if (mSearchText != null && !mSearchText.isEmpty()) {
            int startPos = fullText.toLowerCase().indexOf(mSearchText.toLowerCase());
            int endPos = startPos + mSearchText.length();

            if (startPos != -1) {
                Spannable spannable = new SpannableString(fullText);
                ColorStateList redColor = new ColorStateList(new int[][]{new int[]{}}, new int[]{Color.RED});
                TextAppearanceSpan highlightSpan = new TextAppearanceSpan(null, Typeface.NORMAL, -1, redColor, null);
                spannable.setSpan(highlightSpan, startPos, endPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                textView.setText(spannable);
            } else {
                textView.setText(fullText);
            }
        } else {
            textView.setText(fullText);
        }
        return listItemView;
    }
}
