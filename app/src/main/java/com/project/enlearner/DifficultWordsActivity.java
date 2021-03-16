package com.project.enlearner;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.HashSet;

public class DifficultWordsActivity extends WearableActivity
{
    private SharedPreferences sharedPreferences;
    private ListView wordsListView;
    private ArrayList<String> listOfDifficultWords;
    private ArrayAdapter<String> difficultWordsArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setTitle("Difficult Words");
        setContentView(R.layout.activity_difficult_words);
        sharedPreferences = this.getSharedPreferences("com.project.enlearner", Context.MODE_PRIVATE);
        setAmbientEnabled();
        showWords();
        enableDeletingWords();
    }

    private void showWords()
    {
        wordsListView = findViewById(R.id.difficultWordsListView);
        HashSet<String> setOfDifficultWords = new HashSet<String>();
        listOfDifficultWords = new ArrayList<String>();
        setOfDifficultWords = (HashSet<String>) sharedPreferences.getStringSet("words", new HashSet<String>());

        for (String word : setOfDifficultWords)
        {
            listOfDifficultWords.add(word);
        }
        difficultWordsArrayAdapter =
                new ArrayAdapter<String>(this, R.layout.center_text, listOfDifficultWords);
        wordsListView.setAdapter(difficultWordsArrayAdapter);
    }

    private void enableDeletingWords()
    {
        wordsListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()
        {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l)
            {
                Log.i("info", listOfDifficultWords.get(i));
                MainActivity.deleteWordFromSharedPreferences(sharedPreferences, listOfDifficultWords.get(i), getApplicationContext());
                listOfDifficultWords.remove(i);
                difficultWordsArrayAdapter.notifyDataSetChanged();
                Toast.makeText(getApplicationContext(), "Word has been deleted", Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }
}
