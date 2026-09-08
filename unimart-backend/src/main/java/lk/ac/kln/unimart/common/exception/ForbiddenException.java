package lk.ac.kln.unimart.common.exception;

/** Thrown when an authenticated caller is not allowed to act on a resource they don't own. Mapped to 403. */
public class ForbiddenException extends RuntimeException {
    public ForbiddenException(String message) {
        super(message);
    }
}
