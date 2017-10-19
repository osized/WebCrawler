package com.ayudin;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ForkJoinPool;

public class Main {

    private static final String URL = "https://ru.wikipedia.org/wiki/%D0%97%D0%B0%D0%B3%D0%BB%D0%B0%D0%B2%D0%BD%D0%B0%D1%8F_%D1%81%D1%82%D1%80%D0%B0%D0%BD%D0%B8%D1%86%D0%B0";

    public static void main(String[] args) {
        ConcurrentHashMap<String, Integer> resultHolder = new ConcurrentHashMap<>();
        ConcurrentHashMap <String, Boolean> visited = new ConcurrentHashMap<>();
        visited.put(URL, true);
        ParsePage parsePage = new ParsePage(URL, visited, resultHolder, 4);
        ForkJoinPool pool = new ForkJoinPool(32);
        try {
             pool.submit(parsePage);
        } catch (Exception e){
            e.printStackTrace();
        }
        parsePage.join();
        resultHolder.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Collections.reverseOrder()))
                .limit(100)
                .forEach(System.out::println);
    }

}
