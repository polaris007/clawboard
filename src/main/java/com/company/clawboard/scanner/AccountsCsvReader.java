package com.company.clawboard.scanner;

import com.company.clawboard.entity.DashboardEmployee;
import com.company.clawboard.mapper.EmployeeMapper;
import com.company.clawboard.mapper.OpenclawInstanceMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Reads employee information from accounts.csv file or database.
 * Maps SHA512 hash of employee_id to employee details.
 */
@Slf4j
@Component
public class AccountsCsvReader {

    private final OpenclawInstanceMapper openclawInstanceMapper;
    private final EmployeeMapper employeeMapper;
    private final ObjectMapper objectMapper;

    @Data
    public static class EmployeeInfo {
        private String name;
        private String employeeId;
        private String department;
        private String sha512Hash;
    }

    private Map<String, EmployeeInfo> employeeMap = new HashMap<>();

    public AccountsCsvReader(OpenclawInstanceMapper openclawInstanceMapper, EmployeeMapper employeeMapper) {
        this.openclawInstanceMapper = openclawInstanceMapper;
        this.employeeMapper = employeeMapper;
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Load employee mapping from accounts.csv
     * @param csvPath Path to accounts.csv file
     * 
     * @deprecated This method is kept for backward compatibility. 
     * Use loadFromDatabase() instead for production.
     */
    public void loadFromCsv(Path csvPath) {
        if (csvPath == null || !csvPath.toFile().exists()) {
            log.warn("accounts.csv not found at: {}", csvPath);
            return;
        }

        log.info("Loading employee mapping from: {}", csvPath);
        int count = 0;

        try (BufferedReader reader = new BufferedReader(
                new FileReader(csvPath.toFile(), Charset.forName("GBK")))) {

            // Skip header line
            String header = reader.readLine();
            if (header == null) {
                log.warn("accounts.csv is empty");
                return;
            }

            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                String[] parts = line.split(",", -1);
                if (parts.length < 4) {
                    log.debug("Skipping invalid line: {}", line);
                    continue;
                }

                // CSV format: 序号,姓名,工号,部门,机构号
                String employeeId = parts[2].trim();
                String name = parts[1].trim();
                String department = parts[3].trim();

                if (employeeId.isEmpty()) {
                    continue;
                }

                // Calculate SHA512 hash of employee_id
                String hash = calculateSha512(employeeId);
                if (hash == null) {
                    log.warn("Failed to calculate hash for employee: {}", employeeId);
                    continue;
                }

                EmployeeInfo info = new EmployeeInfo();
                info.setEmployeeId(employeeId);
                info.setName(name);
                info.setDepartment(department);
                info.setSha512Hash(hash);

                employeeMap.put(hash, info);
                
                // Save to database
                try {
                    DashboardEmployee employee = new DashboardEmployee();
                    employee.setEmployeeId(employeeId);
                    employee.setEmployeeName(name);
                    employee.setTeamName(department);
                    employee.setIsActive(1);
                    employeeMapper.upsert(employee);
                } catch (Exception e) {
                    log.debug("Failed to save employee to database: {}", e.getMessage());
                }
                
                count++;
            }

            log.info("Successfully loaded {} employee mappings from CSV", count);

        } catch (IOException e) {
            log.error("Failed to read accounts.csv: {}", csvPath, e);
        }
    }

    /**
     * Load employee mapping from openclaw_instances table
     * uid column is employeeId
     * userName and orgCode are extracted from user_config_json
     */
    public void loadFromDatabase() {
        log.info("Loading employee mapping from database");
        int count = 0;

        try {
            List<Map<String, Object>> instances = openclawInstanceMapper.selectRunningInstances();
            log.info("Found {} running instances in database", instances.size());

            for (Map<String, Object> instance : instances) {
                String uid = (String) instance.get("uid");
                String userConfigJson = (String) instance.get("user_config_json");

                if (uid == null || uid.isEmpty() || userConfigJson == null) {
                    log.debug("Skipping instance with null uid or user_config_json: {}", uid);
                    continue;
                }

                // Parse user_config_json to get userName and orgCode
                JsonNode userConfig = objectMapper.readTree(userConfigJson);
                String name = userConfig.path("userName").asText("");
                String department = userConfig.path("orgCode").asText("");

                if (name.isEmpty()) {
                    log.debug("Skipping instance with empty userName: {}", uid);
                    continue;
                }

                // Calculate SHA512 hash of employee_id (uid)
                String hash = calculateSha512(uid);
                if (hash == null) {
                    log.warn("Failed to calculate hash for employee: {}", uid);
                    continue;
                }

                EmployeeInfo info = new EmployeeInfo();
                info.setEmployeeId(uid);
                info.setName(name);
                info.setDepartment(department);
                info.setSha512Hash(hash);

                employeeMap.put(hash, info);
                
                // Save to database
                try {
                    DashboardEmployee employee = new DashboardEmployee();
                    employee.setEmployeeId(uid);
                    employee.setEmployeeName(name);
                    employee.setTeamName(department);
                    employee.setIsActive(1);
                    employeeMapper.upsert(employee);
                } catch (Exception e) {
                    log.debug("Failed to save employee to database: {}", e.getMessage());
                }
                
                count++;
            }

            log.info("Successfully loaded {} employee mappings from database", count);

        } catch (Exception e) {
            log.error("Failed to load employee mapping from database", e);
        }
    }

    /**
     * Get employee info by SHA512 hash
     * @param sha512Hash SHA512 hash of employee_id
     * @return EmployeeInfo or null if not found
     */
    public EmployeeInfo getEmployeeByHash(String sha512Hash) {
        return employeeMap.get(sha512Hash);
    }

    /**
     * Extract employee info from file path by finding the hash in the path
     * @param filePath Path to transcript file
     * @return EmployeeInfo or null if not found
     */
    public EmployeeInfo extractEmployeeFromPath(Path filePath) {
        String pathStr = filePath.toString().replace('\\', '/');
        String[] parts = pathStr.split("/");

        // Find "agents" directory and get the previous part (SHA512 hash)
        for (int i = 0; i < parts.length; i++) {
            if ("agents".equals(parts[i]) && i > 0) {
                String hash = parts[i - 1];
                return getEmployeeByHash(hash);
            }
        }

        return null;
    }

    /**
     * Calculate SHA512 hash of a string
     * @param input Input string
     * @return Hex-encoded SHA512 hash or null if error
     */
    private String calculateSha512(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-512");
            byte[] hashBytes = digest.digest(input.getBytes("UTF-8"));
            return bytesToHex(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            log.error("SHA-512 algorithm not available", e);
            return null;
        } catch (Exception e) {
            log.error("Failed to calculate SHA-512 hash", e);
            return null;
        }
    }

    /**
     * Convert byte array to hex string
     */
    private String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    /**
     * Get total number of loaded employees
     */
    public int getEmployeeCount() {
        return employeeMap.size();
    }
    
    /**
     * Get all loaded employees
     * @return Collection of EmployeeInfo objects
     */
    public java.util.Collection<EmployeeInfo> getAllEmployees() {
        return employeeMap.values();
    }
}