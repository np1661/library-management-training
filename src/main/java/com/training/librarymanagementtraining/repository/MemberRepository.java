package com.training.librarymanagementtraining.repository;

import com.training.librarymanagementtraining.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MemberRepository extends JpaRepository<Member, Long> {

    List<Member> findByNameContainingIgnoreCase(String name);

    boolean existsByEmail(String email);

}