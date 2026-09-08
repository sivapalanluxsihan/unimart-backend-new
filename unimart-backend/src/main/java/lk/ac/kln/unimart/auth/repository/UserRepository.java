package lk.ac.kln.unimart.auth.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import lk.ac.kln.unimart.auth.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUniversityEmail(String universityEmail);

    boolean existsByUniversityEmail(String universityEmail);
}
