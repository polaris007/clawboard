package com.company.clawboard.scanner;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Recursively scans directories for transcript JSONL files.
 */
@Slf4j
@Component
public class TranscriptFileScanner {

    /**
     * Scan directory recursively for all .jsonl files
     * @param rootDir Root directory to scan
     * @return List of JSONL file paths
     */
    public List<Path> scanForJsonlFiles(Path rootDir) {
        List<Path> jsonlFiles = new ArrayList<>();
        
        if (!Files.exists(rootDir)) {
            log.warn("Directory does not exist: {}", rootDir);
            return jsonlFiles;
        }

        if (!Files.isDirectory(rootDir)) {
            log.warn("Path is not a directory: {}", rootDir);
            return jsonlFiles;
        }

        log.info("Scanning directory for JSONL files: {}", rootDir);
        AtomicInteger dirCount = new AtomicInteger(0);
        AtomicInteger fileCount = new AtomicInteger(0);
        long startTime = System.currentTimeMillis();

        try {
            Files.walkFileTree(rootDir, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
                    int count = dirCount.incrementAndGet();
                    if (count % 100 == 0) {
                        long elapsed = System.currentTimeMillis() - startTime;
                        log.debug("Scanned {} directories, found {} JSONL files... ({}ms)", 
                                count, jsonlFiles.size(), elapsed);
                    }
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                    fileCount.incrementAndGet();
                    String fileName = file.getFileName().toString();
                    
                    // Scan ALL files containing ".jsonl" in filename (no filtering)
                    // Duplicate data will be handled by database batchInsertIgnore
                    if (fileName.contains(".jsonl")) {
                        jsonlFiles.add(file);
                    }
                    
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc) {
                    log.warn("Failed to access file: {}", file, exc);
                    return FileVisitResult.CONTINUE;
                }
            });

            long elapsed = System.currentTimeMillis() - startTime;
            log.info("Scan complete: traversed {} directories, {} files, found {} JSONL files ({}ms)",
                    dirCount.get(), fileCount.get(), jsonlFiles.size(), elapsed);

        } catch (IOException e) {
            log.error("Failed to scan directory: {}", rootDir, e);
        }

        return jsonlFiles;
    }

    /**
     * Filter JSONL files by creation/modification time range
     * @param files List of files to filter
     * @param startTimeMs Start time in milliseconds (null for no lower bound)
     * @param endTimeMs End time in milliseconds (null for no upper bound)
     * @return Filtered list of files
     */
    public List<Path> filterByTimeRange(List<Path> files, Long startTimeMs, Long endTimeMs) {
        if (startTimeMs == null && endTimeMs == null) {
            return files;
        }

        log.info("Filtering {} files by time range: {} - {}", 
                files.size(), 
                startTimeMs != null ? new java.util.Date(startTimeMs) : "null",
                endTimeMs != null ? new java.util.Date(endTimeMs) : "null");

        List<Path> filtered = new ArrayList<>();
        int skipped = 0;

        for (Path file : files) {
            try {
                BasicFileAttributes attrs = Files.readAttributes(file, BasicFileAttributes.class);
                long fileTime = attrs.creationTime().toMillis();

                if (startTimeMs != null && fileTime < startTimeMs) {
                    skipped++;
                    continue;
                }
                if (endTimeMs != null && fileTime > endTimeMs) {
                    skipped++;
                    continue;
                }

                filtered.add(file);
            } catch (IOException e) {
                log.warn("Failed to read file attributes: {}", file, e);
                skipped++;
            }
        }

        log.info("Filtered result: {} files kept, {} files skipped", filtered.size(), skipped);
        return filtered;
    }
}
