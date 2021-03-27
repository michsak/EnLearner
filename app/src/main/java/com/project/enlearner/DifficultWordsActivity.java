package com.project.enlearner;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
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
        setTitle(getString(R.string.difficult_words_string));
        setContentView(R.layout.activity_difficult_words);
        sharedPreferences = this.getSharedPreferences(getString(R.string.difficult_word_preferences), Context.MODE_PRIVATE);
        setAmbientEnabled();
        showWords();
        enableDeletingWords();
        enableGoToDefinitionAndExampleActivity();
    }

    private void showWords()
    {
        wordsListView = findViewById(R.id.difficultWordsListView);
        HashSet<String> setOfDifficultWords = new HashSet<String>();
        listOfDifficultWords = new ArrayList<String>();
        setOfDifficultWords = (HashSet<String>) sharedPreferences.getStringSet(getString(R.string.new_shared_pref_word), new HashSet<String>());

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
                MainActivity.deleteWordFromSharedPreferences(sharedPreferences, listOfDifficultWords.get(i), getApplicationContext(), getString(R.string.new_shared_pref_word));
                listOfDifficultWords.remove(i);
                difficultWordsArrayAdapter.notifyDataSetChanged();
                Toast.makeText(getApplicationContext(), getString(R.string.words_are_deleted), Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }

    private void enableGoToDefinitionAndExampleActivity()
    {
        wordsListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
            {
                Intent intent = new Intent(getApplicationContext(), DefinitionAndExampleActivity.class);
                intent.putExtra(getString(R.string.intent_extra_string), listOfDifficultWords.get(i));
                startActivity(intent);
            }
        });
    }
}
