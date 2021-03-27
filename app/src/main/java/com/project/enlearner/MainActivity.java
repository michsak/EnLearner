package com.project.enlearner;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;

public class MainActivity extends WearableActivity implements Runnable
{
    private final int wordsCount = 458;
    private final int[] intervalTimePossibilities = new int[]{12, 16, 24};
    private final int maxNumberOfWordsInMemory = 5;
    private int intervalTime = 24;
    private boolean firstConnectionCheck = true;
    private String word;
    private SharedPreferences difficultWordsSharedPrefs;
    private SharedPreferences newWordsSharedPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setAmbientEnabled();
        difficultWordsSharedPrefs = this.getSharedPreferences(getString(R.string.difficult_word_preferences), Context.MODE_PRIVATE);
        newWordsSharedPrefs = this.getSharedPreferences(getString(R.string.new_word_preferences), Context.MODE_PRIVATE);
        word = getString(R.string.empty_text);
        onFirstRun();
        Thread internetConnectionCheck = new Thread();
        internetConnectionCheck.start();
    }

    @Override
    public void run()
    {
        while (isNetworkConnected() == true && firstConnectionCheck)
        {
            saveDownloadedWordsToSharedPrefs();
            firstConnectionCheck = false;
            try
            {
                Thread.sleep(15000);
            }
            catch (InterruptedException e)
            {
                Thread.currentThread().interrupt();
            }
        }
    }

    private boolean isNetworkConnected()
    {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }

    private void onFirstRun()
    {
        Boolean isFirstRun = getSharedPreferences(getString(R.string.first_run_preference), MODE_PRIVATE).getBoolean("isFirstRun", true);
        if (isFirstRun)
        {
            showDialog();
            setUpNotification(intervalTime);
            addNewWordToSharedPref(getString(R.string.first_word));
        }
        getSharedPreferences(getString(R.string.first_run_preference), MODE_PRIVATE).edit().putBoolean("isFirstRun", false).apply();
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        saveDownloadedWordsToSharedPrefs();
    }

    private void saveDownloadedWordsToSharedPrefs()
    {
        downloadSingleWordFromDataBase();
        ArrayList<String> listOfNewWords = new ArrayList<String>();
        HashSet<String> setOfNewWords = (HashSet<String>) newWordsSharedPrefs.getStringSet(getString(R.string.set_of_new_words), new HashSet<String>());

        for (String word : setOfNewWords)
        {
            listOfNewWords.add(word);
        }
        setOfNewWords.clear();
        try
        {
            setButtonText(listOfNewWords.get(0));
            for (int i=1; i<listOfNewWords.size(); i++)
            {
                setOfNewWords.add(listOfNewWords.get(i));
            }
            newWordsSharedPrefs.edit().putStringSet(getString(R.string.set_of_new_words), setOfNewWords).apply();
        }
        catch (IndexOutOfBoundsException ie)
        {
            setButtonText(getString(R.string.no_connection));
        }
        for (int i=0; i<maxNumberOfWordsInMemory-listOfNewWords.size(); i++)
        {
            downloadSingleWordFromDataBase();
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
        ParseQuery<ParseObject> query = ParseQuery.getQuery(getString(R.string.db_table_name));
        query.orderByAscending(getString(R.string.db_column));
        query.setSkip(randomWordNumber);
        query.getFirstInBackground(new GetCallback<ParseObject>()
        {
            @Override
            public void done(ParseObject object, ParseException e)
            {
                if (e==null && object!=null)
                {
                    word = object.getString(getString(R.string.db_column)).toString();
                    addNewWordToSharedPref(word);
                }
                else
                {
                    word = getString(R.string.loading_text);
                }
            }
        });
    }

    private void setButtonText(String text)
    {
        final Button wordButton = findViewById(R.id.wordButton);
        wordButton.setText(text);
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
        descriptionTextView.setText(getString(R.string.empty_text));
        descriptionTextView.setVisibility(View.INVISIBLE);
        exampleTextView.setText(getString(R.string.empty_text));
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
            if(difficultWordsSharedPrefs.contains(getString(R.string.new_shared_pref_word)))
            {
                sharedPrefSet = (HashSet<String>) difficultWordsSharedPrefs.getStringSet(getString(R.string.new_shared_pref_word), new HashSet<String>());
            }
            sharedPrefSet.add(word);
            difficultWordsSharedPrefs.edit().clear().apply();
            difficultWordsSharedPrefs.edit().putStringSet(getString(R.string.new_shared_pref_word), sharedPrefSet).apply();
            Toast.makeText(getApplicationContext(), getString(R.string.words_are_added), Toast.LENGTH_SHORT).show();
        }
        catch (Exception e)
        {
            Toast.makeText(getApplicationContext(), getString(R.string.try_again), Toast.LENGTH_SHORT).show();
        }
    }

    private void addNewWordToSharedPref(String word)
    {
        HashSet<String> sharedPrefSet = new HashSet<String>();
        if(newWordsSharedPrefs.contains(getString(R.string.set_of_new_words)))
        {
            sharedPrefSet = (HashSet<String>) newWordsSharedPrefs.getStringSet(getString(R.string.set_of_new_words), new HashSet<String>());
        }
        sharedPrefSet.add(word);
        newWordsSharedPrefs.edit().clear().apply();
        newWordsSharedPrefs.edit().putStringSet(getString(R.string.set_of_new_words), sharedPrefSet).apply();
    }

    public static void deleteWordFromSharedPreferences(SharedPreferences sharedPreferences, String word, Context context, String sharedPrefString)
    {
        HashSet<String> sharedPrefSet = new HashSet<String>();
        if(sharedPreferences.contains(sharedPrefString))
        {
            sharedPrefSet = (HashSet<String>) sharedPreferences.getStringSet(sharedPrefString, new HashSet<String>());
        }
        sharedPrefSet.remove(word);
        sharedPreferences.edit().clear().apply();
        sharedPreferences.edit().putStringSet(sharedPrefString, sharedPrefSet).apply();
        Toast.makeText(context, "Word has been deleted", Toast.LENGTH_SHORT).show();
    }

    /***method used only to clear shared prefs from device**/
    private void clearSharedPreferences(String name)
    {
        SharedPreferences preferences = getSharedPreferences(name, 0);
        preferences.edit().clear().commit();
    }

    public void showSavedWords(View view)
    {
        if (difficultWordsSharedPrefs.contains(getString(R.string.new_shared_pref_word)))
        {
            Intent intent = new Intent(getApplicationContext(), DifficultWordsActivity.class);
            startActivity(intent);
        }
        else
        {
            Toast.makeText(getApplicationContext(), getString(R.string.no_words_are_saved), Toast.LENGTH_SHORT);
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
