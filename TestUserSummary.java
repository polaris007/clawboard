import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class TestUserSummary {
    public static void main(String[] args) throws Exception {
        testUserSummary();
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
}