package ru.nedovizin;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class SeacherBot {
    public static void main(String[] args) throws IOException, SQLException {
        String text = "самый популярный файл Обменник";
        List<String> lemmas = MyLematizator.getWordsBaseForms(text);
        lemmas = DBConnection.getSortedLemmasByFrequency(lemmas);
        var relatives = DBConnection.getRelativeTable(lemmas);
        System.out.println("Запрос: " + text);
        for (Relative relative : relatives) {
            System.out.println("-----------------------------");
            System.out.println("uri: " + relative.getUri());
            System.out.println("title: " + relative.getTitle());
            System.out.println("snippet: " + relative.getSnippet());
            System.out.println("relevance: " + relative.getRelative());
        }
    }
}
