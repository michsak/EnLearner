package com.project.enlearner;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;

public class WebsiteScraper
{
    private String websiteName;
    private String websiteContent;

    public WebsiteScraper(String websiteName)
    {
        this.websiteName = websiteName;
    }

    private void searchWordMeaning() throws IOException
    {
        Document doc = Jsoup.connect(websiteName).get();
        Elements newsHeadlines = doc.select("#mp-itn b a");
        for (Element headline : newsHeadlines)
        {
            headline.attr(("title"), headline.absUrl("href"));
        }
    }
}
