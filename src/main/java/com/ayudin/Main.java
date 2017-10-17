package com.ayudin;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class Main {

    public static void main(String[] args) {
        ConcurrentMap <String, Integer> map = new ConcurrentHashMap<>();
        ConcurrentHashMap<String, Integer> resultHolder = new ConcurrentHashMap<>();
        ParsePage parsePage = new ParsePage("ru.wikipedia.org", new ConcurrentHashMap<>(), resultHolder, 4);
        try {
             parsePage.fork();
        } catch (Exception e){
            e.printStackTrace();
        }
        parsePage.join();
        resultHolder.forEach((k, v) -> System.out.println(k + ": " + v));
    }

}
