package com.project.enlearner;
import android.os.AsyncTask;

import java.io.IOException;

public class WebsiteScraperImp extends AsyncTask<Void, Void, String>
{
    public ScraperResponse delegate = null;
    private WebsiteScraper websiteScraper;
    private String scrapedContent;

    public interface ScraperResponse
    {
        void processFinished(String output);
    }

    public WebsiteScraperImp(ScraperResponse delegate, String word)
    {
        this.delegate = delegate;
        websiteScraper = new WebsiteScraper(word);
    }

    @Override
    protected void onPreExecute()
    {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(Void... voids)
    {
        try
        {
            scrapedContent = websiteScraper.startScraping();
        }
        catch (IOException e)
        {
            scrapedContent = "Connection to the internet failed.";
        }

        return scrapedContent;
    }

    @Override
    protected void onPostExecute(String result)
    {
        delegate.processFinished(result);
    }
}
