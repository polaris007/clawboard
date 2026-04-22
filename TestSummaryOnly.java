
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class TestSummaryOnly {
    public static void main(String[] args) throws Exception {
        testSummary();
    }
    
    private static void testSummary() throws Exception {
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
        System.out.println("/api/v1/dashboard/summary Response Code: " + responseCode);
        
        try (Scanner scanner = new Scanner(conn.getInputStream())) {
            System.out.println("Response Content:");
            while (scanner.hasNextLine()) {
                System.out.println(scanner.nextLine());
            }
        }
        System.out.println();
    }
}
