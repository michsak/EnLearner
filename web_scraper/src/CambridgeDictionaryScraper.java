import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;
import java.io.IOException;

public class CambridgeDictionaryScraper
{
    private static final int tableSize = 3;
    private static final String word = "aggravate";

    public static void main (String[] args)
    {
        String[] text = scrapeAllFromPage(String.format("https://dictionary.cambridge.org/pl/dictionary/english/%s", word));
        String[] clearText = new String[tableSize];

        for (int i=0; i<tableSize; i++)
        {
            clearText[i] = removeAllRedundantAttributes(text[i]);
        }

        if (clearText[0] != null && !clearText[0].equals("") && !clearText[0].startsWith("past "))
        {
            System.out.println(clearText[0]);
        }
        else
        {
            System.out.println(clearText[1]);
        }
    }

    public static String[] scrapeAllFromPage(String link)
    {
        String[] rawDefinition = new String[tableSize];

        try
        {
            Document doc = Jsoup.connect(link).get();
            Elements allContent = doc.getElementsByClass("def ddef_d db");

            int i = 0;
            for (Element seperateLine : allContent)
            {
                rawDefinition[i] = seperateLine.toString();
                i += 1;
                if (i >= tableSize) {break; }
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
