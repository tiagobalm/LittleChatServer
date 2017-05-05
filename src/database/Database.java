package database;

import javax.xml.crypto.Data;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by vasco on 05-05-2017.
 */
public class Database {
    private static final URL databaseURL = Database.class.getResource("database/database.db");
    private static Database ourInstance;
    static {
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
        String url = "jdbc:sqlite:" + databaseURL.getFile(); //C://sqlite/db/test.db";
        Connection conn = null;
        conn = DriverManager.getConnection(url);
        return conn;
    }

}
