# Student Presentation & Viva Guide: Desktop Text Editor with Auto-Save
**Course:** Advanced JAVA Programming (CSE 307)

This guide is designed to help you explain your project step by step to your faculty during lab evaluations, CLAs, or viva exams.

---

## 1. High-Level Summary of the Project

> "We have developed a basic Desktop Text Editor with Auto-Save in Java using JavaFX. It replicates the core functionality of a simple Notepad (New, Open, Save, and Edit), while running a background thread that periodically checks for unsaved changes and saves them automatically to disk without freezing the user interface."

---

## 2. Topic-by-Topic Viva Q&A

### Question 1: How did you implement Multithreading for Auto-Save? (Unit 3)
* **Answer:**
  - We implemented the `Runnable` interface in [`AutoSaveTask.java`](file:///d:/CSE/3rd%20year/java/text-editor/src/AutoSaveTask.java).
  - Inside the `run()` method, a `while(running)` loop runs continuously in the background.
  - It pauses for 15 seconds using `Thread.sleep(15000)` (demonstrating the Thread Life Cycle and Timed Waiting state).
  - When the thread wakes up, it checks if a file is open and has unsaved changes (`app.isModified()`).
  - If modified, it writes the data to the file using `FileManager.writeFile()` and resets the modified flag.
  - The thread is marked as a **daemon thread** (`autoSaveThread.setDaemon(true)`), ensuring it automatically terminates when the main JavaFX application window is closed.

### Question 2: Why do you need `Platform.runLater()` in the background thread? (Unit 3 & Unit 5)
* **Answer:**
  - In JavaFX, only the **JavaFX Application Thread** is allowed to manipulate UI nodes (like updating labels or text fields).
  - Because `AutoSaveTask` runs on a separate worker thread, directly modifying the UI would throw an `IllegalStateException`.
  - Calling `Platform.runLater(() -> app.updateStatus(...))` safely queues the status update to be executed on the JavaFX UI thread.

### Question 3: How is File Handling implemented using Byte Streams? (Unit 3)
* **Answer:**
  - In [`FileManager.java`](file:///d:/CSE/3rd%20year/java/text-editor/src/FileManager.java), we use byte stream classes:
    - **`FileInputStream`** to read the raw bytes of the text file from disk into a `byte[]` array, which is then converted into a `String`.
    - **`FileOutputStream`** to convert the `String` into bytes (`content.getBytes("UTF-8")`) and write them to the file.
  - We use standard `try-catch-finally` blocks to guarantee that the stream objects (`fis` and `fos`) are closed in the `finally` block even if an exception occurs.

### Question 4: Where is Exception Handling used? (Unit 2)
* **Answer:**
  - **`try-catch-finally`:** Used in `FileManager.java` to handle `IOException` when reading or writing files.
  - **`InterruptedException`:** Caught in `AutoSaveTask.java` when the sleeping background thread is interrupted during application shutdown.
  - **`SQLException` & `ClassNotFoundException`:** Caught in `DatabaseHelper.java` to handle database connectivity issues without crashing the editor.

### Question 5: How are Collections and Iterators used? (Unit 4)
* **Answer:**
  - In [`TextEditorApp.java`](file:///d:/CSE/3rd%20year/java/text-editor/src/TextEditorApp.java), we use `List<String> recentFiles = new ArrayList<>()` to track opened files during the session.
  - In `showRecentFilesDialog()`, we use `Iterator<String> it = recentFiles.iterator()` to traverse the list and display the history to the user.

### Question 6: Where is JDBC used and what operations does it perform? (Unit 4)
* **Answer:**
  - In [`DatabaseHelper.java`](file:///d:/CSE/3rd%20year/java/text-editor/src/DatabaseHelper.java):
    - **Connection:** Established via `DriverManager.getConnection("jdbc:sqlite:texteditor.db")`.
    - **Table Creation (DDL):** Executed using `Statement.execute("CREATE TABLE IF NOT EXISTS save_logs ...")`.
    - **Insert (DML):** Executed using a parameterized `PreparedStatement` to log the filename and current timestamp whenever a manual save or auto-save occurs.
    - **Retrieve (DQL):** Executed using `Statement.executeQuery("SELECT ... FROM save_logs")` and reading from `ResultSet`.

### Question 7: How are Generics implemented? (Unit 5)
* **Answer:**
  - We created a generic class [`OperationResult<T>`](file:///d:/CSE/3rd%20year/java/text-editor/src/OperationResult.java).
  - The type parameter `T` allows the class to wrap different types of operation data (e.g. `OperationResult<String>` for file reads and `OperationResult<Boolean>` for file writes).
  - We also included a generic method with a wildcard parameter: `public static void displaySummary(List<?> items)`.

### Question 8: How does Event Handling work in the application? (Unit 3 & Unit 5)
* **Answer:**
  - We follow the **Delegation Event Model**:
    - The event sources are `MenuItem` components (`newItem`, `openItem`, `saveItem`, etc.).
    - Event listeners are attached using `setOnAction(e -> handleSave())`.
    - For real-time modification tracking, a change listener is attached to `textArea.textProperty().addListener(...)`, triggering dirty state whenever text is typed or deleted.

