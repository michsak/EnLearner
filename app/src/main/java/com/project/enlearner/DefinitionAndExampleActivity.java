package com.project.enlearner;
import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.widget.TextView;

public class DefinitionAndExampleActivity extends WearableActivity
{
    private String word;
    private TextView descriptionTextView;
    private TextView exampleTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        word = intent.getStringExtra(getString(R.string.db_column));
        setContentView(R.layout.activity_definition_and_example);
        descriptionTextView = findViewById(R.id.definitionText);
        exampleTextView = findViewById(R.id.exampleText);
        setAmbientEnabled();
        scrapeDefinitionAndExampleFromWebsite();
    }

    public void scrapeDefinitionAndExampleFromWebsite()
    {
        WebsiteScraperImp websiteScraperImp = (WebsiteScraperImp) new WebsiteScraperImp(new WebsiteScraperImp.ScraperResponse()
        {
            @Override
            public void processFinished(String output)
            {
                String lines[] = output.split("\\r?\\n");
                descriptionTextView.setText(lines[0]);
                exampleTextView.setText(lines[lines.length-1]+"\n\n");
            }
        }, word).execute();
    }
}
