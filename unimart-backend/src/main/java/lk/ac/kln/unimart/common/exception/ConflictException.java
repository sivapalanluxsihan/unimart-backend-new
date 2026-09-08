package lk.ac.kln.unimart.common.exception;

/** Thrown when a request is valid but conflicts with the current state of a resource (e.g. duplicate review). Mapped to 409. */
public class ConflictException extends RuntimeException {
    public ConflictException(String message) {
        super(message);
    }
}
