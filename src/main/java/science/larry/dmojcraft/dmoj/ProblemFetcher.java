package science.larry.dmojcraft.dmoj;

import java.io.IOException;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.jsoup.Jsoup;
import org.jsoup.Connection.Response;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Safelist;
import org.jsoup.HttpStatusException;

public class ProblemFetcher {
    private final static String BASE_URL = "https://dmoj.ca";

    private static String br2nl(Response res) throws IOException{
        Document document = res.parse();
        document.outputSettings(new Document.OutputSettings().prettyPrint(false));//makes html() preserve linebreaks and spacing
        document.select("br").append("\\n");
        document.select("p").prepend("\\n\\n");
        String s = document.html().replaceAll("\\\\n", "\n");
        return Jsoup.clean(s, "", Safelist.none(), new Document.OutputSettings().prettyPrint(false));
    }

    private static String getProblemContent(String problemURL) throws IOException, HttpStatusException {
        Response dmojProblemResponse;
        dmojProblemResponse = Jsoup.connect(problemURL)
                .followRedirects(true)
                .execute();
        return br2nl(dmojProblemResponse);
    }

    public static ItemStack readProblem(String problem) throws IOException, HttpStatusException {
        String problemURL = BASE_URL + "/problem/" + problem;

        ItemStack book = new ItemStack(Material.WRITTEN_BOOK);

        BookMeta bookMeta = (BookMeta) book.getItemMeta();
        bookMeta.setTitle("Problem: " + problem);
        bookMeta.setAuthor("DMOJCraft");

        String content = getProblemContent(problemURL);

        for (int i = 0, j = 0; i < content.length() && j < 100; i += 255, j++)
        {
            String page = content.substring(i, Math.min(i + 255, content.length()));
            bookMeta.addPage(page);
        }

        book.setItemMeta(bookMeta);

        return book;
    }
}
