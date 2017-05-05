package database;

import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {
    private static final URL databaseURL;
    private static Database ourInstance;
    static {
        databaseURL = Database.class.getResource("database.db");
        try {
            ourInstance = new Database();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Connection conn;

    public static Database getInstance() {
        return ourInstance;
    }

    public Connection getConn() {
        return conn;
    }

    private Database() throws SQLException {
        conn = connect();
    }

    private Connection connect() throws SQLException {
        // SQLite connection string
        String url = "jdbc:sqlite:" + databaseURL.toExternalForm();
        return DriverManager.getConnection(url);
    }

}
