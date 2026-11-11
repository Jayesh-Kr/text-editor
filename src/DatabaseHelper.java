import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * DatabaseHelper handles SQLite JDBC database operations.
 * Demonstrates:
 * - Unit 4: JDBC connectivity with SQLite, Table Creation, Insert & Retrieve operations
 * - Unit 4: Collections (ArrayList, List, accessing via Iterator)
 * - Unit 2: Exception Handling (try-catch, SQLException)
 */
public class DatabaseHelper {

    private static final String DB_URL = "jdbc:sqlite:texteditor.db";
    private static boolean dbAvailable = true;

    static {
        // Initialize table if SQLite JDBC driver is available
        try {
            Class.forName("org.sqlite.JDBC");
            initDatabase();
        } catch (ClassNotFoundException e) {
            // Driver not in classpath; gracefully continue without crashing editor
            dbAvailable = false;
            System.out.println("Note: SQLite JDBC driver not detected. Database logging will be bypassed.");
        } catch (Exception e) {
            dbAvailable = false;
            System.out.println("Database initialization skipped: " + e.getMessage());
        }
    }

    /**
     * Creates the save_logs table if it doesn't already exist.
     */
    private static void initDatabase() {
        String sql = "CREATE TABLE IF NOT EXISTS save_logs ("
                   + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                   + "filename TEXT NOT NULL, "
                   + "saved_at TEXT NOT NULL"
                   + ");";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (Exception e) {
            dbAvailable = false;
            System.out.println("Could not create SQLite table: " + e.getMessage());
        }
    }

    /**
     * Inserts a record whenever a file is saved or auto-saved.
     * 
     * @param fileName The name of the file being saved.
     */
    public static void logSave(String fileName) {
        if (!dbAvailable) return;

        String sql = "INSERT INTO save_logs(filename, saved_at) VALUES(?, ?)";
        String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, fileName);
            pstmt.setString(2, timeStamp);
            pstmt.executeUpdate();
        } catch (Exception e) {
            System.out.println("Database log error: " + e.getMessage());
        }
    }

    /**
     * Retrieves recent save records using an ArrayList and an Iterator.
     * Demonstrates Unit 4 Collections & Iterator.
     * 
     * @return List of recent save log strings.
     */
    public static List<String> getRecentSaveLogs() {
        List<String> logs = new ArrayList<>();
        if (!dbAvailable) return logs;

        String sql = "SELECT filename, saved_at FROM save_logs ORDER BY id DESC LIMIT 5";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                logs.add(rs.getString("filename") + " at " + rs.getString("saved_at"));
            }
        } catch (Exception e) {
            System.out.println("Database fetch error: " + e.getMessage());
        }

        // Example of accessing the collection via an Iterator (Unit 4)
        Iterator<String> iterator = logs.iterator();
        while (iterator.hasNext()) {
            // Accessible via iterator
            iterator.next();
        }

        return logs;
    }
}

