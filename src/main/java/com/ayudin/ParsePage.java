package com.ayudin;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.RecursiveAction;
import java.util.stream.Collectors;

public class ParsePage extends RecursiveAction {

    private final String url;
    private final ConcurrentHashMap<String, Boolean> visitedPages;
    private ConcurrentHashMap<String, Integer> wordsMap;
    private final int depth;
    private final int RETRIES = 3;

    public ParsePage(String url, ConcurrentHashMap<String, Boolean> visitedPages, ConcurrentHashMap<String, Integer> wordsMap, int depth) {
        this.url = url;
        this.visitedPages = visitedPages;
        this.wordsMap = wordsMap;
        this.depth = depth;
    }

    private Document load() throws IOException{
        for (int retry = 0; retry < RETRIES; retry++) {
            try {
                return Jsoup.connect(url).get();
            } catch (IOException e) {
                //try again
            }
        }
        throw new IOException("Cannot load:" + url);

    }


    @Override
    protected void compute() {
        try {
            System.out.println("depth: " + depth);
            if (depth == 0) return;
            List<ParsePage> childTasks = new ArrayList<>();

            Document page;
            String text;
            try {
                page = load();
                text = page.text();
            } catch (NullPointerException | IOException ioe){
                System.err.println("cannot load text: " + url);
                return;
            }

            countWords(text);
            for (String link : getUniqueLinks(page)) {
                ParsePage newTask = new ParsePage(link, visitedPages, this.wordsMap, depth - 1);

                newTask.fork();
                childTasks.add(newTask);
            }
            for (ParsePage task : childTasks) {
                task.join();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private List<String> getUniqueLinks(Document doc) {
        Elements links = doc.select("a[href]");
        List<String> internalLinks = new ArrayList<>();
        for (Element link : links) {
            String absolute = link.attr("abs:href");
            try {
                URL newUrl = new URL(absolute);
                URL curUrl = new URL(url);
                if (newUrl.getHost().equals(curUrl.getHost())) {
                    internalLinks.add(absolute);
                }
            } catch (MalformedURLException ex) {
                System.err.println("Malformed URL: " + absolute);
            }
        }

        List<String> uniqueLinks = internalLinks.stream()
                .distinct()
                .filter(link -> {
                    String hostPath = "";
                    try {
                        URL url = new URL(link);
                        hostPath += url.getHost();
                        hostPath += url.getPath();
                    } catch (Exception e){
                        return false;
                    }
                    return visitedPages.putIfAbsent(hostPath, true) == null;  //putIfAbsent returns null if link was not in visitedPages
                    }
                )
                .collect(Collectors.toList());
        return uniqueLinks;
    }

    private void countWords(String text) {
        String[] words = text.split(" ");
        for (String word : words) {
            String uppercase = word.toUpperCase();
            if (uppercase.matches("[A-ZА-Я]+")) {
                wordsMap.putIfAbsent(uppercase, 0);
                wordsMap.merge(uppercase, 1, (oldCount, newCount) -> oldCount + 1);
            }
        }
    }
}

