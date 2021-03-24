package com.project.enlearner;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
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
import java.util.Calendar;
import java.util.HashSet;


//TODO
//take words from saved
//add icon
//unit tests

public class MainActivity extends WearableActivity
{
    private final int wordsCount = 362;
    private final int[] intervalTimePossibilities = new int[]{12, 16, 24};
    private String word = "";
    private int intervalTime = 24;
    private SharedPreferences sharedPreferences;
    private boolean isWordReadyToBeDisplayed = true;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        WordsContainer wordsContainer = new WordsContainer();
        setAmbientEnabled();
        sharedPreferences = this.getSharedPreferences("com.project.enlearner", Context.MODE_PRIVATE);
        //clearSharedPreferences();
        showDialog();
        setUpNotification(intervalTime);
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        for (int i=WordsContainer.savedWords.size(); i<WordsContainer.maxNumberOfWordsInMemory; i++)
        {
            if (!WordsContainer.checkIfNumberOfSavedWordsReachesMax())
            {
                downloadSingleWordFromDataBase();
            }
        }
    }

    private void showDialog()
    {
        final Dialog dialog = new Dialog(this);
        View myLayout = getLayoutInflater().inflate(R.layout.dialog_layout, null);
        setButtonListener(dialog, myLayout, R.id.twelveHoursButton, 0);
        setButtonListener(dialog, myLayout, R.id.sixteenHoursButton, 1);
        setButtonListener(dialog, myLayout, R.id.twentyfourHoursButton, 2);
        dialog.setContentView(myLayout);
        dialog.show();
    }

    private void setButtonListener(final Dialog dialog, View myLayout, int buttonId, final int timeId)
    {
        Button positiveButton = myLayout.findViewById(buttonId);
        positiveButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                intervalTime = intervalTimePossibilities[timeId];
                dialog.cancel();
            }
        });
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
                    if (isWordReadyToBeDisplayed)
                    {
                        WordsContainer.savedWords.add(word);
                        isWordReadyToBeDisplayed = false;
                        setButtonText();
                    }
                }
                else
                {
                    word = "Loading";
                }
            }
        });
    }

    private void setButtonText()
    {
        final Button wordButton = findViewById(R.id.wordButton);
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

    public void onExampleOrDescriptionClicked(View view)
    {
        final TextView descriptionTextView = findViewById(R.id.definitionTextView);
        final TextView exampleTextView = findViewById(R.id.exampleTextView);
        final Button button = findViewById(R.id.wordButton);
        descriptionTextView.setText("");
        descriptionTextView.setVisibility(View.INVISIBLE);
        exampleTextView.setText("");
        exampleTextView.setVisibility(View.INVISIBLE);
        button.setVisibility(View.VISIBLE);
    }

    public void addToMemory(View view)
    {
        final Button button = findViewById(R.id.wordButton);
        String word = button.getText().toString();
        addWordToSharedPreferences(word);
    }

    private void addWordToSharedPreferences(String word)
    {
        try
        {
            HashSet<String> sharedPrefSet = new HashSet<String>();
            if(sharedPreferences.contains("words"))
            {
                sharedPrefSet = (HashSet<String>) sharedPreferences.getStringSet("words", new HashSet<String>());
            }
            sharedPrefSet.add(word);
            sharedPreferences.edit().clear().apply();
            sharedPreferences.edit().putStringSet("words", sharedPrefSet).apply();
            Toast.makeText(getApplicationContext(), "Word has been added", Toast.LENGTH_SHORT).show();

            for (String wordnew : sharedPrefSet)
            {
                Log.i("info", wordnew);
            }
        }
        catch (Exception e)
        {
            Toast.makeText(getApplicationContext(), "Try again later", Toast.LENGTH_SHORT).show();
        }
    }

    public static void deleteWordFromSharedPreferences(SharedPreferences sharedPreferences, String word, Context context)
    {
        HashSet<String> sharedPrefSet = new HashSet<String>();
        if(sharedPreferences.contains("words"))
        {
            sharedPrefSet = (HashSet<String>) sharedPreferences.getStringSet("words", new HashSet<String>());
        }
        sharedPrefSet.remove(word);
        sharedPreferences.edit().clear().apply();
        sharedPreferences.edit().putStringSet("words", sharedPrefSet).apply();
        Toast.makeText(context, "Word has been deleted", Toast.LENGTH_SHORT).show();
    }

    /***method used only to clear shared prefs from device**/
    private void clearSharedPreferences()
    {
        SharedPreferences preferences = getSharedPreferences("com.project.enlearner", 0);
        preferences.edit().clear().commit();
    }

    public void showSavedWords(View view)
    {
        if (sharedPreferences.contains("words"))
        {
            Intent intent = new Intent(getApplicationContext(), DifficultWordsActivity.class);
            startActivity(intent);
        }
        else
        {
            Toast.makeText(getApplicationContext(), "You haven't saved any word", Toast.LENGTH_SHORT);
        }
    }

    private void setUpNotification(int interval)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY));
        calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE));
        Intent intent = new Intent(getApplicationContext(), NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 100, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_HOUR*interval, pendingIntent);
    }
}
