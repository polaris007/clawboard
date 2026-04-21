import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class TestResetDatabase {
    public static void main(String[] args) throws Exception {
        // Test with wrong confirm code
        testWrongConfirmCode();

        // Test with correct confirm code (20260421)
        testCorrectConfirmCode();
    }

    private static void testWrongConfirmCode() throws Exception {
        URL url = new URL("http://localhost:8080/api/v1/admin/reset-database?confirmCode=20260420");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");

        int responseCode = conn.getResponseCode();
        System.out.println("Wrong confirm code - Response Code: " + responseCode);

        try (Scanner scanner = new Scanner(conn.getInputStream())) {
            System.out.println("Response Content:");
            while (scanner.hasNextLine()) {
                System.out.println(scanner.nextLine());
            }
        }
        System.out.println();
    }

    private static void testCorrectConfirmCode() throws Exception {
        URL url = new URL("http://localhost:8080/api/v1/admin/reset-database?confirmCode=20260421");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");

        int responseCode = conn.getResponseCode();
        System.out.println("Correct confirm code - Response Code: " + responseCode);

        try (Scanner scanner = new Scanner(conn.getInputStream())) {
            System.out.println("Response Content:");
            while (scanner.hasNextLine()) {
                System.out.println(scanner.nextLine());
            }
        }
        System.out.println();
    }
}
