package science.larry.dmojcraft.dmoj;

import java.io.IOException;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.jsoup.Jsoup;
import org.jsoup.Connection.Response;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import io.github.furstenheim.CopyDown;
import org.jsoup.HttpStatusException;
import org.jsoup.safety.Safelist;
import org.jsoup.select.Elements;

public class ProblemFetcher {
    private final static String BASE_URL = "https://dmoj.ca";

    private static String processHtml(Response res) throws IOException {
        CopyDown converter = new CopyDown();

        Document document = res.parse();
        
        document.getElementById("comments").remove();

        Elements links = document.select("a");
        for (Element link : links) link.replaceWith(new Element("p").text(link.text()));

        Element content = document.selectFirst("div#content-left");
        return converter.convert(
            Jsoup.clean(
                content.html(),
                Safelist.basic()
                    .addTags(
                        "h1", "h2", "h3", "h4", "h5", "h6",
                        "p", "ul", "ol", "li", "strong", "em", "code", "pre"
                    )
            )
        ).replaceAll("~","")
         .replaceAll("\\\\\\\\times", "*") // those are \\ for like escaped latex shit
         .replaceAll("\\\\\\\\frac", "/")
         .replaceAll("\\\\\\\\ne", "!=")
         .replaceAll("\\\\\\\\ge", ">=")
         .replaceAll("\\\\\\\\le", "<=")
         .replaceAll("\\\\\\\\sqrt", "sqrt_")
         .replaceAll("\\\\\\\\,", "_");
    }

    private static String getProblemContent(String problemURL) throws IOException, HttpStatusException {
        Response dmojProblemResponse;
        dmojProblemResponse = Jsoup.connect(problemURL)
                .followRedirects(true)
                .execute();
        return processHtml(dmojProblemResponse);
    }

    public static ItemStack readProblem(String problem) throws IOException, HttpStatusException {
        String problemURL = BASE_URL + "/problem/" + problem;

        ItemStack book = new ItemStack(Material.WRITTEN_BOOK);

        BookMeta bookMeta = (BookMeta) book.getItemMeta();
        bookMeta.setTitle("Problem: " + problem);
        bookMeta.setAuthor("DMOJCraft");

        String content = getProblemContent(problemURL);

        for (int i = 0, j = 0; i < content.length() && j < 100; i += 200, j++)
        {
            String page = content.substring(i, Math.min(i + 200, content.length()));
            bookMeta.addPage(page);
        }

        book.setItemMeta(bookMeta);

        return book;
    }
}
