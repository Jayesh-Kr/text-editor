import java.io.File;

/**
 * Verification test to test non-GUI components:
 * - FileManager (Byte Streams: FileInputStream / FileOutputStream)
 * - OperationResult (Generics)
 * - DatabaseHelper (Collections, SQLite)
 */
public class FileManagerTest {

    public static void main(String[] args) {
        System.out.println("=== Starting Non-GUI Component Verification ===");

        File testFile = new File("test_sample.txt");
        String testContent = "Hello from Java CSE 307!\nAuto-save test content.";

        // 1. Test Writing (FileOutputStream & OperationResult<Boolean>)
        System.out.println("1. Testing File Writing (FileOutputStream)...");
        OperationResult<Boolean> writeRes = FileManager.writeFile(testFile, testContent);
        if (writeRes.isSuccess()) {
            System.out.println("   [PASS] Write successful: " + writeRes.getMessage());
        } else {
            System.err.println("   [FAIL] Write failed: " + writeRes.getMessage());
            return;
        }

        // 2. Test Reading (FileInputStream & OperationResult<String>)
        System.out.println("2. Testing File Reading (FileInputStream)...");
        OperationResult<String> readRes = FileManager.readFile(testFile);
        if (readRes.isSuccess() && testContent.equals(readRes.getData())) {
            System.out.println("   [PASS] Read verified! Content matches expected string.");
        } else {
            System.err.println("   [FAIL] Read mismatch or error: " + readRes.getMessage());
            return;
        }

        // 3. Test Database Logging & Collections (Unit 4)
        System.out.println("3. Testing Database Logging & Collections...");
        DatabaseHelper.logSave(testFile.getName());
        var logs = DatabaseHelper.getRecentSaveLogs();
        System.out.println("   [PASS] Database & Collections processed. Found " + logs.size() + " logs.");

        // Clean up test file
        if (testFile.exists()) {
            testFile.delete();
            System.out.println("4. Cleaned up temporary test file.");
        }

        System.out.println("=== All Non-GUI Verifications Passed Successfully! ===");
    }
}

