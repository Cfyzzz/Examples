package ru.nedovizin;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.RecursiveAction;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;


public class Domen extends RecursiveAction {
    private URL url;
    private int siteId;

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

    public Domen(String address, int siteId) {
        this(address);
        this.siteId = siteId;
    }

    public static Domen getSite(String address) {
        synchronized (DBConnection.getConnection()) {
            // TODO: 28.03.2022 Второй параметр - это значение name
            DBConnection.addSite(address, address);
            return new Domen(address);
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
        List<String> lemmas;
        List<String> tags;
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
                tags = getTags();
                for (String tag : tags) {
                    Elements tagsText = doc.getElementsByTag(tag);
                    for (Element tagText : tagsText) {
                        String text = tagText.text().toLowerCase(Locale.ROOT);
                        lemmas = MyLematizator.getWordsBaseForms(text);
                        synchronized (DBConnection.getConnection()) {
                            DBConnection.addLemmas(lemmas);
                            DBConnection.addIndexes(lemmas, url.getPath(), tag);
                        }
                    }
                }
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
                    Thread.sleep(5000);
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
