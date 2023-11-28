import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLConnect {

    public static void main(String[] args) {
        String url = "jdbc:mysql://127.0.0.1:3306/sys";
        String user = "root";
        String password = "password";

        try {
            // Uncomment the following line if using an older JDBC driver
            // Class.forName("com.mysql.cj.jdbc.Driver");

            Connection conn = DriverManager.getConnection(url, user, password);
            if (conn != null) {
                System.out.println("Connected to the database successfully!");
            }
        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
            e.printStackTrace(); // For more detailed error information
        }
    }
}
