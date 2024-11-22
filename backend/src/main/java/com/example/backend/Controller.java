package com.example.backend;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import jakarta.annotation.PostConstruct;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.model.AutoComplete;
import com.example.backend.model.Greeting;
import com.example.backend.model.SearchQuery;
import com.example.backend.services.Search;
import com.example.backend.services.WebCrawler;

@RestController
public class Controller {
    private static final Search search = new Search();

    @PostConstruct
    public void init() {
        // (new WebCrawler()).init();
        search.buildTrie();
        // inverted index
    }

    private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();

    @GetMapping("/greeting")
    public Greeting greeting(@RequestParam(value = "name", defaultValue = "World") String name) {
        return new Greeting(counter.incrementAndGet(), String.format(template, name));
    }

    @GetMapping("/search")
    public SearchQuery search(@RequestParam(value = "q", defaultValue = "") String query) {
        System.out.println("Query: " + query);
        List<Map<String, Object>> list = search.convertToJson(search.invertedIndex.search(query));

        System.out.println(list);
        return new SearchQuery(list);
    }

    @GetMapping("/auto_complete")
    public AutoComplete autoComplete(@RequestParam(value = "q", defaultValue = "") String query) {
        System.out.println("Query: " + query);
        List<String> list = search.invertedIndex.autocomplete(query);
        System.out.println(list);
        return new AutoComplete(list);
    }

}