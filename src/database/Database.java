package database;

import org.jetbrains.annotations.Contract;

import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
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

    public static String getSecurePassword(String passwordToHash, byte[] salt)
    {
        String generatedPassword = null;
        try {
            // Create MessageDigest instance for MD5
            MessageDigest md = MessageDigest.getInstance("MD5");
            //Add password bytes to digest
            md.update(salt);
            //Get the hash's bytes
            byte[] bytes = md.digest(passwordToHash.getBytes());
            //This bytes[] has bytes in decimal format;
            //Convert it to hexadecimal format
            StringBuilder sb = new StringBuilder();
            for(int i=0; i< bytes.length ;i++)
            {
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            //Get complete hashed password in hex format
            generatedPassword = sb.toString();
        }
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return generatedPassword;
    }

    //Add salt
    public static byte[] getSalt() throws NoSuchAlgorithmException, NoSuchProviderException
    {
        //Always use a SecureRandom generator
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG", "SUN");
        //Create array for salt
        byte[] salt = new byte[16];
        //Get a random salt
        sr.nextBytes(salt);
        //return salt
        return salt;
    }
}
