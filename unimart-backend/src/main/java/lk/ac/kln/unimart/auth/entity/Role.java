package lk.ac.kln.unimart.auth.entity;

/**
 * Stored as a VARCHAR(30) via @Enumerated(STRING) so the column stays
 * human-readable in ad-hoc SQL. See Guide 01 entity catalogue (User.role).
 */
public enum Role {
    STUDENT,
    ADMIN
}
