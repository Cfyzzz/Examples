import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.RecursiveAction;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class Domen extends RecursiveAction {
    private URL url;

    public Domen(URL url) {
        this.url = url;
    }

    public Domen(String address) {
        try {
            this.url = new URL(address);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public boolean save(int statusCode, String content) {
        synchronized (DBConnection.getConnection()) {
            try {
                if (check(url.getPath())) {
                    DBConnection.append(url.getPath(), statusCode, content);
                    return true;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public boolean check(String path) {
        try {
            return !DBConnection.isExistPath(path);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void compute() {
        HashSet<String> set = new HashSet<>();
        List<Domen> subdomens = new ArrayList<>();
        try {
            var response = Jsoup.connect(url.toString())
                    .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                    .referrer("http://www.google.com")
                    .timeout(3000)
                    .execute();
            boolean isSaved = save(response.statusCode(), response.body().replace('\"', '\''));
            if (!isSaved) {
                return;
            }
            set.add(url.toString());
            Document doc = response.parse();

            if (response.statusCode() == 200) {
                List<String> tags = getTags();
                List<String> lemmas = new ArrayList<>();
                for (String tag : tags) {
                    Elements tagsText = doc.getElementsByTag(tag);
                    for (Element tagText : tagsText) {
                        String text = tagText.text().toLowerCase(Locale.ROOT);
                        lemmas.addAll(MyLematizator.getWordsBaseForms(text));
                    }
                }
                DBConnection.addLemms(lemmas);
                // TODO: 11.03.2022 Сделать пункт 4.5 Возможно через Pair<tag, lemma>
            }

            Elements tagsA = doc.select("a");
            for (Element tagA : tagsA) {
                String href = tagA.attr("abs:href");
                if (href.contains(this.url.getHost())
                        && !set.contains(href)  // ссылки на теущей странице
                        && !href.matches(".+\\.\\w{3}$")  // картинки, т.е. *.XXX ссылки
                        && !href.matches(".+\\/\\d+\\/.*")  // ссылки с цифрами вида .../цифры/...
                        && !href.contains("#")  // переходы внутри страницы
                        && !href.contains("?")  // передача параметров
                ) {
                    set.add(href);
                    Domen subdomen = new Domen(href);
                    subdomens.add(subdomen);
                    Thread.sleep(200);
                    subdomen.fork();
                }
            }
            for (Domen subdomen : subdomens) {
                subdomen.join();
            }
        } catch (IOException | InterruptedException e) {
            System.out.println("url: " + url.toString());
            e.printStackTrace();
        }
    }

    private static List<String> getTags() {
        return DBConnection.getFields();
    }
}
