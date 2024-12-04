package com.example.backend.services;

import java.io.IOException;

import com.example.backend.scrapper.Amazon;
import com.example.backend.scrapper.Dropbox;
import com.example.backend.scrapper.GoogleDrive;
import com.example.backend.scrapper.Icloud;
import com.example.backend.scrapper.OneDrive;

public class WebCrawler {
    public static void main(String[] args) {
        WebCrawler crawl = new WebCrawler();
        crawl.init();
    }

    public void init() {
        try {
            String directory = "./data";
            (new GoogleDrive()).init(directory);
            (new Dropbox()).init(directory);
            (new Icloud()).init(directory);
            (new OneDrive()).init(directory);
            (new Amazon()).init(directory);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
