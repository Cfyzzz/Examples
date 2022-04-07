package ru.nedovizin;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class Relative {
    private String uri;
    private String content;
    private float relative;
    private String title;
    private String snippet;

    public Relative(String uri, String content, float relative) {
        this.uri = uri;
        this.content = content;
        this.relative = relative;
        title = parseTitle();
        snippet = parseSnippet();
    }

    public String getUri() {
        return uri;
    }

    public String getContent() {
        return content;
    }

    public float getRelative() {
        return relative;
    }

    public String getTitle() {
        return title;
    }

    public String getSnippet() {
        return snippet;
    }

    private String parseTitle() {
        Document doc = Jsoup.parse(getContent());
        return doc.getElementsByTag("title").get(0).text();
    }

    private String parseSnippet() {
        Document doc = Jsoup.parse(getContent());
        // TODO: 19.03.2022 Выделить фрагмент с леммой
        return "";
    }
}
