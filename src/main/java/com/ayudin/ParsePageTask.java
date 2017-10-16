package com.ayudin;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;
import java.util.stream.Collectors;

public class ParsePageTask extends RecursiveTask<Map<String, Integer>> {

    private final String url;
    private final Set<String> visitedPages;

    public ParsePageTask(String url, Set<String> visitedPages) {
        this.url = url;
        this.visitedPages = visitedPages;
    }

    private Map<String, Integer> parsePage() throws ExecutionException, InterruptedException {
        Map<String, Integer> wordsMap = new HashMap<>();
        String body = getPageBody();
        for (String link: getUniqueLinks(body)){
            ForkJoinTask<Map<String, Integer>> nextTask =  new ParsePageTask(link, visitedPages).fork();
            wordsMap =  mergeMaps(wordsMap, nextTask.fork().get());
        }
        return wordsMap;
    }

    private List<String> getUniqueLinks(String body) {
        //jsoup goes here
        List<String> allLinks = Arrays.asList("http://www.ya.ru");
        List<String> uniqueLinks = allLinks.stream()
                .filter(s -> !visitedPages.contains(s))
                .collect(Collectors.toList());

        return uniqueLinks;
    }


    //todo
    private String getPageBody(){
        return "aaa bbb ccc aaaa";
    }

    private Map<String, Integer> mergeMaps(Map<String, Integer> map1, Map<String, Integer> map2){
        return Collections.EMPTY_MAP;
    }

    @Override
    protected Map<String, Integer> compute() {
        return null;
    }
}
