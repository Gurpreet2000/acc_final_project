package com.example.backend.model;

import java.util.List;
import java.util.Map;

public record SearchHistory(List<Map.Entry<String, Integer>> list) {
}