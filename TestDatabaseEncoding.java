import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class TestDatabaseEncoding {
    public static void main(String[] args) {
        try {
            // 加载驱动
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // 连接数据库
            String url = "jdbc:mysql://127.0.0.1:3306/clawboard?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai";
            String username = "clawboard";
            String password = "Clqc@1234";
            Connection conn = DriverManager.getConnection(url, username, password);
            
            // 执行SQL查询表结构
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SHOW CREATE TABLE test_table");
            
            // 打印结果
            while (rs.next()) {
                System.out.println("Table: " + rs.getString(1));
                System.out.println("Create Table: " + rs.getString(2));
            }
            
            // 关闭连接
            rs.close();
            stmt.close();
            conn.close();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
