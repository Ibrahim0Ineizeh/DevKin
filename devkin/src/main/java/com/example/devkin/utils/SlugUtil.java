package com.example.devkin.utils;

import com.example.devkin.repositories.ProjectRepository;
import org.springframework.stereotype.Component;

import java.text.Normalizer;
import java.util.Locale;
import java.util.UUID;

@Component
public class SlugUtil {

    private final ProjectRepository projectRepository;

    public SlugUtil(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    public static String toSlug(String input) {
        String nonAccent = Normalizer.normalize(input, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        String slug = nonAccent.toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "-")  // Replace non-alphanumeric with hyphen
                .replaceAll("^-|-$", "");       // Remove leading or trailing hyphens
        return slug;
    }

    public String generateUniqueSlug(String projectName) {
        String slug = toSlug(projectName);
        String uniqueSlug = slug;
        String uniqueId;

        while (projectRepository.existsBySlug(uniqueSlug)) { // Check if slug exists in the database
            uniqueId = UUID.randomUUID().toString().substring(0, 6); // Generate new short random string
            uniqueSlug = slug + "-" + uniqueId;
        }

        return uniqueSlug;
    }
}
