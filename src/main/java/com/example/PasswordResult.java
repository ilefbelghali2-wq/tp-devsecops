package com.example.democyber;

import java.util.List;

public record PasswordResult(String strength, int score, List<String> missing) { }