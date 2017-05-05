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
        System.out.println(databaseURL == null ? "null" : "not null");
        String url = "jdbc:sqlite:" + databaseURL.toExternalForm(); //C://sqlite/db/test.db";
        Connection conn = null;
        conn = DriverManager.getConnection(url);
        return conn;
    }

}
