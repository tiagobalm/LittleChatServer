package database.users;

import database.Database;

import java.sql.*;

public class UserRequests {
    private static Connection getConn() {
        return Database.getInstance().getConn();
    }

    public static boolean validateUser(String username, String password) {
        String sql = "SELECT password FROM warehouses WHERE username = ?";

        try (Connection conn = getConn();
             PreparedStatement pstmt  = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            ResultSet rs  = pstmt.executeQuery();

            // loop through the result set
            while (rs.next()) {
                System.out.println(rs.getString("password"));
                if( rs.getString("password").equals(password) )
                    return true;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return false;
    }
}
