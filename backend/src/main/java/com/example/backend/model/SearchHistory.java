package com.example.backend.model;

import java.util.List;
import java.util.Map.Entry;

public record SearchHistory(List<Entry<String, Integer>> data) {

}