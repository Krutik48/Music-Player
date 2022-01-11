package com.example.music3;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;

import static com.example.music3.MainActivity.songs;

/**
 * A simple {@link Fragment} subclass.
 * Use the  factory method to
 * create an instance of this fragment.
 */
public class songView extends BottomSheetDialogFragment{


    ListView listView;
    SongAdapter arrayAdapter;


    public songView() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_song_view, container, false);

        listView = view.findViewById(R.id.songListView);


        arrayAdapter = new SongAdapter(getContext(), songs,null,-1);
        listView.setAdapter(arrayAdapter);

        return view;

    }
}