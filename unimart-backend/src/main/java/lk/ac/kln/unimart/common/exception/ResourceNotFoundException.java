package lk.ac.kln.unimart.common.exception;

/** Thrown when a requested resource does not exist (or is not visible to the caller). Mapped to 404. */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
