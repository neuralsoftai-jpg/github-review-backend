package com.codereview.githubreview.repository;

import com.codereview.githubreview.entity.CodeReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CodeReviewRepository extends JpaRepository<CodeReview, Long> {

    // 1. Sirf ek specific user ke saare reviews laane ke liye
    List<CodeReview> findByUserEmailOrderByIdDesc(String userEmail);

    // 2. IDOR protection: Review ID ke sath verify karna ki woh usi user ka hai
    Optional<CodeReview> findByIdAndUserEmail(Long id, String userEmail);

    // 3. Sirf us specific user ka count nikalne ke liye
    long countByUserEmail(String userEmail);
}