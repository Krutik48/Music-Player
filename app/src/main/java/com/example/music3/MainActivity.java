package com.example.music3;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.cardview.widget.CardView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.ExifInterface;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.media.audiofx.BassBoost;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import carbon.widget.Banner;
import carbon.widget.BottomBar;

import static com.example.music3.PlaySong.mVuMeterView;
import static com.example.music3.PlaySong.play;

public class MainActivity extends AppCompatActivity{


    ListView listView;

    static ArrayList<Music> songs;
    ArrayList<String> musicsPath;
    ArrayList<String> musicName;

    EditText searchTxt;
    SongAdapter arrayAdapter;
    ImageView searchIcn;
    static ImageView musicState,musicImage;
    TextView cancel,displayAppType;
    static TextView musicTitle,musicArtist;
    static int pos;

    CardView cardView;
    ImageView textCard;
    int previous=-1,prevDurn;
    static int flag =0;
    static MediaPlayer mediaPlayer=null;

    Bitmap bitmap;
    MediaMetadataRetriever mmr = new MediaMetadataRetriever();
    byte[] rawArt;
    BitmapFactory.Options bfo=new BitmapFactory.Options();



    // Request focus for music stream and pass AudioManager.OnAudioFocusChangeListener
    // implementation reference



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        getSupportActionBar().hide();

        listView =findViewById(R.id.songListView);
        searchIcn = findViewById(R.id.searchIcn);
        searchTxt =findViewById(R.id.searchText);
        cancel =findViewById(R.id.cancel);
        musicsPath = new ArrayList<>();
        musicName = new ArrayList<>();
        displayAppType =findViewById(R.id.appTypeDisplay);
        cardView =findViewById(R.id.cardView2);
        musicState = findViewById(R.id.musicState);
        musicTitle = findViewById(R.id.musicName);
        musicArtist = findViewById(R.id.artistName);
        musicImage = findViewById(R.id.songImage);
        textCard = findViewById(R.id.textCard);

        Dexter.withContext(this).withPermission(Manifest.permission.READ_EXTERNAL_STORAGE).withListener(new PermissionListener() {

            @Override
            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {

                songs = getAllMusic(getApplicationContext());
                setAdapter();
                musicTitle.setSelected(true);

                if (mediaPlayer==null)
                {
                    cardView.setVisibility(View.INVISIBLE);
                }




//                ---------------------------------------------------------------------------
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @SuppressLint("ResourceAsColor")
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        cardView.setVisibility(View.VISIBLE);
                        pos = position;

                        if(mediaPlayer!=null && previous!=position)
                        {
                            mediaPlayer.pause();
                            mediaPlayer.release();
                        }
                        if(previous!=position)
                        {
                            Uri uri =Uri.parse(songs.get(pos).getPath());
                            mediaPlayer =MediaPlayer.create(MainActivity.this, uri);
                            mediaPlayer.start();
                        }
                        previous = position;

                        musicState.setImageResource(R.drawable.ic_baseline_pause_24);
                        musicTitle.setText(songs.get(position).getSongName());
                        musicArtist.setText(songs.get(position).getArtist());

                        Glide.with(MainActivity.this)
                                .load(songs.get(position).getBitmap())
                                .error(R.drawable.music22)
                                .into(musicImage);

                        view.setSelected(true);

                      
//                        listView.setAdapter(newSongAdapter);
//                        BottomSheetDialog sheetDialog;
//                        sheetDialog = new BottomSheetDialog(MainActivity.this,R.style.BottomSheetStyle);
//                        View view2 = LayoutInflater.from(MainActivity.this).inflate(R.layout.activity_play_song,findViewById(R.id.bottm_sheet));
//                        sheetDialog.setContentView(view2);
//                        sheetDialog.show();


//                        InputMethodManager imm =(InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//                        imm.hideSoftInputFromWindow(searchTxt.getWindowToken(),0);
//
//                        Intent intent =new Intent(getApplicationContext(),PlaySong.class);
//                        String currSong = listView.getItemAtPosition(position).toString();
//
//                        intent.putExtra("currSong",currSong);
//                        intent.putExtra("position",position);
//                        intent.putExtra("musics",musicsPath);
//                        intent.putExtra("name",musicName);
//
//                        startActivity(intent);
                    }
                });
//                ------------------------------------------------------------------------------------


                cardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        PlaySong songViews = new PlaySong();
                        songViews.show(getSupportFragmentManager(),songViews.getTag());
                    }
                });

                musicState.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(mediaPlayer.isPlaying()){

                            musicState.setImageResource(R.drawable.play);
                            if (play!=null) {
                                play.setImageResource(R.drawable.ic_baseline_play_circle_filled_24);
                                mVuMeterView.stop(true);
                            }
                            mediaPlayer.pause();

                        }
                        else{

                            musicState.setImageResource(R.drawable.ic_baseline_pause_24);
                            if (play!=null) {
                                play.setImageResource(R.drawable.ic_baseline_pause_circle_filled_24);
                                mVuMeterView.resume(true);
                            }
                            mediaPlayer.start();
                        }
                    }
                });
                searchTxt.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        filter(s.toString());
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });


                float scale = textCard.getScaleX();
                searchIcn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        searchTxt.setVisibility(View.VISIBLE);
                        textCard.setVisibility(View.VISIBLE);
                        textCard.setScaleX(2*scale);
                        displayAppType.animate().translationXBy(-1000).setDuration(300);
                        textCard.setTranslationX(-500);
                        textCard.animate().scaleX(0).setDuration(3000);
                        searchTxt.requestFocus();
                        searchTxt.setFocusableInTouchMode(true);
                        InputMethodManager imm =(InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.showSoftInput(searchTxt,InputMethodManager.SHOW_FORCED);
                        searchIcn.setVisibility(View.INVISIBLE);
                        cancel.setVisibility(View.VISIBLE);
//                        displayAppType.setVisibility(View.INVISIBLE);
                    }
                });

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        textCard.setVisibility(View.INVISIBLE);
                        displayAppType.animate().translationXBy(1000).setDuration(300);
                        textCard.setTranslationX(500);
                        textCard.setScaleX(scale);
                        searchTxt.setVisibility(View.INVISIBLE);
                        searchTxt.setText("");
                        searchIcn.setVisibility(View.VISIBLE);
                        InputMethodManager imm =(InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(searchTxt.getWindowToken(),0);
                        cancel.setVisibility(View.INVISIBLE);
//                        displayAppType.setVisibility(View.VISIBLE);
                    }
                });
            }


            @Override
            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                permissionToken.continuePermissionRequest();
            }
        }).check();
    }


        private void filter(String text) {
            ArrayList<Music> filteredList = new ArrayList<>();

            for (Music item : songs)
            {
                if(item.getSongName().toLowerCase().contains(text.toLowerCase())){

                    filteredList.add(item);;
                }
            }
//            ((SongAdapter)listView.getAdapter()).update(filteredList);

            SongAdapter filteredAdapter = new SongAdapter(MainActivity.this,filteredList,text,-1);
            listView.setAdapter(filteredAdapter);

        }

        public void setAdapter(){
            arrayAdapter = new SongAdapter(MainActivity.this, songs,null,-1);
            listView.setAdapter(arrayAdapter);
        }




        public ArrayList<Music> getAllMusic(Context context) {

            ArrayList<Music> tempVideoFile = new ArrayList<>();

            Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

            String[] projection = {
                    MediaStore.Audio.Media.DATA,
                    MediaStore.Video.Media.TITLE,
                    MediaStore.Audio.Media.ARTIST
            };


            Cursor cursor = context.getContentResolver().query(uri, projection, null, null, MediaStore.Video.Media.TITLE);

            if (cursor != null) {
                while (cursor.moveToNext()) {
                    String path = cursor.getString(0);
                    String title = cursor.getString(1);
                    String artist = cursor.getString(2);

                    File file = new File(path);

                    bitmap = null;

//                    rawArt = mmr.getEmbeddedPicture();

// if rawArt is null then no cover art is embedded in the file or is not
// recognized as such.
//                    if (null != rawArt)
//                        bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length, bfo);
//                    else
//                    {
//                        bitmap = null;
//                    }





                    tempVideoFile.add(new Music(title,path,bitmap,artist));
                    musicsPath.add(file.getPath());
                    musicName.add(title);

                }
                cursor.close();
            }
            return tempVideoFile;
        }

    }


