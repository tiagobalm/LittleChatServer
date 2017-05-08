package database;

import org.jetbrains.annotations.Contract;

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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Connection conn;

    @Contract(pure = true)
    public static Database getInstance() {
        return ourInstance;
    }

    public Connection getConn() throws SQLException {
        if( conn.isClosed() )
            conn = connect();
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
