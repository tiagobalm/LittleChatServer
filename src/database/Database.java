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

/**
 * This class creates the database's connections
 */
class Database {
    /**
     * This varaiable represents the database's URL
     */
    private static final URL databaseURL;

    /**
     * This variable is a instance of the Database class
     */
    private static Database ourInstance;

    static {
        databaseURL = Database.class.getResource("database.db");
        try {
            ourInstance = new Database();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Database's connection
     */
    private Connection conn;

    /**
     * This is the database's constructor
     *
     * @throws SQLException This is an exception that provides information on a database access error or other errors
     */
    private Database() throws SQLException {
        conn = connect();
    }

    @Contract(pure = true)
    static Database getInstance() {
        return ourInstance;
    }


    /**
     * This function creates a secure password with MD5 and salt
     *
     * @param passwordToHash Password that will be hashed
     * @param salt           Salt to be used on the hashed password
     * @return The hashed password
     */
    static String getSecurePassword(String passwordToHash, byte[] salt) {
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
            for (byte aByte : bytes)
                sb.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));
            //Get complete hashed password in hex format
            generatedPassword = sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return generatedPassword;
    }

    /**
     * This function creates the salt that will be used on the hashed password
     *
     * @return The salt created to be used on the hashed password
     * @throws NoSuchAlgorithmException This exception is thrown when a particular cryptographic algorithm is requested but is not available in the environment
     * @throws NoSuchProviderException  This exception is thrown when a particular security provider is requested but is not available in the environment
     */
    static byte[] getSalt() throws NoSuchAlgorithmException, NoSuchProviderException {
        //Always use a SecureRandom generator
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG", "SUN");
        //Create array for salt
        byte[] salt = new byte[16];
        //Get a random salt
        sr.nextBytes(salt);
        //return salt
        return salt;
    }

    Connection getConn() throws SQLException {
        if (conn.isClosed())
            conn = connect();
        return conn;
    }

    /**
     * This function creates the connection between the server and the database
     *
     * @return The connecion creates
     * @throws SQLException This is an exception that provides information on a database access error or other errors.
     */
    private Connection connect() throws SQLException {
        // SQLite connection string
        String url = "jdbc:sqlite:" + databaseURL.getPath();
        return DriverManager.getConnection(url);
    }
}
