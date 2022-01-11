package com.example.music3;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.cardview.widget.CardView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.audiofx.BassBoost;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bullhead.equalizer.DialogEqualizerFragment;
import com.bullhead.equalizer.EqualizerFragment;
import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.Stack;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import io.gresse.hugo.vumeterlibrary.VuMeterView;

import static com.example.music3.MainActivity.mediaPlayer;
import static com.example.music3.MainActivity.musicArtist;
import static com.example.music3.MainActivity.musicImage;
import static com.example.music3.MainActivity.musicState;
import static com.example.music3.MainActivity.musicTitle;
import static com.example.music3.MainActivity.pos;
import static com.example.music3.MainActivity.songs;

public class PlaySong extends BottomSheetDialogFragment {



    TextView textView;
    TextView duration,pogress;
    ImageView imageView,previous,next, shuffle,repeat, equalizer,playlist;
    static ImageView play;

    static int sessionId;
    ArrayList<Integer> forShuffle;
    int iterator=0;
    BassBoost bassBoost;

    int repInt =1;
    boolean shuffleBool=false;
    int randomNum;
    Context context;

//    MediaPlayer mediaPlayer;
    Thread updateSeek;
    SeekBar seekBar;

    CardView imageCard;

     static VuMeterView mVuMeterView;

    Bitmap art;
    MediaMetadataRetriever mmr = new MediaMetadataRetriever();
    byte[] rawArt;
    BitmapFactory.Options bfo=new BitmapFactory.Options();


    public  String getCurrTime(int currTime)
    {

        @SuppressLint("DefaultLocale") String s=String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(currTime),
                TimeUnit.MILLISECONDS.toSeconds(currTime) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(currTime))
        );
        return s;
    }

//    @Override
//    public void onStop() {
//        super.onStop();
//
//        mediaPlayer.pause();
//        mediaPlayer.release();
//    }


    @Override
    public View onCreateView(LayoutInflater inflater , ViewGroup container,
                         Bundle savedInstanceState ) {

//        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        View view = inflater.inflate(R.layout.activity_play_song, container, false);




//        getSupportActionBar().hide();

        textView=view.findViewById(R.id.songName);
        imageView=view.findViewById(R.id.thumbnail);
        next=(ImageView)view.findViewById(R.id.next);
        previous=(ImageView)view.findViewById(R.id.previous);
        play=(ImageView)view.findViewById(R.id.play);
        seekBar=(SeekBar)view.findViewById(R.id.seekBar);
        duration =(TextView)view.findViewById(R.id.duration);
        pogress =(TextView)view.findViewById(R.id.pogress);
        shuffle = view.findViewById(R.id.shufle);
        repeat = view.findViewById(R.id.repeat);
        equalizer = view.findViewById(R.id.equalizer);
        context=getContext();
        playlist = view.findViewById(R.id.playlist);
        imageCard = view.findViewById(R.id.cardView);
        mVuMeterView = (VuMeterView) view.findViewById(R.id.vumeter);
        forShuffle = new ArrayList<>();



        textView.setText(songs.get(pos).getSongName());

        Uri uri =Uri.parse(songs.get(pos).getPath());
        if(!mediaPlayer.isPlaying())
        {
            play.setImageResource(R.drawable.ic_baseline_play_circle_filled_24);
            mVuMeterView.stop(true);
        }

//        mediaPlayer =MediaPlayer.create(context, uri);
//        mediaPlayer.start();

        mmr.setDataSource(context, uri);
        rawArt = mmr.getEmbeddedPicture();

        if (null != rawArt)
            art = BitmapFactory.decodeByteArray(rawArt, 0, rawArt.length, bfo);
        else
        {
            art = null;
        }
//        Bitmap bitmap = ThumbnailUtils.createAudioThumbnail(songs.get(position), MediaStore.Images.Thumbnails.MINI_KIND);

        Glide.with(context)
                .load(art)
                .error(R.drawable.music22)
                .into(imageView);


        textView.setSelected(true);
        seekBar.setMax(mediaPlayer.getDuration());

        DialogEqualizerFragment fragment = DialogEqualizerFragment.newBuilder()
                .setAudioSessionId(mediaPlayer.getAudioSessionId())
                .themeColor(ContextCompat.getColor(context, R.color.black))
                .textColor(ContextCompat.getColor(context, R.color.red))
                .accentAlpha(ContextCompat.getColor(context, R.color.red))
                .darkColor(ContextCompat.getColor(context, R.color.red))
                .setAccentColor(ContextCompat.getColor(context, R.color.red))
                .build();

        bassBoost =new BassBoost(8,mediaPlayer.getAudioSessionId());
        bassBoost.setStrength((short) 1000);


        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seekBar.getProgress());
            }
        });

        updateSeekMethod();

        ScheduledExecutorService myScheduledExecutorService = Executors.newScheduledThreadPool(1);

        myScheduledExecutorService.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                monitorHandler.sendMessage(monitorHandler.obtainMessage());
            }
        },200,200,TimeUnit.MILLISECONDS);

        equalizer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sessionId = mediaPlayer.getAudioSessionId();
//
//                EqualizerFragment equalizerFragment = EqualizerFragment.newBuilder()
//                        .setAccentColor(Color.parseColor("#4caf50"))
//                        .setAudioSessionId(sessionId)
//                        .build();
////                getFragmentManager().beginTransaction()
////                        .replace(R.id.eqFrame, equalizerFragment)
////                        .commit();
//
//                fragment.show(equalizerFragment.getFragmentManager(), "eq");
            }
        });

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mediaPlayer.isPlaying()){
                    if(mediaPlayer.getCurrentPosition()==mediaPlayer.getDuration())
                    {
                        updateSeekMethod();
                    }
                    play.setImageResource(R.drawable.ic_baseline_play_circle_filled_24);
                    musicState.setImageResource(R.drawable.play);
                    mVuMeterView.stop(true);
                    mediaPlayer.pause();

                }
                else{
                    if(mediaPlayer.getCurrentPosition()==mediaPlayer.getDuration())
                    {
                        updateSeekMethod();
                    }
                    play.setImageResource(R.drawable.ic_baseline_pause_circle_filled_24);
                    musicState.setImageResource(R.drawable.ic_baseline_pause_24);
                    mVuMeterView.resume(true);
                    mediaPlayer.start();
                }

            }
        });




        shuffle.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // shuffleBool = false - no shuffle
                // shuffleBool = true - shuffle
                if(shuffleBool)
                {

                    shuffle.setColorFilter(ContextCompat.getColor(context,R.color.grey));
                    shuffleBool=false;
                }
                else
                {
                    shuffle.setColorFilter(ContextCompat.getColor(context,R.color.red));
                    for (int i=1; i<songs.size(); i++) {
                        forShuffle.add(i);
                    }
                    Collections.shuffle(forShuffle);
                    shuffleBool=true;
                }
            }
        });

        repeat.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (repInt==0)
                {
                    // autoPlay
                    repeat.setColorFilter(ContextCompat.getColor(context,R.color.red));
                    repInt=1;
                }
                else if (repInt==1)
                {
                    // repeat infinitely
                    repeat.setImageResource(R.drawable.repeat_one);
                    repInt=2;
                }
                else
                {
                   // stop music
                    repeat.setImageResource(R.drawable.repeat);
                    repeat.setColorFilter(ContextCompat.getColor(context,R.color.grey));
                    repInt=0;
                }
            }
        });



        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
                if(shuffleBool)
                {
                    if (iterator == 0)
                    {
                        iterator = (songs.size() -2);
                        pos = forShuffle.get(iterator);
                    }
                    else
                    {
                        iterator--;
                        pos =forShuffle.get(iterator);
                    }
                }
                else if(pos!=0){
                    pos = pos - 1;
                }
                else{
                    pos = songs.size() - 1;
                }
               changeSongEffect();

            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;

                if(shuffleBool)
                {
                    if (iterator!=songs.size()-2)
                    {
                        iterator++;
                        pos = forShuffle.get(iterator);
                    }
                    else
                    {
                        iterator = 0;
                        Collections.shuffle(forShuffle);
                        pos = forShuffle.get(iterator);
                        iterator ++;
                    }
                    Log.e(String.valueOf(iterator), String.valueOf(pos));
                }
                else if(pos!=songs.size()-1){
                    pos = pos + 1;
                }
                else{
                    pos = 0;
                }
               changeSongEffect();
            }
        });

        return view;
    }


    Handler monitorHandler = new Handler(){
        @Override
        public void handleMessage( Message msg) {
            mediaPlayerMonitor();
        }
    };

    private void mediaPlayerMonitor() {
        try {

            int mediaDuration = mediaPlayer.getDuration();
            int mediaPosition = mediaPlayer.getCurrentPosition();
            pogress.setText(getCurrTime(mediaPosition));
            duration.setText(getCurrTime(mediaDuration));
            seekBar.setProgress(mediaPosition);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();

        if (dialog != null) {
            View bottomSheet = dialog.findViewById(R.id.design_bottom_sheet);
            bottomSheet.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
        }
        View view = getView();
        view.post(() -> {
            View parent = (View) view.getParent();
            CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) (parent).getLayoutParams();
            CoordinatorLayout.Behavior behavior = params.getBehavior();
            BottomSheetBehavior bottomSheetBehavior = (BottomSheetBehavior) behavior;
            bottomSheetBehavior.setPeekHeight(view.getMeasuredHeight());

        });
    }



    public  void changeSongEffect()
    {
        Uri uri = Uri.parse(songs.get(pos).getPath());
        mediaPlayer = MediaPlayer.create(context, uri);
        mediaPlayer.start();
        play.setImageResource(R.drawable.ic_baseline_pause_circle_filled_24);
        seekBar.setMax(mediaPlayer.getDuration());
        textView.setText(songs.get(pos).getSongName());
        seekBar.setProgress(0);
        musicTitle.setText(songs.get(pos).getSongName());
        musicArtist.setText(songs.get(pos).getArtist());
        Glide.with(context)
                .load(songs.get(pos).getBitmap())
                .error(R.drawable.music22)
                .into(musicImage);
//        Bitmap bitmap = ThumbnailUtils.createAudioThumbnail(songs.get(position), MediaStore.Images.Thumbnails.MINI_KIND);



        mmr.setDataSource(context, uri);
        rawArt = mmr.getEmbeddedPicture();
        if (null != rawArt)
            art = BitmapFactory.decodeByteArray(rawArt, 0, rawArt.length, bfo);
        else
        {
            art = null;
        }

        Glide.with(context)
                .load(art)
                .error(R.drawable.music22)
                .into(imageView);
        imageCard.setTranslationX(1000);
        imageCard.animate().translationXBy(-1000).setDuration(100);
        updateSeekMethod();
    }

    public int getRandomNum()
    {
        Random random = new Random();
        randomNum =random.nextInt(songs.size()+1);
        return randomNum;
    }

    public void updateSeekMethod()
    {

        updateSeek = new Thread(){
            @Override
            public void run() {
                int currentPosition = 0;
                try{
                    while(currentPosition<mediaPlayer.getDuration()){
                        currentPosition = mediaPlayer.getCurrentPosition();
                        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                            public void onCompletion(MediaPlayer mp) {

                                if (repInt == 1 && !shuffleBool)
                                {
                                    if(pos!=songs.size()-1){
                                        pos = pos + 1;
                                    }
                                    else{
                                        pos = 0;
                                    }
                                    changeSongEffect();
                                }
                                else if(repInt==1 && shuffleBool)
                                {
                                    if (iterator!=songs.size()-1)
                                    {
                                        iterator++;
                                        pos = forShuffle.get(iterator);
                                    }
                                    else
                                    {
                                        Collections.shuffle(forShuffle);
                                        iterator = 0;
                                        pos = forShuffle.get(iterator);
                                    }
                                    changeSongEffect();
                                }
                                else if(repInt==2)
                                {
                                    changeSongEffect();
                                }
                                else
                                {
                                    pogress.setText(duration.getText());
                                    play.setImageResource(R.drawable.ic_baseline_play_circle_filled_24);
                                }
                            }
                        });
                        sleep(800);
                    }
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        };

        updateSeek.start();

    }

}