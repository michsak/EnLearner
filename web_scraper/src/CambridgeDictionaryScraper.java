import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;
import java.io.IOException;

public class CambridgeDictionaryScraper
{
    private static final int scrapingTableSize = 2;
    private static final String word = "complicit";

    public static void main (String[] args)
    {
        String[] definitionText = scrapeFromPage(String.format("https://dictionary.cambridge.org/pl/dictionary/english/%s", word), "def ddef_d db");
        String[] examplesText = scrapeFromPage(String.format("https://dictionary.cambridge.org/pl/dictionary/english/%s", word), "examp dexamp");
        String[] clearDefinitionText = new String[scrapingTableSize];
        String[] clearExamplesText = new String[scrapingTableSize];

        for (int i = 0; i< scrapingTableSize; i++)
        {
            clearDefinitionText[i] = removeAllRedundantAttributes(definitionText[i]);
            clearExamplesText[i] = removeAllRedundantAttributes(examplesText[i]);
        }

        if (clearDefinitionText[0] != null && !clearDefinitionText[0].equals("") && !clearDefinitionText[0].startsWith("past "))
        {
            System.out.println(clearDefinitionText[0]);
        }

        else
        {
            System.out.println(clearDefinitionText[1]);
        }

        if (clearExamplesText[0] != null && !clearExamplesText[0].equals(""))
        {
            System.out.println(clearExamplesText[0]);
        }

        else
        {
            System.out.println(clearExamplesText[1]);
        }
    }


    private static String[] scrapeFromPage(String link, String divClassName)
    {
        String[] rawDefinition = new String[scrapingTableSize];

        try
        {
            Document doc = Jsoup.connect(link).get();
            Elements allContent = doc.getElementsByClass(divClassName);

            int i = 0;
            for (Element seperateLine : allContent)
            {
                rawDefinition[i] = seperateLine.toString();
                i += 1;
                if (i >= scrapingTableSize) {break; }
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return rawDefinition;
    }


    private static String removeAllRedundantAttributes(String text)
    {
        String finalText = "";
        if (text != null && !text.equals(""))
        {
            String message = Jsoup.clean(text, Whitelist.none());
            finalText = message;

            if (message.endsWith(":"))
            {
                finalText = message.substring(0, message.length()-1);
            }
        }

        if (text == null || text.equals(""))
        {
            finalText = "Word not found in the dictionary";
        }

        return finalText;
    }
}
