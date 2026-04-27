package com.company.clawboard.scanner;

import com.company.clawboard.config.ClawboardProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserScanner {

    private final ClawboardProperties properties;
    private final AccountsCsvReader accountsReader;

    /**
     * Scan for user directories (hash-based directory names)
     * 
     * Directory structure:
     * {basePath}/{sha512_hash}/agents/{agentName}/sessions/*.jsonl
     * 
     * The hash is SHA512 of employee ID from accounts.csv
     */
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

        // Build reverse mapping: sha512_hash -> employee_id
        Map<String, String> hashToEmployeeId = buildHashMapping();
        
        File[] hashDirs = baseDir.listFiles(File::isDirectory);
        if (hashDirs != null) {
            for (File hashDir : hashDirs) {
                String dirName = hashDir.getName();
                
                // Check if this directory contains agents subdirectory (use configured openclawDir)
                Path agentsPath = Path.of(hashDir.getAbsolutePath(), openclawDir);
                if (!agentsPath.toFile().exists()) {
                    continue;  // Skip directories without configured subdirectory
                }
                
                // Try to match hash to employee ID
                String employeeId = resolveEmployeeId(dirName, hashToEmployeeId);
                
                if (employeeId != null && !employeeId.isEmpty()) {
                    users.add(employeeId);
                    log.debug("Found user directory: {} -> employee {}", dirName, employeeId);
                } else {
                    // Use truncated hash with "sha-" prefix as fallback identifier
                    String fallbackId = "sha-" + (dirName.length() > 10 ? dirName.substring(0, 10) : dirName);
                    users.add(fallbackId);
                    log.debug("Found user directory with unknown hash: {} (using fallback ID: {})", dirName, fallbackId);
                }
            }
        }

        log.info("Found {} user directories", users.size());
        return users;
    }
    
    /**
     * Build reverse mapping from SHA512 hash to employee ID
     */
    private Map<String, String> buildHashMapping() {
        Map<String, String> hashToEmployeeId = new HashMap<>();
        
        try {
            var employees = accountsReader.getAllEmployees();
            MessageDigest digest = MessageDigest.getInstance("SHA-512");
            
            for (var employee : employees) {
                String employeeId = employee.getEmployeeId();
                if (employeeId == null || employeeId.isEmpty()) {
                    continue;
                }
                
                byte[] hashBytes = digest.digest(employeeId.getBytes("UTF-8"));
                StringBuilder hexString = new StringBuilder();
                for (byte b : hashBytes) {
                    String hex = Integer.toHexString(0xff & b);
                    if (hex.length() == 1) {
                        hexString.append('0');
                    }
                    hexString.append(hex);
                }
                
                String hashValue = hexString.toString();
                hashToEmployeeId.put(hashValue, employeeId);
            }
            
            log.debug("Built hash mapping for {} employees", hashToEmployeeId.size());
        } catch (Exception e) {
            log.error("Failed to build hash mapping", e);
        }
        
        return hashToEmployeeId;
    }
    
    /**
     * Resolve employee ID from directory name (hash)
     * 
     * @param dirName Directory name (should be SHA512 hash)
     * @param hashToEmployeeId Pre-built hash mapping
     * @return Employee ID or null if not found
     */
    private String resolveEmployeeId(String dirName, Map<String, String> hashToEmployeeId) {
        // Direct match
        if (hashToEmployeeId.containsKey(dirName)) {
            return hashToEmployeeId.get(dirName);
        }
        
        // Try prefix matching (in case hash is truncated or has extra characters)
        for (Map.Entry<String, String> entry : hashToEmployeeId.entrySet()) {
            String hash = entry.getKey();
            // Check if directory name starts with the hash (or vice versa)
            if (dirName.startsWith(hash) || hash.startsWith(dirName)) {
                log.debug("Partial hash match: {} matches {}", dirName, hash);
                return entry.getValue();
            }
        }
        
        return null;
    }
}
