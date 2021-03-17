package com.project.enlearner;
import java.util.ArrayList;

public class WordsContainer
{
    public static final int maxNumberOfWordsInMemory = 10;
    public static ArrayList<String> savedWords;

    WordsContainer()
    {
        savedWords = new ArrayList<String>();
    }

    public static void removeFirstElementFromWordList()   //may be wrong with indexing
    {
        savedWords.remove(0);
        ArrayList<String> newSavedWords = new ArrayList<String>();
        newSavedWords.addAll(savedWords);
    }

    public static boolean checkIfNumberOfSavedWordsReachesMax()
    {
        boolean result = savedWords.size() >= maxNumberOfWordsInMemory ? true : false;
        return result;
    }
}
