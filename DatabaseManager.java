//DatabaseManager.java
import java.sql.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.nio.charset.StandardCharsets;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:events.db";
    
    public static void initializeDatabase() {
        System.out.println("Initializing database...");
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            
            System.out.println("Creating tables...");
            stmt.execute("CREATE TABLE IF NOT EXISTS events (" +
                    "name TEXT PRIMARY KEY, " +
                    "date TEXT, venue TEXT, type TEXT, " +
                    "capacity INTEGER, current_capacity INTEGER, fee REAL)");
            
            stmt.execute("CREATE TABLE IF NOT EXISTS users (" +
                    "userId TEXT PRIMARY KEY, " +
                    "name TEXT, isStudent BOOLEAN)");
            
            stmt.execute("CREATE TABLE IF NOT EXISTS registrations (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "userId TEXT, eventName TEXT, " +
                    "catering BOOLEAN, transport BOOLEAN, " +
                    "groupSize INTEGER, billId TEXT, " +
                    "FOREIGN KEY(userId) REFERENCES users(userId), " +
                    "FOREIGN KEY(eventName) REFERENCES events(name))");
            
            stmt.execute("CREATE TABLE IF NOT EXISTS bills (" +
                    "billId TEXT PRIMARY KEY, " +
                    "billDateTime TEXT, baseFeePerPerson REAL, " +
                    "cateringFeePerPerson REAL, transportFeePerPerson REAL, " +
                    "groupSize INTEGER, discountLabel TEXT, " +
                    "discountAmount REAL, netPayable REAL, " +
                    "baseFee REAL, cateringFee REAL, transportFee REAL, " +
                    "totalBeforeDiscount REAL)");
            
            stmt.execute("CREATE TABLE IF NOT EXISTS admins (" +
                    "username TEXT PRIMARY KEY, " +
                    "password TEXT)");
            
            // Check if admin exists
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM admins WHERE username = 'admin'");
            int count = rs.getInt(1);
            rs.close();
            
            if (count == 0) {
                System.out.println("Creating default admin...");
                String hashedPassword = hashPassword("1234");
                System.out.println("Admin password hash: " + hashedPassword);
                stmt.execute("INSERT INTO admins VALUES ('admin', '" + hashedPassword + "')");
            }
            
            System.out.println("Database initialized successfully");
        } catch (SQLException e) {
            System.err.println("Database initialization failed:");
            e.printStackTrace();
        }
    }
    
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }
    
    public static boolean verifyAdmin(String username, String password) {
        System.out.println("Verifying admin: " + username);
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                 "SELECT password FROM admins WHERE username = ?")) {
            
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                String storedHash = rs.getString("password");
                String inputHash = hashPassword(password);
                
                return storedHash.equals(inputHash);
            }
            System.out.println("User not found: " + username);
            return false;
        } catch (SQLException e) {
            System.err.println("Database error during verification:");
            e.printStackTrace();
            return false;
        }
    }
    
    private static String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedHash = digest.digest(
                password.getBytes(StandardCharsets.UTF_8));
            
            StringBuilder hexString = new StringBuilder();
            for (byte b : encodedHash) {
                String hex = Integer.toHexString(0xff & b);
                if(hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Failed to hash password", e);
        }
    }
}