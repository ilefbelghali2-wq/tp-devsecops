package com.example.democyber;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/password")
public class PasswordController {

    private static final Set<String> COMMON = Set.of(
            "password", "123456", "12345678", "qwerty", "azerty",
            "admin", "letmein", "welcome", "motdepasse", "iloveyou");

    private final MeterRegistry registry;

    public PasswordController(MeterRegistry registry) {
        this.registry = registry;
    }

    @PostMapping("/check")
    public PasswordResult check(@RequestBody Map<String, String> body) {
        String pwd = body.getOrDefault("password", "");
        List<String> missing = new ArrayList<>();
        int score = 0;

        if (pwd.length() >= 12) {
            score += 30;
        } else if (pwd.length() >= 8) {
            score += 15;
            missing.add("Utiliser au moins 12 caractères");
        } else {
            missing.add("Utiliser au moins 8 caractères (idéalement 12)");
        }

        if (pwd.chars().anyMatch(Character::isLowerCase)) {
            score += 15;
        } else {
            missing.add("Ajouter une minuscule");
        }
        if (pwd.chars().anyMatch(Character::isUpperCase)) {
            score += 15;
        } else {
            missing.add("Ajouter une majuscule");
        }
        if (pwd.chars().anyMatch(Character::isDigit)) {
            score += 15;
        } else {
            missing.add("Ajouter un chiffre");
        }
        if (pwd.chars().anyMatch(c -> !Character.isLetterOrDigit(c))) {
            score += 25;
        } else {
            missing.add("Ajouter un caractère spécial (!@#$...)");
        }

        if (COMMON.contains(pwd.toLowerCase())) {
            score = Math.min(score, 10);
            missing.add("Mot de passe trop courant, à éviter absolument");
        }

        String strength = score >= 80 ? "FORT" : score >= 50 ? "MOYEN" : "FAIBLE";

        registry.counter("password.checks", "strength", strength.toLowerCase()).increment();

        return new PasswordResult(strength, score, missing);
    }
}