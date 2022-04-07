package ru.nedovizin;

import org.apache.lucene.morphology.LuceneMorphology;
import org.apache.lucene.morphology.russian.RussianLuceneMorphology;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MyLematizator {
    private static LuceneMorphology luceneMorph;

    public synchronized static List<String> getWordsBaseForms(String text) throws IOException {
        luceneMorph = new RussianLuceneMorphology();
        List<String> result = new ArrayList<>();

        Arrays.stream(text.split("\\s+"))
                .map(String::toLowerCase)
                .map(w -> w.replaceAll("[.,!]+", ""))
                .filter(MyLematizator::checkWord)
                .forEach(
                        word -> {
                            List<String> wordBaseForms = luceneMorph.getNormalForms(word);
                            String foundWord = wordBaseForms.get(wordBaseForms.size() - 1);
                            result.add(foundWord);
                        });
        return result;
    }

    private static boolean checkWord(String word) {
        try {
            List<String> wordBaseForms = luceneMorph.getMorphInfo(word);
            String regexp = "(.*\\|.*МЕЖД)|(.*\\|.*СОЮЗ)|(.*\\|.*ПРЕДЛ)|(.*\\|.*ЧАСТ)";
            return !wordBaseForms.get(0).matches(regexp);
        } catch (Exception e) {
        }
        return false;
    }
}
