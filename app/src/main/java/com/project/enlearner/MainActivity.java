package com.project.enlearner;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

//TODO
//take random word from parser
//website-scraping and showing in textbox
//read words from database - database config
//alert how many days

public class MainActivity extends WearableActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        WordsContainer wordsContainer = new WordsContainer();

        setAmbientEnabled();

        startService(new Intent(this, BackgroundService.class));
    }

    public void onWordClicked(View view)
    {
        //go to website
        //take description
        //write down in textfield
    }

    public void addToWords(View view)
    {
        WordsContainer.savedWords.add("word");
    }

    public void showSavedWords(View view)
    {
        //start new activity with list and option to delete
    }


}