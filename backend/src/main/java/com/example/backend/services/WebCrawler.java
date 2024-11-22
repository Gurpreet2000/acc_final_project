package com.example.backend.services;

import java.io.IOException;

import com.example.backend.scrapper.GoogleDrive;

public class WebCrawler {
    public static void main(String[] args) {
        try {
            (new GoogleDrive()).init();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void init() {
        try {
            (new GoogleDrive()).init();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
