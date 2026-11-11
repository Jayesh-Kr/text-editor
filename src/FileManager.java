import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * FileManager handles reading and writing files.
 * Demonstrates:
 * - Unit 3: Byte Stream classes (FileInputStream, FileOutputStream, InputStream, OutputStream)
 * - Unit 2: Exception handling (try-catch-finally, IOException)
 * - Unit 5: Generics (returning OperationResult<T>)
 */
public class FileManager {

    /**
     * Reads text content from a file using FileInputStream (Byte Stream).
     * 
     * @param file The file to read.
     * @return OperationResult containing the file text on success, or error message.
     */
    public static OperationResult<String> readFile(File file) {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            byte[] data = new byte[(int) file.length()];
            fis.read(data);
            String text = new String(data, "UTF-8");
            return new OperationResult<>(true, text, "File loaded successfully.");
        } catch (IOException e) {
            // Unit 2: Exception handling
            return new OperationResult<>(false, null, "Failed to read file: " + e.getMessage());
        } finally {
            // Clean up stream resources using finally block (Unit 2)
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    System.err.println("Error closing input stream: " + e.getMessage());
                }
            }
        }
    }

    /**
     * Writes text content to a file using FileOutputStream (Byte Stream).
     * 
     * @param file The file to write to.
     * @param content The text content to write.
     * @return OperationResult indicating success or failure.
     */
    public static OperationResult<Boolean> writeFile(File file, String content) {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            byte[] data = content.getBytes("UTF-8");
            fos.write(data);
            return new OperationResult<>(true, true, "File saved successfully.");
        } catch (IOException e) {
            // Unit 2: Exception handling
            return new OperationResult<>(false, false, "Failed to write file: " + e.getMessage());
        } finally {
            // Clean up stream resources using finally block (Unit 2)
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    System.err.println("Error closing output stream: " + e.getMessage());
                }
            }
        }
    }
}
