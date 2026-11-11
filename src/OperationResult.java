/**
 * OperationResult - A simple generic class to hold the result of an operation.
 * 
 * Demonstrates:
 * - Unit 5: Introduction to Generic Class
 * - Unit 5: General Form of a Generic Class (class OperationResult<T>)
 * 
 * @param <T> The type of the data returned by the operation.
 */
public class OperationResult<T> {

    private final boolean success;
    private final T data;
    private final String message;

    // Constructor with 'this' reference (Unit 1)
    public OperationResult(boolean success, T data, String message) {
        this.success = success;
        this.data = data;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public T getData() {
        return data;
    }

    public String getMessage() {
        return message;
    }

    /**
     * Demonstrates a Generic Method with a Wildcard Argument (?) from Unit 5:
     * "Using Wildcard Arguments, Creating Generic Method"
     */
    public static void displaySummary(java.util.List<?> items) {
        for (Object item : items) {
            System.out.println("Log item: " + item);
        }
    }
}

