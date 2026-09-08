package lk.ac.kln.unimart.auth.service;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lk.ac.kln.unimart.auth.dto.AuthResponse;
import lk.ac.kln.unimart.auth.dto.LoginRequest;
import lk.ac.kln.unimart.auth.dto.RegisterRequest;
import lk.ac.kln.unimart.auth.entity.Role;
import lk.ac.kln.unimart.auth.entity.User;
import lk.ac.kln.unimart.auth.repository.UserRepository;
import lk.ac.kln.unimart.common.exception.ConflictException;
import lk.ac.kln.unimart.security.JwtService;

/**
 * Registration and login. This is a minimal implementation of what Guide 07
 * assumes already exists from Guide 06 (POST /auth/register, POST
 * /auth/login). The JWT subject is the user's university email, which
 * matches Guide 07's use of {@code authentication.getName()} for ownership
 * checks in ListingService/ReviewService.
 */
@Service
public class AuthService {

    private final UserRepository users;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository users, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.users = users;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (users.existsByUniversityEmail(request.universityEmail())) {
            throw new ConflictException("An account with this university email already exists");
        }

        User user = new User(
                request.universityEmail().trim().toLowerCase(),
                passwordEncoder.encode(request.password()),
                request.fullName().trim(),
                Role.STUDENT);
        users.save(user);

        String token = jwtService.generateAccessToken(user.getUniversityEmail(), user.getRole().name());
        return AuthResponse.bearer(token);
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        User user = users.findByUniversityEmail(request.email().trim().toLowerCase())
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new BadCredentialsException("Invalid email or password");
        }

        String token = jwtService.generateAccessToken(user.getUniversityEmail(), user.getRole().name());
        return AuthResponse.bearer(token);
    }
}
