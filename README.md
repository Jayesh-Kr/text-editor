# Desktop Text Editor with Auto-Save (Java / JavaFX)

A clean, beginner-friendly desktop text editor with automatic periodic saving, built strictly based on the **CSE 307 (Advanced Java Programming)** syllabus.

---

## 📚 Syllabus Topics Demonstrated

| Syllabus Unit | Concept / Topic | How It Is Implemented |
|---|---|---|
| **Unit 1: Overview of Java & OOP** | Classes, Objects, Inheritance (`extends`), `this`, `static`, `final`, Encapsulation | Main application structure; `TextEditorApp` extends `Application`; constants declared `static final`; clean object-oriented architecture. |
| **Unit 2: Polymorphism & Exception Handling** | Dynamic method dispatch / overriding (`@Override`), `try-catch-finally` blocks, built-in exceptions (`IOException`, `InterruptedException`) | Overrides `Application.start()` and `Runnable.run()`; handles file read/write and thread sleep errors cleanly with `try-catch-finally`. |
| **Unit 3: Multithreading & Event Handling** | `Runnable` interface, Thread life cycle, `Thread.sleep()`, Byte Streams (`FileInputStream`, `FileOutputStream`), Delegation Event Model | **Auto-Save Engine:** Background daemon thread runs periodically every 15 seconds to auto-save open files.<br>**File I/O:** Reading and writing bytes via `FileInputStream` and `FileOutputStream`.<br>**Event Handling:** Menu actions handled via `setOnAction()`. |
| **Unit 4: Collections & JDBC** | `List`, `ArrayList`, `Iterator`, SQLite JDBC (`Connection`, `Statement`, `ResultSet`) | **Collections:** `ArrayList<String>` tracks opened files and save records, accessed via `Iterator`.<br>**JDBC:** `DatabaseHelper` logs save events to an SQLite database (`texteditor.db`). |
| **Unit 5: Generics & JavaFX** | JavaFX Application Skeleton, `MenuBar`, `Menu`, `MenuItem`, `TextArea`, `Label`, `BorderPane`, `FileChooser`, Generic Classes (`class OperationResult<T>`), Wildcards | **GUI Layout:** Simple Notepad UI with File and History menus, central text editing area, and bottom auto-save status bar.<br>**Generics:** `OperationResult<T>` and generic methods with wildcard arguments (`List<?>`). |

---

## 📁 Project Structure

```
text-editor/
├── src/
│   ├── TextEditorApp.java       # JavaFX UI, MenuBar, Status Bar & Event Handling
│   ├── AutoSaveTask.java        # Background thread (Runnable) for automatic saving
│   ├── FileManager.java         # Byte Stream file reading & writing (FileInputStream / FileOutputStream)
│   ├── DatabaseHelper.java      # SQLite JDBC logging & Collections (ArrayList, Iterator)
│   └── OperationResult.java     # Generic class (OperationResult<T>) and wildcard methods
├── compile.bat                  # Batch script to compile the application
├── run.bat                      # Batch script to run the application
└── README.md                    # Project documentation
```

---

## 🚀 How to Run the Application

### Option A: Using Command Prompt / PowerShell

1. Download the **JavaFX SDK** for Windows (if not already installed) from:
   👉 [https://gluonhq.com/products/javafx/](https://gluonhq.com/products/javafx/)
2. Extract the JavaFX SDK to a folder (for example `C:\javafx-sdk-23\lib`).
3. Set your JavaFX library path:
   ```cmd
   set PATH_TO_FX="C:\javafx-sdk-23\lib"
   ```
4. Compile the project:
   ```cmd
   javac --module-path %PATH_TO_FX% --add-modules javafx.controls -d bin src\*.java
   ```
5. Run the application:
   ```cmd
   java --module-path %PATH_TO_FX% --add-modules javafx.controls -cp bin TextEditorApp
   ```

*(Optional: To enable SQLite database logging, also place `sqlite-jdbc.jar` in your classpath).*

---

### Option B: Running in an IDE (IntelliJ IDEA / Eclipse / VS Code)

1. Open this `text-editor` folder in your IDE.
2. Mark the `src` folder as the **Sources Root**.
3. Add the JavaFX library in project dependencies.
4. Run `TextEditorApp.java`.

---

## 📝 Features & User Guide

1. **Write & Edit Text**: Type directly into the central text area.
2. **New File**: Go to `File -> New` to clear the editor and start a new document.
3. **Open File**: Go to `File -> Open...` to browse and open any existing `.txt` file using `FileInputStream`.
4. **Save / Save As**: Go to `File -> Save` or `File -> Save As...` to write text to disk using `FileOutputStream`.
5. **Auto-Save**: Once a file is saved or opened, any subsequent edits are automatically detected and saved to disk in the background every 15 seconds without freezing the UI.
6. **History & Logs**: Go to `History -> Recent Files & Logs` to view files opened in the current session (using `ArrayList` and `Iterator`) and past save timestamps recorded in the SQLite database.

