
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class TestFullFlow {
    public static void main(String[] args) throws Exception {
        System.out.println("=== Step 1: Reset Database ===");
        testResetDatabase();
        
        System.out.println("\n=== Step 2: Trigger Scan ===");
        testTriggerScan();
        
        System.out.println("\n=== Step 3: Wait for scan completion (30 seconds)... ===");
        Thread.sleep(30000);
        
        System.out.println("\n=== Step 4: Get Stats ===");
        testGetStats();
        
        System.out.println("\n=== Step 5: Get Dashboard Summary ===");
        testDashboardSummary();
    }
    
    private static void testResetDatabase() throws Exception {
        URL url = new URL("http://localhost:8080/api/v1/admin/reset-database?confirmCode=20260422");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        
        int responseCode = conn.getResponseCode();
        System.out.println("Response Code: " + responseCode);
        
        try (Scanner scanner = new Scanner(conn.getInputStream())) {
            System.out.println("Response Content:");
            while (scanner.hasNextLine()) {
                System.out.println(scanner.nextLine());
            }
        }
    }
    
    private static void testTriggerScan() throws Exception {
        URL url = new URL("http://localhost:8080/api/v1/scan/trigger");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        
        int responseCode = conn.getResponseCode();
        System.out.println("Response Code: " + responseCode);
        
        try (Scanner scanner = new Scanner(conn.getInputStream())) {
            System.out.println("Response Content:");
            while (scanner.hasNextLine()) {
                System.out.println(scanner.nextLine());
            }
        }
    }
    
    private static void testGetStats() throws Exception {
        URL url = new URL("http://localhost:8080/api/v1/admin/stats");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        
        int responseCode = conn.getResponseCode();
        System.out.println("Response Code: " + responseCode);
        
        try (Scanner scanner = new Scanner(conn.getInputStream())) {
            System.out.println("Response Content:");
            while (scanner.hasNextLine()) {
                System.out.println(scanner.nextLine());
            }
        }
    }
    
    private static void testDashboardSummary() throws Exception {
        URL url = new URL("http://localhost:8080/api/v1/dashboard/summary");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);
        
        String json = "{}";
        try (OutputStream os = conn.getOutputStream()) {
            os.write(json.getBytes());
        }
        
        int responseCode = conn.getResponseCode();
        System.out.println("Response Code: " + responseCode);
        
        try (Scanner scanner = new Scanner(conn.getInputStream())) {
            System.out.println("Response Content:");
            while (scanner.hasNextLine()) {
                System.out.println(scanner.nextLine());
            }
        }
    }
}
