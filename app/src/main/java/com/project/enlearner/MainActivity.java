package com.project.enlearner;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import java.util.HashSet;


//TODO
//read words from shared pref
//custom list layout for difficult words with thrash
//alert how many days

public class MainActivity extends WearableActivity implements Runnable
{
    private String word = "";
    private final int wordsCount = 362;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        WordsContainer wordsContainer = new WordsContainer();

        setAmbientEnabled();
        startService(new Intent(this, BackgroundService.class));

        sharedPreferences = this.getSharedPreferences("com.project.enlearner", Context.MODE_PRIVATE);
        //clearSharedPreferences();

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

    public void addToMemory(View view)
    {
        final Button button = findViewById(R.id.wordButton);
        String word = button.getText().toString();
        HashSet<String> sharedPrefSet = new HashSet<String>();

        try
        {
            if(sharedPreferences.contains("words"))
            {
                sharedPrefSet = (HashSet<String>) sharedPreferences.getStringSet("words", new HashSet<String>());
            }
            sharedPrefSet.add(word);
            sharedPreferences.edit().putStringSet("words", sharedPrefSet).apply();
            Toast.makeText(getApplicationContext(), "Word has been added", Toast.LENGTH_SHORT).show();
        }
        catch (Exception e)
        {
            Toast.makeText(getApplicationContext(), "Try again later", Toast.LENGTH_SHORT).show();
        }
    }

    /***method used only to clear shared prefs from device**/
    private void clearSharedPreferences()
    {
        SharedPreferences preferences = getSharedPreferences("com.project.enlearner", 0);
        preferences.edit().clear().commit();
    }

    public void showSavedWords(View view)
    {
        //start new activity with list and option to delete
        //sharedPreferences.getString("username", "");  //returns s1 if nothing is there
        //ArrayList<String> sharedPrefList = (ArrayList<String>)
        //                    ObjectSerializer.deserialize(sharedPreferences.getString("words", ObjectSerializer.serialize(new ArrayList<String>())));
    }

    @Override
    public void run()
    {
        //do downloads in seperate threads
    }
}