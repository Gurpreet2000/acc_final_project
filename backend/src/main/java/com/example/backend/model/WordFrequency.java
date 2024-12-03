package com.example.backend.model;

import java.util.List;
import java.util.Map.Entry;

public record WordFrequency(List<Entry<String, Integer>> list) {

}