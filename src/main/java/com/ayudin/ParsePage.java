package com.ayudin;

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

    public ParsePage(String url, ConcurrentHashMap<String, Boolean> visitedPages, ConcurrentHashMap<String, Integer> wordsMap, int depth) {
        this.url = url;
        this.visitedPages = visitedPages;
        this.wordsMap = wordsMap;
        this.depth = depth;
    }


    @Override
    protected void compute() {
        try {
            if (depth <= 0) return;
            List<ParsePage> childTasks = new ArrayList<>();
            String body = getPageBody();
            countWords(body);
            for (String link : getUniqueLinks(body)) {
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

    private List<String> getUniqueLinks(String body) {
        //jsoup goes here
        List<String> allLinks = Arrays.asList("http://www.ya.ru");
        List<String> uniqueLinks = allLinks.stream()
                .distinct()
                .filter(link ->
                        visitedPages.putIfAbsent(link, true) == null  //putIfAbsent returns null if link was not in visitedPages
                )
                .collect(Collectors.toList());
        return uniqueLinks;
    }

    private void countWords(String text) {
        String[] words = text.split(" ");
        for (String word : words) {
            wordsMap.putIfAbsent(word, 0);
            wordsMap.merge(word, 1, (oldCount, newCount) -> oldCount + 1);
        }
    }

    //todo
    private String getPageBody() {
        return "aaa bbb ccc aaaa";
    }
}

