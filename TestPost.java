import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class TestPost {
    public static void main(String[] args) throws Exception {
        // Test GET interfaces
        testSkillOptions();
        testGlobalStats();
        testTrace();
        
        // Test POST interfaces
        testSummary();
        testTrend();
        testUserSummary();
        testSearchTurns();
    }
    
    private static void testSkillOptions() throws Exception {
        URL url = new URL("http://localhost:8080/api/v1/skills/options");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        
        int responseCode = conn.getResponseCode();
        System.out.println("/api/v1/skills/options Response Code: " + responseCode);
        
        try (Scanner scanner = new Scanner(conn.getInputStream())) {
            System.out.println("Response Content:");
            while (scanner.hasNextLine()) {
                System.out.println(scanner.nextLine());
            }
        }
        System.out.println();
    }
    
    private static void testGlobalStats() throws Exception {
        URL url = new URL("http://localhost:8080/api/v1/dashboard/global-stats");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        
        int responseCode = conn.getResponseCode();
        System.out.println("/api/v1/dashboard/global-stats Response Code: " + responseCode);
        
        try (Scanner scanner = new Scanner(conn.getInputStream())) {
            System.out.println("Response Content:");
            while (scanner.hasNextLine()) {
                System.out.println(scanner.nextLine());
            }
        }
        System.out.println();
    }
    
    private static void testTrace() throws Exception {
        URL url = new URL("http://localhost:8080/api/v1/turns/1/trace");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        
        int responseCode = conn.getResponseCode();
        System.out.println("/api/v1/turns/1/trace Response Code: " + responseCode);
        
        try (Scanner scanner = new Scanner(conn.getInputStream())) {
            System.out.println("Response Content:");
            while (scanner.hasNextLine()) {
                System.out.println(scanner.nextLine());
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
        System.out.println();
    }
    
    private static void testSummary() throws Exception {
        URL url = new URL("http://localhost:8080/api/v1/dashboard/summary");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);
        
        String json = "{\"startTime\": 1680000000000, \"endTime\": 1680086400000}";
        try (OutputStream os = conn.getOutputStream()) {
            os.write(json.getBytes());
        }
        
        int responseCode = conn.getResponseCode();
        System.out.println("/api/v1/dashboard/summary Response Code: " + responseCode);
        
        try (Scanner scanner = new Scanner(conn.getInputStream())) {
            System.out.println("Response Content:");
            while (scanner.hasNextLine()) {
                System.out.println(scanner.nextLine());
            }
        }
        System.out.println();
    }
    
    private static void testTrend() throws Exception {
        URL url = new URL("http://localhost:8080/api/v1/dashboard/trend");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);
        
        String json = "{\"startTime\": 1680000000000, \"endTime\": 1680086400000}";
        try (OutputStream os = conn.getOutputStream()) {
            os.write(json.getBytes());
        }
        
        int responseCode = conn.getResponseCode();
        System.out.println("/api/v1/dashboard/trend Response Code: " + responseCode);
        
        try (Scanner scanner = new Scanner(conn.getInputStream())) {
            System.out.println("Response Content:");
            while (scanner.hasNextLine()) {
                System.out.println(scanner.nextLine());
            }
        }
        System.out.println();
    }
    
    private static void testUserSummary() throws Exception {
        URL url = new URL("http://localhost:8080/api/v1/dashboard/usersummary");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);
        
        String json = "{\"page\": 1, \"pageSize\": 10}";
        try (OutputStream os = conn.getOutputStream()) {
            os.write(json.getBytes());
        }
        
        int responseCode = conn.getResponseCode();
        System.out.println("/api/v1/dashboard/usersummary Response Code: " + responseCode);
        
        try (Scanner scanner = new Scanner(conn.getInputStream())) {
            System.out.println("Response Content:");
            while (scanner.hasNextLine()) {
                System.out.println(scanner.nextLine());
            }
        }
        System.out.println();
    }
    
    private static void testSearchTurns() throws Exception {
        URL url = new URL("http://localhost:8080/api/v1/turns/search");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);
        
        String json = "{\"page\": 1, \"pageSize\": 10}";
        try (OutputStream os = conn.getOutputStream()) {
            os.write(json.getBytes());
        }
        
        int responseCode = conn.getResponseCode();
        System.out.println("/api/v1/turns/search Response Code: " + responseCode);
        
        try (Scanner scanner = new Scanner(conn.getInputStream())) {
            System.out.println("Response Content:");
            while (scanner.hasNextLine()) {
                System.out.println(scanner.nextLine());
            }
        }
        System.out.println();
    }
}