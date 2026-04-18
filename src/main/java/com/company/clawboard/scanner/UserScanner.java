package com.company.clawboard.scanner;

import com.company.clawboard.config.ClawboardProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserScanner {

    private final ClawboardProperties properties;

    public List<String> scanUsers() {
        String basePath = properties.getNas().getBasePath();
        String openclawDir = properties.getNas().getOpenclawDir();
        
        List<String> users = new ArrayList<>();
        File baseDir = new File(basePath);
        
        if (!baseDir.exists() || !baseDir.isDirectory()) {
            log.warn("NAS base path does not exist: {}", basePath);
            return users;
        }

        // If openclawDir is empty, treat basePath as the sessions directory (flat structure)
        if (openclawDir == null || openclawDir.isEmpty()) {
            log.info("Using flat directory structure, treating '{}' as sessions root", basePath);
            users.add("default");  // Use a placeholder username
            return users;
        }

        File[] userDirs = baseDir.listFiles(File::isDirectory);
        if (userDirs != null) {
            for (File userDir : userDirs) {
                Path openclawPath = Path.of(userDir.getAbsolutePath(), openclawDir);
                if (openclawPath.toFile().exists()) {
                    users.add(userDir.getName());
                }
            }
        }

        return users;
    }
}
