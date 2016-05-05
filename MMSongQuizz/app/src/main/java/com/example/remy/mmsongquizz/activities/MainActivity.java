package com.example.remy.mmsongquizz.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.remy.mmsongquizz.R;

import java.util.ArrayList;
import java.util.HashMap;

import models.Artist;
import models.Genre;
import services.ArtistManager;
import services.GenreManager;
import utils.AsyncHttpRequest;
import utils.EchonestUtils;
import utils.HttpUtils;
import utils.Logger;

public class MainActivity extends BaseActivity {
    private Button startBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkNetwork();

        startBtn = (Button) findViewById(R.id.startBtn);

        initView();



        GenreManager genreManager = application.getContainer().get(GenreManager.class);
        ArtistManager artistManager = application.getContainer().get(ArtistManager.class);

        Genre genre = genreManager.getRandom();
        Logger.debug("random genre: " + genre.getName());

        ArrayList<Artist> artists = artistManager.getByGenre(genre.getName());
        for(Artist artist : artists){
            Logger.debug("artist id:" + artist.getId()+" name:" + artist.getName());
        }


    }

    private void initView(){
        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toQuestion = new Intent(MainActivity.this, QuestionActivity.class);
                startActivity(toQuestion);
            }
        });
    }


}
