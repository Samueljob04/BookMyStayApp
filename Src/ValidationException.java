/**
 * ValidationException - domain-specific exception for invalid booking inputs.
 */
public class ValidationException extends Exception {
    public ValidationException(String message) { super(message); }
}
