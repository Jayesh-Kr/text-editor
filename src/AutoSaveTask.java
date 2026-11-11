import javafx.application.Platform;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * AutoSaveTask runs in a separate background thread.
 * Demonstrates:
 * - Unit 3: Multithreading (implementing Runnable, Thread life cycle, Thread.sleep())
 * - Unit 2: Exception handling (try-catch with InterruptedException)
 */
public class AutoSaveTask implements Runnable {

    private final TextEditorApp app;
    private final int intervalSeconds;
    private volatile boolean running = true;

    // Constructor using 'this' reference (Unit 1)
    public AutoSaveTask(TextEditorApp app, int intervalSeconds) {
        this.app = app;
        this.intervalSeconds = intervalSeconds;
    }

    public void stop() {
        this.running = false;
    }

    @Override
    public void run() {
        // Background thread execution loop (Unit 3: Multithreading)
        while (running) {
            try {
                // Sleep for the specified duration (Unit 3: Thread life cycle)
                Thread.sleep(intervalSeconds * 1000L);

                // Auto-save only if a file is already opened/saved and has unsaved changes
                if (running && app.isModified() && app.getCurrentFile() != null) {
                    File file = app.getCurrentFile();
                    String text = app.getTextContent();

                    // Perform file writing via Byte Streams
                    OperationResult<Boolean> result = FileManager.writeFile(file, text);

                    if (result.isSuccess()) {
                        // Reset modified flag
                        app.setModified(false);

                        // Record to database if available
                        DatabaseHelper.logSave(file.getName());

                        // Safely update JavaFX UI on the JavaFX Application Thread
                        String timeStamp = new SimpleDateFormat("HH:mm:ss").format(new Date());
                        Platform.runLater(() -> {
                            app.updateStatus("Auto-saved successfully at " + timeStamp);
                        });
                    } else {
                        Platform.runLater(() -> {
                            app.updateStatus("Auto-save failed: " + result.getMessage());
                        });
                    }
                }
            } catch (InterruptedException e) {
                // Unit 2 & 3: Thread interruption handling during shutdown
                break;
            } catch (Exception e) {
                Platform.runLater(() -> {
                    app.updateStatus("Auto-save unexpected error: " + e.getMessage());
                });
            }
        }
    }
}
