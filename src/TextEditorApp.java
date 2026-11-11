import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * TextEditorApp - A simple Desktop Text Editor with Auto-Save.
 * 
 * Demonstrates Syllabus Topics:
 * - Unit 1: Classes, Objects, Inheritance (extends Application), static & final keywords, this reference.
 * - Unit 2: Polymorphism (overriding start()), Exception handling (try-catch-finally).
 * - Unit 3: Event Handling (Delegation Event Model, KeyEvent class), Multithreading (AutoSaveTask).
 * - Unit 4: Collections (ArrayList, List, Iterator), SQLite JDBC (via DatabaseHelper).
 * - Unit 5: JavaFX GUI Skeleton, MenuBar, Menu, MenuItem, ToolBar, TextArea, Label, BorderPane.
 * - Unit 5: Generics (OperationResult<T>, Wildcard method).
 */
public class TextEditorApp extends Application {

    // Constants (Unit 1: static and final keywords)
    private static final String APP_TITLE = "Simple Text Editor";
    private static final int AUTOSAVE_INTERVAL_SECONDS = 15;

    // GUI Controls (Unit 5: Exploring JavaFX Controls)
    private TextArea textArea;
    private Label statusLabel;
    private Label positionLabel;
    private Stage primaryStage;

    // Document State
    private File currentFile = null;
    private boolean isModified = false;

    // Collections (Unit 4: Collection Classes - ArrayList, List)
    private final List<String> recentFiles = new ArrayList<>();

    // Background Thread (Unit 3: Multithreading)
    private AutoSaveTask autoSaveTask;
    private Thread autoSaveThread;

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;

        // 1. Root Layout (Unit 5: JavaFX Layouts)
        BorderPane root = new BorderPane();

        // 2. Create Menus & Toolbar (Unit 5: MenuBar, Menu, MenuItem, Creating Menu and Toolbar)
        MenuBar menuBar = createMenuBar();
        ToolBar toolBar = createToolBar();
        VBox topContainer = new VBox(menuBar, toolBar);
        root.setTop(topContainer);

        // 3. Central Text Area (Unit 5: TextArea)
        textArea = new TextArea();
        textArea.setWrapText(true);

        // Event Handling: Listen for text changes to track dirty state (Unit 3)
        textArea.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!isModified) {
                isModified = true;
                updateTitle();
            }
        });

        // Event Handling: KeyEvent handling to track caret line & column (Unit 3: KeyEvent class)
        textArea.addEventHandler(KeyEvent.KEY_RELEASED, event -> updateCaretPosition());
        textArea.setOnMouseClicked(event -> updateCaretPosition());

        root.setCenter(textArea);

        // 4. Bottom Status Bar (Unit 5: JavaFX Label & HBox layout)
        statusLabel = new Label("Ready | Auto-save enabled (" + AUTOSAVE_INTERVAL_SECONDS + "s)");
        positionLabel = new Label("Ln 1, Col 1");
        
        HBox statusBar = new HBox(statusLabel, new Label(" | "), positionLabel);
        statusBar.setStyle("-fx-padding: 6px; -fx-background-color: #f2f2f2; -fx-font-size: 12px;");
        HBox.setHgrow(statusLabel, Priority.ALWAYS);
        root.setBottom(statusBar);

        // 5. Initialize & Start Auto-Save Thread (Unit 3: Java Thread Model & Life Cycle)
        startAutoSaveThread();

        // 6. Configure Stage & Scene (Unit 5: JavaFX Application Skeleton)
        Scene scene = new Scene(root, 720, 520);
        primaryStage.setTitle(APP_TITLE + " - Untitled");
        primaryStage.setScene(scene);

        // Handle window close event to cleanly stop background thread
        primaryStage.setOnCloseRequest(event -> {
            if (autoSaveTask != null) {
                autoSaveTask.stop();
            }
            if (autoSaveThread != null) {
                autoSaveThread.interrupt();
            }
        });

        primaryStage.show();
    }

    /**
     * Builds the MenuBar containing File and History menus.
     * Demonstrates Unit 5 Menus & Unit 3 Event Delegation.
     */
    private MenuBar createMenuBar() {
        MenuBar menuBar = new MenuBar();

        // --- File Menu ---
        Menu fileMenu = new Menu("File");

        MenuItem newItem = new MenuItem("New");
        newItem.setAccelerator(KeyCombination.keyCombination("Ctrl+N"));

        MenuItem openItem = new MenuItem("Open...");
        openItem.setAccelerator(KeyCombination.keyCombination("Ctrl+O"));

        MenuItem saveItem = new MenuItem("Save");
        saveItem.setAccelerator(KeyCombination.keyCombination("Ctrl+S"));

        MenuItem saveAsItem = new MenuItem("Save As...");
        MenuItem exitItem = new MenuItem("Exit");

        // Event Handling: Action Listeners using Delegation Event Model (Unit 3)
        newItem.setOnAction(e -> handleNew());
        openItem.setOnAction(e -> handleOpen());
        saveItem.setOnAction(e -> handleSave());
        saveAsItem.setOnAction(e -> handleSaveAs());
        exitItem.setOnAction(e -> handleExit());

        fileMenu.getItems().addAll(newItem, openItem, saveItem, saveAsItem, new SeparatorMenuItem(), exitItem);

        // --- History Menu (Demonstrates Unit 4 Collections & Iterator) ---
        Menu historyMenu = new Menu("History");
        MenuItem showHistoryItem = new MenuItem("Recent Files & Logs");
        showHistoryItem.setOnAction(e -> showRecentFilesDialog());
        historyMenu.getItems().add(showHistoryItem);

        menuBar.getMenus().addAll(fileMenu, historyMenu);
        return menuBar;
    }

    /**
     * Builds the Toolbar with quick action buttons.
     * Demonstrates Unit 5: "Creating Menu and Toolbar".
     */
    private ToolBar createToolBar() {
        Button newBtn = new Button("New");
        Button openBtn = new Button("Open");
        Button saveBtn = new Button("Save");

        newBtn.setOnAction(e -> handleNew());
        openBtn.setOnAction(e -> handleOpen());
        saveBtn.setOnAction(e -> handleSave());

        return new ToolBar(newBtn, openBtn, saveBtn);
    }

    /**
     * Handles "New" menu action: resets editor for a fresh document.
     */
    private void handleNew() {
        textArea.clear();
        currentFile = null;
        isModified = false;
        updateTitle();
        updateStatus("New document created.");
        updateCaretPosition();
    }

    /**
     * Handles "Open" menu action: opens existing text file via FileChooser.
     * Demonstrates Unit 3 Stream classes, Unit 5 Generics, & Unit 2 Exception Handling.
     */
    private void handleOpen() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Text File");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Text Files (*.txt)", "*.txt", "*.*")
        );

        File selectedFile = fileChooser.showOpenDialog(primaryStage);
        if (selectedFile != null) {
            // Read using Byte Stream and Generics (Unit 3 & Unit 5)
            OperationResult<String> result = FileManager.readFile(selectedFile);
            if (result.isSuccess()) {
                textArea.setText(result.getData());
                currentFile = selectedFile;
                isModified = false;
                updateTitle();

                // Add to recent files Collection (Unit 4)
                recentFiles.add(selectedFile.getAbsolutePath());

                updateStatus("Opened: " + selectedFile.getName());
                updateCaretPosition();
            } else {
                showError("File Error", result.getMessage());
            }
        }
    }

    /**
     * Handles "Save" action: writes to current file or prompts Save As.
     */
    private void handleSave() {
        if (currentFile == null) {
            handleSaveAs();
        } else {
            saveToFile(currentFile);
        }
    }

    /**
     * Handles "Save As..." action: prompts user for target destination.
     */
    private void handleSaveAs() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Text File");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Text Files (*.txt)", "*.txt")
        );

        File targetFile = fileChooser.showSaveDialog(primaryStage);
        if (targetFile != null) {
            saveToFile(targetFile);
        }
    }

    /**
     * Saves content to file using FileManager and logs to SQLite database.
     */
    private void saveToFile(File file) {
        // Write using Byte Stream and Generics (Unit 3 & Unit 5)
        OperationResult<Boolean> result = FileManager.writeFile(file, textArea.getText());

        if (result.isSuccess()) {
            currentFile = file;
            isModified = false;
            updateTitle();

            // Log save in SQLite database (Unit 4)
            DatabaseHelper.logSave(file.getName());

            // Add to recent files collection (Unit 4)
            if (!recentFiles.contains(file.getAbsolutePath())) {
                recentFiles.add(file.getAbsolutePath());
            }

            String timeStamp = new SimpleDateFormat("HH:mm:ss").format(new Date());
            updateStatus("Saved to " + file.getName() + " at " + timeStamp);
        } else {
            showError("Save Error", result.getMessage());
        }
    }

    /**
     * Displays recent files using Collection and Iterator (Unit 4).
     */
    private void showRecentFilesDialog() {
        StringBuilder sb = new StringBuilder();
        if (recentFiles.isEmpty()) {
            sb.append("No files opened in this session yet.\n\n");
        } else {
            sb.append("Files opened this session:\n");
            // Iterating through Collection via Iterator (Unit 4)
            Iterator<String> it = recentFiles.iterator();
            int count = 1;
            while (it.hasNext()) {
                sb.append(count++).append(". ").append(it.next()).append("\n");
            }
        }

        // Fetch recent saves from SQLite database
        List<String> dbLogs = DatabaseHelper.getRecentSaveLogs();
        if (!dbLogs.isEmpty()) {
            sb.append("\nRecent Save Logs (from SQLite DB):\n");
            for (String log : dbLogs) {
                sb.append("- ").append(log).append("\n");
            }
        }

        // Demonstrate Generic method with wildcard (Unit 5)
        OperationResult.displaySummary(recentFiles);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Recent Files & Logs");
        alert.setHeaderText("Session History");
        alert.setContentText(sb.toString());
        alert.showAndWait();
    }

    /**
     * Updates caret row and column indicator in status bar.
     */
    private void updateCaretPosition() {
        int caretPos = textArea.getCaretPosition();
        String text = textArea.getText();
        int line = 1;
        int column = 1;
        for (int i = 0; i < caretPos && i < text.length(); i++) {
            if (text.charAt(i) == '\n') {
                line++;
                column = 1;
            } else {
                column++;
            }
        }
        positionLabel.setText("Ln " + line + ", Col " + column);
    }

    /**
     * Exits the application cleanly.
     */
    private void handleExit() {
        if (autoSaveTask != null) {
            autoSaveTask.stop();
        }
        if (autoSaveThread != null) {
            autoSaveThread.interrupt();
        }
        Platform.exit();
    }

    /**
     * Starts the background auto-save thread.
     * Demonstrates Unit 3: Thread creation and execution.
     */
    private void startAutoSaveThread() {
        autoSaveTask = new AutoSaveTask(this, AUTOSAVE_INTERVAL_SECONDS);
        autoSaveThread = new Thread(autoSaveTask, "AutoSaveThread");
        autoSaveThread.setDaemon(true); // Daemon thread exits when main application exits
        autoSaveThread.start();
    }

    private void updateTitle() {
        String fileName = (currentFile != null) ? currentFile.getName() : "Untitled";
        String dirtyMarker = isModified ? " *" : "";
        primaryStage.setTitle(APP_TITLE + " - " + fileName + dirtyMarker);
    }

    public void updateStatus(String message) {
        statusLabel.setText(message);
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Getters and Setters for AutoSaveTask
    public File getCurrentFile() {
        return currentFile;
    }

    public String getTextContent() {
        return textArea.getText();
    }

    public boolean isModified() {
        return isModified;
    }

    public void setModified(boolean modified) {
        this.isModified = modified;
        Platform.runLater(this::updateTitle);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
