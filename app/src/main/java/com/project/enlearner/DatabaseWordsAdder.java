package com.project.enlearner;
import android.util.Log;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.SaveCallback;

/**Class purpose is to add new words to database**/
public class DatabaseWordsAdder
{
    DatabaseWordsAdder(String[] words, int startNumber)
    {
        iterateThroughAllTextFileLines(words, startNumber);
    }

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

    private void iterateThroughAllTextFileLines(String[] words, int initialNumber)
    {
        for (String word : words)
        {
            addWordsToDatabase(word, initialNumber);
            initialNumber += 1;
        }
    }
}
