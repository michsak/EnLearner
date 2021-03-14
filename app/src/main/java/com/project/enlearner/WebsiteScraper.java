package com.project.enlearner;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;
import java.io.IOException;

public class WebsiteScraper
{
    private String websiteName;
    private final int scrapingTableSize = 2;

    public WebsiteScraper(String word)
    {
        websiteName = "https:dictionary.cambridge.org/dictionary/english/".concat(word);
    }

    public String startScraping() throws IOException
    {
        String finalTextBoxContent = "";
        String[] definitionText = scrapeFromPage(websiteName, "def ddef_d db");
        String[] examplesText = scrapeFromPage(websiteName, "examp dexamp");
        String[] clearDefinitionText = new String[scrapingTableSize];
        String[] clearExamplesText = new String[scrapingTableSize];

        for (int i = 0; i< scrapingTableSize; i++)
        {
            clearDefinitionText[i] = removeAllRedundantAttributes(definitionText[i]);
            clearExamplesText[i] = removeAllRedundantAttributes(examplesText[i]);
        }
        if (clearDefinitionText[0] != null && !clearDefinitionText[0].equals("") && !clearDefinitionText[0].startsWith("past "))
        {
            finalTextBoxContent = clearDefinitionText[0] + "\n\n";
        }
        else
        {
            finalTextBoxContent = clearDefinitionText[1] + "\n\n";
        }
        if (clearExamplesText[0] != null && !clearExamplesText[0].equals(""))
        {
            finalTextBoxContent += clearExamplesText[0];
        }
        else
        {
            finalTextBoxContent += clearExamplesText[1];
        }

        return finalTextBoxContent;
    }


    private String[] scrapeFromPage(String link, String divClassName) throws IOException
    {
        String[] rawDefinition = new String[scrapingTableSize];

        Document doc = Jsoup.connect(link).get();
        Elements allContent = doc.getElementsByClass(divClassName);

        int i = 0;
        for (Element seperateLine : allContent)
        {
            rawDefinition[i] = seperateLine.toString();
            i += 1;
            if (i >= scrapingTableSize) {break; }
        }

        return rawDefinition;
    }

    private String removeAllRedundantAttributes(String text)
    {
        String finalText = "";
        if (text != null && !text.equals(""))
        {
            String message = Jsoup.clean(text, Whitelist.none());
            finalText = message;
            finalText = finalText.substring(0,1).toUpperCase() + finalText.substring(1, finalText.length());

            if (message.endsWith(":"))
            {
                finalText = finalText.substring(0, message.length()-1);
            }
        }

        if (text == null || text.equals(""))
        {
            finalText = "Word not found in the dictionary";
        }

        return finalText;
    }
}
