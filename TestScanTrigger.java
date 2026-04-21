import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class TestScanTrigger {
    public static void main(String[] args) throws Exception {
        URL url = new URL("http://localhost:8080/api/v1/scan/trigger");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");

        int responseCode = conn.getResponseCode();
        System.out.println("/api/v1/scan/trigger Response Code: " + responseCode);

        try (Scanner scanner = new Scanner(conn.getInputStream())) {
            System.out.println("Response Content:");
            while (scanner.hasNextLine()) {
                System.out.println(scanner.nextLine());
            }
        }
        System.out.println();
    }
}
