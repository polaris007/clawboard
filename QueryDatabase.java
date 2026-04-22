
import java.sql.*;

public class QueryDatabase {
    public static void main(String[] args) {
        String url = "jdbc:mysql://127.0.0.1:3306/clawboard?useSSL=false&serverTimezone=Asia/Shanghai";
        String user = "clawboard";
        String password = "Clqc@1234";
        
        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            // 1. Check total count in dashboard_conversation_turn
            String countSql = "SELECT COUNT(*) as total FROM dashboard_conversation_turn";
            try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(countSql)) {
                if (rs.next()) {
                    System.out.println("dashboard_conversation_turn total: " + rs.getInt("total"));
                }
            }
            
            // 2. Group by system_turn
            System.out.println("\nGroup by system_turn:");
            String groupSql = "SELECT system_turn, COUNT(*) as count FROM dashboard_conversation_turn GROUP BY system_turn";
            try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(groupSql)) {
                while (rs.next()) {
                    System.out.println("  system_turn=" + rs.getObject("system_turn") + ": " + rs.getInt("count"));
                }
            }
            
            // 3. Query session_summary
            System.out.println("\ndashboard_session_summary:");
            String sessionSql = "SELECT SUM(total_turns) as sum_total_turns, SUM(non_system_turns) as sum_non_system_turns FROM dashboard_session_summary";
            try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sessionSql)) {
                if (rs.next()) {
                    System.out.println("  SUM(total_turns): " + rs.getObject("sum_total_turns"));
                    System.out.println("  SUM(non_system_turns): " + rs.getObject("sum_non_system_turns"));
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
