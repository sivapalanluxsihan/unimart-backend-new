package lk.ac.kln.unimart.listing.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import lk.ac.kln.unimart.listing.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    // Used when creating/updating a listing so archived/inactive categories
    // can't be assigned. See Guide 07 Part D.
    Optional<Category> findByIdAndActiveTrue(Long id);
}
