package com.ayudin;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ForkJoinPool;

public class Main {

    public static void main(String[] args) {
        ConcurrentMap <String, Integer> map = new ConcurrentHashMap<>();
        ConcurrentHashMap<String, Integer> resultHolder = new ConcurrentHashMap<>();
        ParsePage parsePage = new ParsePage("https://shipilev.net/", new ConcurrentHashMap<>(), resultHolder, 10);
        ForkJoinPool pool = new ForkJoinPool();
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
