package lk.ac.kln.unimart.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank @Email @Size(max = 190) String universityEmail,
        @NotBlank @Size(min = 8, max = 72) String password,
        @NotBlank @Size(max = 120) String fullName
) {
}
