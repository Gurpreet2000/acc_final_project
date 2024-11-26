package com.example.backend.model;

import java.util.*;

public record SearchQuery(List<Map<String, Object>> data, String spellCheck) {
}
