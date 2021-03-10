package com.project.enlearner;
import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;


//TODO
//website-scraping and showing in textbox
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

    /**Method used only to add new words to database**/
    private void addWordsToDatabase(String word, int number)
    {
        ParseObject words = new ParseObject("Words");
        words.put("word", word);
        words.put("number", number);
        words.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e)
            {
                if (e==null)
                {
                    Log.i("info", "Success");
                }
                else
                {
                    Log.i("info", "Something went wrong. Try again.");
                    e.printStackTrace();
                }
            }
        });
    }

    /**Method used only to add new words to database**/
    private void iterateThroughAllTextFileLines(String[] words, int initialNumber)
    {
        for (String word : words)
        {
            addWordsToDatabase(word, initialNumber);
            initialNumber += 1;
        }
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
        Button button = findViewById(R.id.wordButton);
        String websiteName = "https:dictionary.cambridge.org/dictionary/english/".concat(button.getText().toString());

        WebsiteScraper websiteScraper = new WebsiteScraper(websiteName);
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