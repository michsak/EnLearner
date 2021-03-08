package com.project.enlearner;
import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ThemedSpinnerAdapter;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

//TODO
//website-scraping and showing in textbox
//read words from database - database config
//alert how many days

public class MainActivity extends WearableActivity implements Runnable
{
    private String word = "";
    private final int wordsCount = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        WordsContainer wordsContainer = new WordsContainer();

        setAmbientEnabled();
        startService(new Intent(this, BackgroundService.class));

        for (int i=0; i<WordsContainer.maxNumberOfWordsInMemory; i++)
        {
            if (!WordsContainer.checkIfNumberOfSavedWordsReachesMax())
            {
                downloadSingleWordFromDataBase();    //do in other thread
            }
        }
    }

    private void downloadSingleWordFromDataBase()
    {
        int randomWordNumber = (int)(Math.random()*(wordsCount));

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Words");
        query.orderByAscending("word");
        query.setSkip(randomWordNumber);
        query.getFirstInBackground(new GetCallback<ParseObject>()
        {
            @Override
            public void done(ParseObject object, ParseException e)
            {
                if (e==null && object!=null)
                {
                    word = object.getString("word").toString();
                    WordsContainer.savedWords.add(word);
                }
                else
                {
                    word = "Loading";
                }
                setButtonText();  //in future should be invoked elsewhere
            }
        });
    }

    private void setButtonText()
    {
        final Button wordButton = findViewById(R.id.wordButton);  //may be needed to initialize inside
        wordButton.setText(WordsContainer.savedWords.get(0));
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

    @Override
    public void run()
    {
        //do downloads in seperate threads
    }
}