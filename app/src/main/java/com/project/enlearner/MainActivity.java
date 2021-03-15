package com.project.enlearner;
import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;


//TODO
//save difficult words in shared preferences
//custom list layout for difficult words with thrash
//alert how many days

public class MainActivity extends WearableActivity implements Runnable
{
    private String word = "";
    private final int wordsCount = 362;

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
        final Button button = findViewById(R.id.wordButton);
        final TextView descriptionTextView = findViewById(R.id.definitionTextView);
        final TextView exampleTextView = findViewById(R.id.exampleTextView);
        WebsiteScraperImp websiteScraperImp = (WebsiteScraperImp) new WebsiteScraperImp(new WebsiteScraperImp.ScraperResponse()
        {
            @Override
            public void processFinished(String output)
            {
                String lines[] = output.split("\\r?\\n");
                button.setVisibility(View.INVISIBLE);
                descriptionTextView.setText(lines[0]);
                descriptionTextView.setVisibility(View.VISIBLE);
                exampleTextView.setText(lines[lines.length-1]);
                exampleTextView.setVisibility(View.VISIBLE);
            }
        }, button.getText().toString()).execute();

    }

    public void addToWords(View view)
    {

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